package com.JohnBravos.bookhub_manager.mapper;

import com.JohnBravos.bookhub_manager.dto.Request.CreateLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Request.UpdateLoanRequest;
import com.JohnBravos.bookhub_manager.dto.Response.LoanResponse;
import com.JohnBravos.bookhub_manager.model.Loan;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LoanMapper {

    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public LoanMapper (UserMapper userMapper, BookMapper bookMapper) {
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

//    /**
//     * Μετατροπή από CreateLoanRequest -> Loan
//     */
//    public Loan toEntity(CreateLoanRequest request) {
//        return Loan.builder()
//                .dueDate(LocalDate.from(request.dueDate()))
//                // loanDate, returnDate, status θα τα ορίσουμε στο Service
//                .build();
//    }

    /**
     * Ενημέρωση Loan από UpdateLoanRequest
     */
    public void updateEntity(UpdateLoanRequest request, Loan loan) {
        if (request.dueDate() != null) {
            loan.setDueDate(request.dueDate());
        }
        // Σημείωση: Στα loans συνήθως αλλάζουμε μόνο το dueDate
        // Το returnDate και status τα ορίζει το system
    }

    /**
     * Μετατροπή από Loan -> LoanResponse
     */
    public LoanResponse toResponse(Loan loan) {
        if (loan == null) {
            return null;
        }

        // Calculate overdue status
        boolean isOverdue = loan.getDueDate().isBefore(ChronoLocalDate.from(LocalDateTime.now()))
                && loan.getStatus().toString().equals("ACTIVE");
        int daysOverdue = isOverdue ?
                (int) java.time.Duration.between(loan.getDueDate(), LocalDateTime.now()).toDays() : 0;

        return new LoanResponse(
                loan.getId(),
                bookMapper.toResponse(loan.getBook()),
                userMapper.toResponse(loan.getUser()),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus(),
                isOverdue,
                daysOverdue
        );
    }

    /**
     * Μετατροπή λίστας Loans -> LoanResponses
     */
    public List<LoanResponse> toResponseList(List<Loan> loans) {
        if (loans == null) {
            return List.of();
        }

        return loans.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
