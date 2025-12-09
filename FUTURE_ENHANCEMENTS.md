# 🚀 BookHub Manager - Future Enhancements & Feature Roadmap

**Document Created:** December 9, 2025  
**Status:** Post-MVP Features (Ready for development after initial deployment)

---

## 📋 Top Priority Features

### 1. **🔄 Loan Renewal Limits** ⭐⭐⭐
**Difficulty:** Easy | **Time:** 30 min | **Impact:** High

**Description:** 
- Track number of renewals per loan (max 2 times)
- Show "2/2 renewals used" indicator on MyLoans page
- Disable renew button when limit reached
- Prevent abuse of renewal system

**Implementation:**
- Backend: Add `renewalCount` field to Loan entity, increment on each renewal
- Frontend: Show renewal counter on loan card, conditional button disabling
- Business logic: Check `renewalCount < 2` before allowing renewal

**Files to modify:**
- Backend: `Loan.java`, `LoanService.java`, `renewLoan()` method
- Frontend: `MyLoans.jsx`, display renewal count
- Frontend: `LibrarianLoans.jsx`, show counter

---

### 2. **📊 Enhanced Dashboard Statistics** ⭐⭐⭐
**Difficulty:** Easy | **Time:** 30 min | **Impact:** Medium

**Description:**
- Member Dashboard: Show lifetime stats
  - Total books borrowed
  - Books currently borrowed
  - Overdue loans count
  - Pending reservations count
  - Most borrowed book genres

**Implementation:**
- Frontend: Update `MemberDashboard.jsx` with stat cards
- Backend: Add `GET /users/{id}/statistics` endpoint if missing
- Services: Calculate stats in backend or frontend

**Files to modify:**
- Frontend: `MemberDashboard.jsx`, `AdminDashboard.jsx`, `LibrarianDashboard.jsx`
- Backend: `UserController.java`, `UserService.java` (add statistics method)

---

### 3. **🔍 Advanced Search & Filters** ⭐⭐⭐
**Difficulty:** Medium | **Time:** 1 hour | **Impact:** High

**Description:**
- Books page enhanced filters:
  - Genre filter (dropdown with multiple select)
  - Availability status (Available / Reserved / Unavailable)
  - Publication year range slider
  - Rating/Review filter (if reviews implemented)
  - Sort options (Newest, Most Popular, A-Z, Z-A)

**Implementation:**
- Frontend: Add filter panel UI to Books.jsx
- Frontend: Build filter query params
- Backend: Enhance `/books` endpoint with multiple filter parameters
- Database: Add query methods for filtered search

**Files to modify:**
- Frontend: `Books.jsx` (add filter UI and state)
- Backend: `BookController.java`, `BookRepository.java`, `BookService.java`

---

## 🔧 Medium Priority Features

### 4. **⭐ User Reviews & Ratings System** ⭐⭐
**Difficulty:** Hard | **Time:** 2 hours | **Impact:** High

**Description:**
- Members can leave 1-5 star ratings + text reviews
- Display average rating on book cards
- Show all reviews on BookDetails page
- Only allow members who borrowed the book to review

**Implementation:**
- Backend: Create `Review` entity (id, bookId, userId, rating, text, createdDate)
- Backend: Add review endpoints (POST, GET, DELETE)
- Frontend: Review form modal on BookDetails
- Frontend: Display reviews list with pagination

**Files to create:**
- Backend: `model/Review.java`, `dto/ReviewRequest.java`, `dto/ReviewResponse.java`, `repository/ReviewRepository.java`, `ReviewController.java`, `ReviewService.java`
- Frontend: `components/ReviewForm.jsx`, `components/ReviewList.jsx`

**Files to modify:**
- Frontend: `BookDetails.jsx` (add review section)

---

### 5. **💰 Fine/Penalty System** ⭐⭐
**Difficulty:** Medium | **Time:** 1-2 hours | **Impact:** High

**Description:**
- Charge fine for overdue loans (€0.50 per day)
- Track fines on user profile
- Payment tracking (paid/unpaid)
- Fine calculation automatic on overdue status

**Implementation:**
- Backend: Create `Fine` entity or add to `Loan`
- Backend: Calculate fines daily (scheduled task)
- Frontend: Display fines on Profile page
- Frontend: Option to mark as paid

**Files to create:**
- Backend: `model/Fine.java`, `FineService.java`, `FineController.java`
- Frontend: `components/FinesDisplay.jsx`

**Files to modify:**
- Frontend: `Profile.jsx` (add fines section)
- Backend: `Loan.java` (add fine tracking)

---

### 6. **📈 Librarian Analytics & Reports** ⭐⭐
**Difficulty:** Medium | **Time:** 1-2 hours | **Impact:** Medium

**Description:**
- Dashboard with charts and analytics:
  - Books borrowed this month (bar chart)
  - Most popular books (top 10)
  - Member borrowing patterns
  - Overdue trends
  - Revenue from fines (if fine system implemented)

**Implementation:**
- Frontend: Install Chart.js or Recharts library
- Backend: Create `/stats/librarian` endpoint with aggregated data
- Frontend: Create analytics page with visualizations

**Files to create:**
- Frontend: `pages/librarian/LibrarianAnalytics.jsx`
- Backend: `StatsController.java`, `StatsService.java`

**Libraries:**
- Frontend: `npm install recharts` (or Chart.js)

---

## 🔐 Advanced Features

### 7. **📧 Email Notifications** ⭐
**Difficulty:** Medium | **Time:** 1-2 hours | **Impact:** High

