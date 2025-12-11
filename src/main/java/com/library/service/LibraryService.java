package com.library.service;

import com.library.model.Book;
import com.library.model.CD;
import com.library.model.Loan;
import com.library.model.UserAccount;
import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LibraryService {

    // ✅ FIXED BAD SMELL — duplicated literal "Admin"
    private static final String ADMIN_ROLE = "Admin";

    private final List<UserAccount> users = new ArrayList<>();
    private final List<Book> books = new ArrayList<>();
    private final List<Loan> loans = new ArrayList<>();
    private final FineStrategy bookFineStrategy = new BookFineStrategy();
    private final FineStrategy cdFineStrategy = new CDFineStrategy();
    private final Observer internalNotifier = new EmailNotifier(null, null);

    public LibraryService() {
        // Default admin
        users.add(new UserAccount("A1", ADMIN_ROLE, "admin@lib.com", "000", "admin", ADMIN_ROLE));
        // Default User
        users.add(new UserAccount("U1", "Test User", "user@lib.com", "111", "user", "User"));
    }

    public Observer getInternalNotifier() {
        return internalNotifier;
    }

    public boolean registerUser(UserAccount user) {
        for (UserAccount u : users) {
            if (u.getEmail().equalsIgnoreCase(user.getEmail()) || u.getId().equals(user.getId()))
                return false;
        }
        users.add(user);
        return true;
    }

    public UserAccount login(String email, String password) {
        for (UserAccount u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password))
                return u;
        }
        return null;
    }

    public boolean unregisterUser(UserAccount admin, String userId) {

        // ✅ Replaced "Admin" with constant
        if (!ADMIN_ROLE.equalsIgnoreCase(admin.getRole())) return false;

        UserAccount target = null;
        for (UserAccount u : users)
            if (u.getId().equals(userId)) target = u;

        // ✅ Replaced "Admin" with constant
        if (target == null || ADMIN_ROLE.equalsIgnoreCase(target.getRole())) return false;

        for (Loan l : loans)
            if (l.getUser().equals(target) && !l.isReturned())
                return false;

        if (target.getFineBalance() > 0)
            return false;

        users.remove(target);
        return true;
    }

    public void addBook(Book book) { books.add(book); }

    public List<Book> getBooks() { return books; }

    public Book findBookByIsbn(String isbn) {
        for (Book b : books)
            if (b.getIsbn().equalsIgnoreCase(isbn))
                return b;
        return null;
    }

    public boolean borrowBook(UserAccount user, Book item) {
        for (Loan l : loans)
            if (l.getItem().equals(item) && !l.isReturned())
                return false;

        if (user.getFineBalance() > 0)
            return false;

        for (Loan l : loans) {
            if (l.getUser().equals(user) && !l.isReturned()
                    && l.getDueDate().isBefore(LocalDate.now()))
                return false;
        }

        Loan newLoan = new Loan(user, item);
        if (item instanceof CD)
            newLoan.setDueDate(LocalDate.now().plusDays(7));
        else
            newLoan.setDueDate(LocalDate.now().plusDays(28));

        loans.add(newLoan);
        return true;
    }

    public boolean returnBook(UserAccount user, Book item) {
        for (Loan l : loans) {
            if (l.getUser().equals(user) && l.getItem().equals(item) && !l.isReturned()) {

                l.setReturned(true);
                LocalDate today = LocalDate.now();

                if (l.getDueDate().isBefore(today)) {
                    int overdueDays = (int) ChronoUnit.DAYS.between(l.getDueDate(), today);

                    FineStrategy fineStrategy =
                            (item instanceof CD) ? this.cdFineStrategy : this.bookFineStrategy;

                    double fine = fineStrategy.calculateFine(overdueDays);
                    user.addFine(fine);

                    String msg = String.format(
                            "A fine of %.2f has been charged to your account for returning '%s' %d days late.",
                            fine, item.getTitle(), overdueDays);

                    internalNotifier.notify(user, msg);
                }
                return true;
            }
        }
        return false;
    }

    public boolean payFine(UserAccount user, double amount) {
        if (amount <= 0 || amount > user.getFineBalance())
            return false;

        user.addFine(-amount);
        return true;
    }

    public List<Loan> getUserLoans(UserAccount user) {
        List<Loan> result = new ArrayList<>();
        for (Loan l : loans)
            if (l.getUser().equals(user))
                result.add(l);
        return result;
    }

    public List<Loan> getAllActiveLoans() {
        List<Loan> activeLoans = new ArrayList<>();
        for (Loan l : loans)
            if (!l.isReturned())
                activeLoans.add(l);
        return activeLoans;
    }

    public void sendOverdueReminders(Observer notifier) {
        Map<UserAccount, Integer> overdueCount = new HashMap<>();
        LocalDate today = LocalDate.now();

        for (Loan l : loans) {
            if (!l.isReturned() && l.getDueDate().isBefore(today)) {
                overdueCount.put(
                        l.getUser(),
                        overdueCount.getOrDefault(l.getUser(), 0) + 1
                );
            }
        }

        for (UserAccount u : overdueCount.keySet()) {
            String msg = "You have " + overdueCount.get(u)
                    + " overdue item(s). Please return or pay fines.";
            notifier.notify(u, msg);
        }
    }
}
