package tests;

import com.library.model.*;
import com.library.service.LibraryService;
import com.library.service.Observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LibraryServiceTest {

    @Spy
    private LibraryService libraryService;

    @Mock
    private Observer mockNotifier;

    private UserAccount admin;
    private UserAccount user1;
    private Book book1;
    private CD cd1;

    @BeforeEach
    void setUp() throws Exception {
        libraryService = spy(new LibraryService());

        admin = libraryService.login("admin@lib.com", "admin");
        user1 = libraryService.login("user@lib.com", "user");

        book1 = new Book("The Code", "A. Author", "ISBN-100");
        cd1 = new CD("Best Hits", "B. Artist", "ISBN-200");

        libraryService.addBook(book1);
        libraryService.addBook(cd1);

        Field field = LibraryService.class.getDeclaredField("internalNotifier");
        field.setAccessible(true);
        field.set(libraryService, mockNotifier);
    }

    // -------------------- Borrow Tests --------------------

    @Test
    void testBorrowBook_successAndDueDateCheck() {
        assertTrue(libraryService.borrowBook(user1, book1));
        Loan bookLoan = libraryService.getUserLoans(user1).stream()
                .filter(l -> l.getItem().equals(book1)).findFirst().orElseThrow();
        assertEquals(LocalDate.now().plusDays(28), bookLoan.getDueDate());

        assertTrue(libraryService.borrowBook(user1, cd1));
        Loan cdLoan = libraryService.getUserLoans(user1).stream()
                .filter(l -> l.getItem().equals(cd1)).findFirst().orElseThrow();
        assertEquals(LocalDate.now().plusDays(7), cdLoan.getDueDate());
    }

    @Test
    void testBorrowBook_alreadyBorrowedItem_failure() {
        libraryService.borrowBook(user1, book1);
        assertFalse(libraryService.borrowBook(admin, book1));
    }

    @Test
    void testBorrowBook_userHasFine_failure() {
        user1.addFine(50.0);
        assertFalse(libraryService.borrowBook(user1, book1));
    }

    @Test
    void testBorrowBook_userHasOverdueLoan_failure() {
        libraryService.borrowBook(user1, cd1);
        Loan cdLoan = libraryService.getUserLoans(user1).stream()
                .filter(l -> l.getItem().equals(cd1)).findFirst().orElseThrow();
        cdLoan.setDueDate(LocalDate.now().minusDays(1));

        assertFalse(libraryService.borrowBook(user1, book1));
    }

    // -------------------- Return Tests --------------------

    @Test
    void testReturnBook_bookOverdue_correctFineAndNotified() {
        libraryService.borrowBook(user1, book1);
        Loan loan = libraryService.getUserLoans(user1).stream()
                .filter(l -> l.getItem().equals(book1)).findFirst().orElseThrow();
        loan.setDueDate(LocalDate.now().minusDays(5));

        libraryService.returnBook(user1, book1);

        assertEquals(50.0, user1.getFineBalance(), 0.001);
        verify(mockNotifier, times(1))
                .notify(eq(user1), startsWith("A fine of 50.00"));
    }

    @Test
    void testReturnBook_cdOverdue_correctFineAndNotified() {
        libraryService.borrowBook(user1, cd1);
        Loan loan = libraryService.getUserLoans(user1).stream()
                .filter(l -> l.getItem().equals(cd1)).findFirst().orElseThrow();
        loan.setDueDate(LocalDate.now().minusDays(3));

        libraryService.returnBook(user1, cd1);

        assertEquals(60.0, user1.getFineBalance(), 0.001);
        verify(mockNotifier, times(1))
                .notify(eq(user1), startsWith("A fine of 60.00"));
    }

    @Test
    void testReturnBook_onTime_noFineAndNoNotification() {
        libraryService.borrowBook(user1, book1);
        assertTrue(libraryService.returnBook(user1, book1));

        assertEquals(0.0, user1.getFineBalance(), 0.001);
        verify(mockNotifier, never()).notify(eq(user1), anyString());
    }

    // -------------------- Unregister Tests --------------------

    @Test
    void testUnregisterUser_activeLoan_failure() {
        libraryService.borrowBook(user1, book1);
        assertFalse(libraryService.unregisterUser(admin, user1.getId()));
    }

    @Test
    void testUnregisterUser_pendingFine_failure() {
        libraryService.borrowBook(user1, book1);
        Loan loan = libraryService.getUserLoans(user1).stream()
                .filter(l -> l.getItem().equals(book1)).findFirst().orElseThrow();

        loan.setDueDate(LocalDate.now().minusDays(1));
        libraryService.returnBook(user1, book1);

        assertTrue(user1.getFineBalance() > 0);
        assertFalse(libraryService.unregisterUser(admin, user1.getId()));
    }

    @Test
    void testUnregisterUser_success() {
        UserAccount tempUser = new UserAccount("TEMP", "Temp User", "temp@del.com", "001", "del", "User");
        libraryService.registerUser(tempUser);

        assertTrue(libraryService.unregisterUser(admin, "TEMP"));
    }

    // -------------------- NEW TESTS ADDED FOR COVERAGE --------------------

    @Test
    void testRegisterUser() {
        UserAccount newUser = new UserAccount("NEW1", "New User", "new@lib.com", "222", "pass", "User");
        assertTrue(libraryService.registerUser(newUser));

        UserAccount dupEmail = new UserAccount("XX1", "X", "new@lib.com", "333", "p", "User");
        assertFalse(libraryService.registerUser(dupEmail));

        UserAccount dupId = new UserAccount("NEW1", "X2", "other@lib.com", "444", "p", "User");
        assertFalse(libraryService.registerUser(dupId));
    }

    @Test
    void testLogin() {
        assertNotNull(libraryService.login("admin@lib.com", "admin"));
        assertNotNull(libraryService.login("user@lib.com", "user"));

        assertNull(libraryService.login("user@lib.com", "wrong"));
        assertNull(libraryService.login("unknown@lib.com", "pass"));
    }

    @Test
    void testPayFine() {
        user1.addFine(100);

        assertFalse(libraryService.payFine(user1, -10));
        assertFalse(libraryService.payFine(user1, 0));
        assertFalse(libraryService.payFine(user1, 200));

        assertTrue(libraryService.payFine(user1, 50));
        assertEquals(50, user1.getFineBalance());
    }

    @Test
    void testFindBookByIsbn() {
        assertEquals(book1, libraryService.findBookByIsbn("ISBN-100"));
        assertNull(libraryService.findBookByIsbn("UNKNOWN"));
    }

    @Test
    void testGetAllActiveLoans() {
        assertTrue(libraryService.getAllActiveLoans().isEmpty());

        libraryService.borrowBook(user1, book1);
        assertEquals(1, libraryService.getAllActiveLoans().size());

        libraryService.returnBook(user1, book1);
        assertTrue(libraryService.getAllActiveLoans().isEmpty());
    }

    @Test
    void testSendOverdueReminders() {
        libraryService.borrowBook(user1, book1);

        Loan loan = libraryService.getUserLoans(user1).get(0);
        loan.setDueDate(LocalDate.now().minusDays(3));

        libraryService.sendOverdueReminders(mockNotifier);

        verify(mockNotifier, times(1))
                .notify(eq(user1), contains("overdue"));
    }
}