**Description:**
- Send email when:
  - Reservation is ready for pickup
  - Loan is about to expire (2 days before)
  - Loan becomes overdue
  - Renewal successful

**Implementation:**
- Backend: Configure Spring Mail (SMTP settings)
- Backend: Create `EmailService` with templates
- Backend: Send notifications async (use @Async)
- Frontend: Show success toast "Notification sent"

**Files to create:**
- Backend: `service/EmailService.java`
- Backend: Email templates (HTML files)

**Configuration:**
- Add SMTP settings to `application.properties`
- Configure Gmail/custom SMTP credentials

---

### 8. **🔔 Real-time WebSocket Notifications** ⭐
**Difficulty:** Hard | **Time:** 2-3 hours | **Impact:** Medium

**Description:**
- Real-time notifications using WebSocket
- Pop-up toast when:
  - Loan/reservation status changes
  - Book becomes available
  - Fine added

**Implementation:**
- Backend: Configure Spring WebSocket (stomp-websocket)
- Backend: Create notification endpoints
- Frontend: Install `stompjs` and `sockjs-client`
- Frontend: Connect to WebSocket on app load
- Frontend: Display toast notifications

**Files to create:**
- Backend: `config/WebSocketConfig.java`, `controller/NotificationController.java`
- Frontend: `hooks/useNotification.js`, `components/NotificationToast.jsx`

**Libraries:**
- Frontend: `npm install stompjs sockjs-client`

---

### 9. **🌙 Dark Mode** ⭐
**Difficulty:** Easy | **Time:** 30 min | **Impact:** Low-Medium

**Description:**
- Toggle dark/light theme
- Persist preference in localStorage
- Apply to all components
- Dark color scheme: grays + lighter text

**Implementation:**
- Frontend: Create `ThemeContext.jsx`
- Frontend: Create CSS custom properties for colors
- Frontend: Toggle button in navbar
- Frontend: Apply theme on app load

**Files to create:**
- Frontend: `context/ThemeContext.jsx`

**Files to modify:**
- Frontend: `Navbar.jsx` (add theme toggle button)
- Frontend: `App.jsx` (wrap with ThemeProvider)
- Frontend: `index.css` (add CSS variables)

---

### 10. **🔐 Two-Factor Authentication (2FA)** ⭐
**Difficulty:** Hard | **Time:** 3 hours | **Impact:** Medium

**Description:**
- OTP sent to email on login
- Verify OTP before granting access
- Option for SMS (requires Twilio API)
- Backup codes for account recovery

**Implementation:**
- Backend: Generate OTP, store in Redis (expiry 5 min)
- Backend: Email OTP to user
- Backend: Verify OTP endpoint
- Frontend: OTP input modal on login
- Frontend: Loading state during verification

**Libraries:**
- Backend: Apache Commons OTP or custom implementation
- Frontend: React OTP Input (npm install react-otp-input)

---

## 📅 Implementation Roadmap

### **Phase 1 (Week 1)** - Post-Launch Polish
1. Renewal limits ✓
2. Enhanced dashboard stats ✓
3. Advanced search filters ✓

### **Phase 2 (Week 2)** - User Experience
4. Reviews & ratings
5. Email notifications
6. Fine/penalty system

### **Phase 3 (Week 3)** - Analytics & Advanced
7. Librarian analytics
8. Dark mode
9. Real-time notifications

### **Phase 4 (Week 4+)** - Security & Scale
10. Two-factor authentication
11. Performance optimization
12. Mobile app (if needed)

---

## 🛠️ Quick Reference: Feature Dependencies

```
Renewal Limits
├── No dependencies (standalone)

Dashboard Stats
├── Requires: User statistics endpoint

Advanced Search
├── Requires: Enhanced backend query methods

Reviews & Ratings
├── Depends on: Nothing (standalone)
├── Used by: Book details page

Fine System
├── Depends on: Overdue loan detection
├── Used by: User profile, Admin dashboard

Analytics
├── Depends on: Aggregated data endpoints
├── Uses: Charts library (Recharts/Chart.js)

Email Notifications
├── Requires: SMTP configuration
├── Used by: All status change events

WebSocket Notifications
├── Depends on: Spring WebSocket config
├── Requires: Frontend Socket.io setup

Dark Mode
├── No dependencies (standalone)

2FA
├── Depends on: Email service
├── Used by: Login flow
```

---

## 📝 Notes for Development

### Testing Priority
1. Test renewal limits extensively (edge cases)
2. Test fine calculations with various overdue periods
3. Test notification delivery (especially emails)
4. Test WebSocket connection reliability

### Performance Considerations
- Dashboard stats: Consider caching (Redis)
- Analytics queries: Use database aggregation, not frontend
- Email notifications: Use async @Scheduled tasks
- WebSocket: Implement heartbeat to prevent disconnects

### Security Checklist
- Validate all filter inputs (prevent SQL injection)
- Rate limit email sending (prevent spam)
- Verify user owns the review before allowing delete
- Check user role before analytics access

### User Experience
- Show loading states during async operations
- Provide clear error messages
- Use toast notifications for confirmations
- Add confirmation dialogs for destructive actions

---

## 🎯 Success Metrics

After implementing these features:
- ✅ User engagement increases (more interactions)
- ✅ Fewer overdue loans (fine system deters)
- ✅ Better book discovery (advanced search)
- ✅ Librarian efficiency improves (analytics)
- ✅ User retention increases (notifications + engagement)

---

**Last Updated:** December 9, 2025  
**Status:** Ready for implementation post-deployment  
**Estimated Total Dev Time:** 15-20 hours (for all features)
