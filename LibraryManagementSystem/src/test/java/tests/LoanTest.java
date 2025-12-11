package tests;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.UserAccount;

import java.time.LocalDate;

public class LoanTest {

    @Test
    public void testInitialLoanDates() {
        UserAccount user = new UserAccount("bana", "password", null, null, null, null); 
        Book book = new Book("Java Basics", "Bana Aloul", "1234567890");
        Loan loan = new Loan(user, book);

        assertEquals(user, loan.getUser(), "User should match");
        assertEquals(book, loan.getItem(), "Item should match");

        LocalDate today = LocalDate.now();
        assertEquals(today, loan.getBorrowDate(), "Borrow date should be today");
        assertEquals(today.plusDays(14), loan.getDueDate(), "Due date should be borrow date + 14 days");
        assertFalse(loan.isReturned(), "Returned should be false by default");
    }

    @Test
    public void testSetters() {
        UserAccount user = new UserAccount("bana", "password", null, null, null, null);
        Book book = new Book("Java Basics", "Bana Aloul", "1234567890");
        Loan loan = new Loan(user, book);

        LocalDate newBorrowDate = LocalDate.of(2025, 11, 1);
        LocalDate newDueDate = LocalDate.of(2025, 11, 15);

        loan.setBorrowDate(newBorrowDate);
        loan.setDueDate(newDueDate);
        loan.setReturned(true);

        assertEquals(newBorrowDate, loan.getBorrowDate(), "Borrow date should be updated");
        assertEquals(newDueDate, loan.getDueDate(), "Due date should be updated");
        assertTrue(loan.isReturned(), "Returned should be true after setting it");
    }
}