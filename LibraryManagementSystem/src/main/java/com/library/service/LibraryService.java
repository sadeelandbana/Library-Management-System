package com.library.service;

import com.library.model.Book;
import com.library.model.CD;
import com.library.model.Loan;
import com.library.model.UserAccount;
import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LibraryService {
    private final List<UserAccount> users = new ArrayList<>();
    private final List<Book> books = new ArrayList<>();
    private final List<Loan> loans = new ArrayList<>();
    private final FineStrategy bookFineStrategy = new BookFineStrategy();
    private final FineStrategy cdFineStrategy = new CDFineStrategy();
    private final Observer internalNotifier = new EmailNotifier(null, null); 

    public LibraryService() { 
        // Default admin
        users.add(new UserAccount("A1", "Admin", "admin@lib.com", "000", "admin", "Admin"));
        // Default User
        users.add(new UserAccount("U1", "Test User", "user@lib.com", "111", "user", "User"));
    }
    
    public Observer getInternalNotifier() {
        return internalNotifier;
    }

    //User management
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
        if (!"Admin".equalsIgnoreCase(admin.getRole())) return false;

        UserAccount target = null;
        for (UserAccount u : users)
            if (u.getId().equals(userId)) target = u;

        if (target == null || "Admin".equalsIgnoreCase(target.getRole())) return false;

        // US4.2: Cannot unregister if active loans or unpaid fines
        for (Loan l : loans)
            if (l.getUser().equals(target) && !l.isReturned())
                return false;

        if (target.getFineBalance() > 0)
            return false;

        users.remove(target);
        return true;
    }

    //Book / Media management

    public void addBook(Book book) {
        books.add(book);
    }

    public List<Book> getBooks() {
        return books;
    }

    public Book findBookByIsbn(String isbn) {
        for (Book b : books)
            if (b.getIsbn().equalsIgnoreCase(isbn))
                return b;
        return null;
    }

    //borrow and return

    public boolean borrowBook(UserAccount user, Book item) {
        // Cannot borrow if same item is already borrowed
        for (Loan l : loans)
            if (l.getItem().equals(item) && !l.isReturned())
                return false;

        // US4.1: Borrow restrictions - Cannot borrow if user has pending fines
        if (user.getFineBalance() > 0)
            return false;
            
        // US4.1: Borrow restrictions - Cannot borrow if user has any overdue items
        for (Loan l : loans) {
            if (l.getUser().equals(user) && !l.isReturned()
                            && l.getDueDate().isBefore(LocalDate.now()))
                return false;
        }

        // Determine due date based on item type (CD: 7 days, Book: 28 days)
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

                // Fine calculation using Strategy Pattern
                if (l.getDueDate().isBefore(today)) {
                    int overdueDays = (int) ChronoUnit.DAYS.between(l.getDueDate(), today);
                    
                    // Select Strategy based on item type
                    FineStrategy fineStrategy = (item instanceof CD) ? this.cdFineStrategy : this.bookFineStrategy; 
                    
                    double fine = fineStrategy.calculateFine(overdueDays);
                    user.addFine(fine);
                    
                    // Send notification email immediately upon fine charge
                    String msg = String.format("A fine of %.2f has been charged to your account for returning '%s' %d days late.", fine, item.getTitle(), overdueDays);
                    internalNotifier.notify(user, msg);
                }
                return true;
            }
        }
        return false;
    }

    //Fines 

    /**
     * Processes a fine payment.
     * @return true if payment is valid and processed, false otherwise.
     */
    public boolean payFine(UserAccount user, double amount) {
        if (amount <= 0 || amount > user.getFineBalance())
            return false;
            
        user.addFine(-amount);
        return true;
    }

    /**
     * Retrieves all loan history (active and returned) for a specific user.
     * @return A list of all Loans associated with the user.
     */
    public List<Loan> getUserLoans(UserAccount user) {
        List<Loan> result = new ArrayList<>();
        // Returns ALL loans (returned and not returned) for history view
        for (Loan l : loans)
            if (l.getUser().equals(user))
                result.add(l);
        return result;
    }
    
    /**
     * Retrieves all currently active (not returned) loans in the system (Admin view).
     * @return A list of all active Loan objects.
     */
    public List<Loan> getAllActiveLoans() {
        List<Loan> activeLoans = new ArrayList<>();
        for (Loan l : loans) {
            if (!l.isReturned()) {
                activeLoans.add(l);
            }
        }
        return activeLoans;
    }

    //REMINDERS

    /**
     * Sends overdue reminders to users with overdue items (US3.3).
     * This method remains for potential batch processes or testing.
     * @param notifier The Observer instance (e.g., EmailNotifier).
     */
    public void sendOverdueReminders(Observer notifier) {
        Map<UserAccount, Integer> overdueCount = new HashMap<>();
        LocalDate today = LocalDate.now();

        for (Loan l : loans) {
            if (!l.isReturned() && l.getDueDate().isBefore(today)) {
                overdueCount.put(l.getUser(),
                        overdueCount.getOrDefault(l.getUser(), 0) + 1);
            }
        }

        for (UserAccount u : overdueCount.keySet()) {
            String msg = "You have " + overdueCount.get(u)
                    + " overdue item(s). Please return or pay fines.";
            notifier.notify(u, msg);
        }
    }
}