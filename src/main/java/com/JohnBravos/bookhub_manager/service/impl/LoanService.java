package com.JohnBravos.bookhub_manager.service.impl;


import com.JohnBravos.bookhub_manager.core.enums.LoanStatus;
import com.JohnBravos.bookhub_manager.core.exceptions.custom.*;
import com.JohnBravos.bookhub_manager.dto.Request.CreateLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Request.ReturnLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Response.LoanResponse;
import com.JohnBravos.bookhub_manager.mapper.LoanMapper;
import com.JohnBravos.bookhub_manager.model.Book;
import com.JohnBravos.bookhub_manager.model.Loan;
import com.JohnBravos.bookhub_manager.model.User;
import com.JohnBravos.bookhub_manager.repository.BookRepository;
import com.JohnBravos.bookhub_manager.repository.LoanRepository;
import com.JohnBravos.bookhub_manager.repository.UserRepository;
import com.JohnBravos.bookhub_manager.service.ILoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanService implements ILoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanMapper loanMapper;

    @Override
    @Transactional
    public LoanResponse createLoan(CreateLoanRequest request) {
        log.info("Creating new loan for user ID: {} and book ID: {}",
                request.userId(), request.bookId());

        // Validate user and book exist
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BookNotFoundException(request.bookId()));

        // Business Rules Validation
        validateLoanCreation(user, book);

        // Manual Builder for CREATE
        Loan loan = Loan.builder()
                .user(user)
                .book(book)
                .dueDate(request.dueDate())
                .build();

        // Business Logic
        loan.setLoanDate(LocalDate.now());
        loan.setStatus(LoanStatus.ACTIVE);

        // Update book availability
        book.borrowCopy();
        bookRepository.save(book);

        Loan savedLoan = loanRepository.save(loan);
        log.info("Loan created successfully with ID: {}", savedLoan.getId());

        return loanMapper.toResponse(savedLoan);
    }

    /**
     * Business Rules Validation for Loan Creation
     */
    private void validateLoanCreation(User user, Book book) {
        // Rule 1: Book must be available
        if (!book.isAvailable()) {
            throw new BookUnavailableException(
                    book.getTitle(),
                    book.getAvailableCopies(),
                    book.getTotalCopies()
            );
        }

        if (book.getAvailableCopies() <= 0) {
            throw new BadRequestException("No available copies for this book");
        }

        // Rule 2: User cannot have multiple active loans for the same book
        if (hasActiveLoan(user.getId(), book.getId())) {
            throw new LoanNotAllowedException("User has already an active loan for this book");
        }

        // Rule 3: User cannot have overdue loans
        boolean hasOverdueLoans = loanRepository
                .existsByUserIdAndDueDateBeforeAndStatus(user.getId(), LocalDate.now(), LoanStatus.ACTIVE);


        // Rule 4: Maximum active loans per user
        int activeLoansCount = getActiveLoansCountByUser(user.getId());
        if (activeLoansCount >= 5) {
            throw new LoanNotAllowedException("User has reached maximum active loans limit");
        }
    }

    @Override
    public LoanResponse getLoanById(Long id) {
        log.debug("Fetching loan by ID: {}", id);
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(id));
        return loanMapper.toResponse(loan);
    }

    @Override
    public List<LoanResponse> getAllLoans() {
        log.debug("Fetching all loans");
        return loanMapper.toResponseList(loanRepository.findAll());
    }

    @Override
    public List<LoanResponse> getLoansByUser(Long userId) {
        log.debug("Fetching loans for user ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return loanMapper.toResponseList(loanRepository.findByUserId(userId));
    }

    @Override
    public List<LoanResponse> getLoansByBook(Long bookId) {
        log.debug("Fetching loans for book ID: {}", bookId);
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }
        return loanMapper.toResponseList(loanRepository.findByBookId(bookId));
    }

    @Override
    public List<LoanResponse> getLoansByStatus(LoanStatus status) {
        log.debug("Fetching loans by status: {}", status);
        return loanMapper.toResponseList(loanRepository.findByStatus(status));
    }

    @Override
    public List<LoanResponse> getActiveLoans() {
        log.debug("Fetching active loans");
        return loanMapper.toResponseList(loanRepository.findByStatus(LoanStatus.ACTIVE));
    }

    @Override
    public List<LoanResponse> getOverdueLoans() {
        log.debug("Fetching overdue loans");
        return loanMapper.toResponseList(loanRepository.findOverdueLoans(LocalDate.now()));
    }

    public List<LoanResponse> getLoansDueSoon() {
        log.debug("Fetching loans due soon");
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(3);    // Due in next 3 days
        return loanMapper.toResponseList(loanRepository.findLoansDueSoon(startDate, endDate));
    }

    @Override
    @Transactional
    public LoanResponse updateLoan(Long loanId, UpdateLoanRequest request) {
        log.info("Updating loan with ID: {}", loanId);
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        // Only allow updating due date for active loans
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new LoanNotAllowedException("Can only update due date for active loans");
        }

        loan.setDueDate(request.dueDate());
        Loan updatedLoan = loanRepository.save(loan);

        log.info("Loan updated successfully with ID: {}", loanId);
        return loanMapper.toResponse(updatedLoan);
    }

    @Override
    @Transactional
    public LoanResponse returnLoan(ReturnLoanRequest request) {
        log.info("Returning loan with ID: {}", request.loanId());
        Loan loan = loanRepository.findById(request.loanId())
                .orElseThrow(() -> new LoanNotFoundException(request.loanId()));

        // Business Logic for returning book
        loan.setReturnDate(LocalDateTime.now());
        loan.setStatus(LoanStatus.RETURNED);

        // Update Book Availability
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        Loan returnedLoan = loanRepository.save(loan);
        log.info("Book returned successfully for loan ID: ", request.loanId());

        return loanMapper.toResponse(returnedLoan);
    }

    @Override
    @Transactional
    public void deleteLoan(Long loanId) {
        log.info("Deleting loan with ID: {}", loanId);
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        // Only allow deletion of returned loans
        if (loan.getStatus() != LoanStatus.RETURNED) {
            throw new LoanNotAllowedException("Can only delete returned loans");
        }

        loanRepository.delete(loan);
        log.info("Loan deleted successfully with ID: {}", loanId);
    }

    @Override
    public boolean loanExists(Long loanId) {
        return loanRepository.existsById(loanId);
    }

    @Override
    public boolean hasActiveLoan(Long userId, Long bookId) {
        return loanRepository.existsActiveLoanByUserAndBook(userId, bookId);
    }

    public int getActiveLoansCountByUser(Long userId) {
        return (int) loanRepository.countByUserAndStatus(
                userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(userId)),
                LoanStatus.ACTIVE
        );
    }
}
