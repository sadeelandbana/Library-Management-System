package com.library.test;

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

/**
 * Comprehensive Unit and Integration tests for the LibraryService.
 * Focuses on core business logic, borrowing restrictions (US4.1, US4.2), 
 * and integrated Strategy/Observer patterns (US5.1, US3.3).
 */
@ExtendWith(MockitoExtension.class)
public class LibraryServiceTest {

    // @Spy allows us to test real methods of LibraryService while still using Mockito features
    @Spy 
    private LibraryService libraryService; 
    
    // @Mock allows us to simulate the EmailNotifier/Observer and verify notifications
    @Mock
    private Observer mockNotifier;

    private UserAccount admin;
    private UserAccount user1;
    private Book book1;
    private CD cd1;

    @BeforeEach
    void setUp() throws Exception { // تم تعديل التوقيع لإضافة throws Exception
        // Initialize the service Spy (this will call the constructor and initialize users/books)
        libraryService = spy(new LibraryService());
        
        // Fetch the default users initialized by the LibraryService constructor
        admin = libraryService.login("admin@lib.com", "admin");
        user1 = libraryService.login("user@lib.com", "user");
        
        // Since the constructor is now active, we only need to add the media items
        book1 = new Book("The Code", "A. Author", "ISBN-100");
        cd1 = new CD("Best Hits", "B. Artist", "ISBN-200");
        libraryService.addBook(book1);
        libraryService.addBook(cd1);
        
        // FIX: Inject the mockNotifier directly into the private 'internalNotifier' field using reflection.
        // This ensures the production code calls the mock when accessing the field directly.
        Field field = LibraryService.class.getDeclaredField("internalNotifier");
        field.setAccessible(true);
        field.set(libraryService, mockNotifier);
    }
    
    // ============================= 1. Borrowing & Due Date Tests =============================

    @Test
    void testBorrowBook_successAndDueDateCheck() {
        // Book: 28 days
        assertTrue(libraryService.borrowBook(user1, book1));
        Loan bookLoan = libraryService.getUserLoans(user1).stream().filter(l -> l.getItem().equals(book1)).findFirst().orElseThrow();
        assertEquals(LocalDate.now().plusDays(28), bookLoan.getDueDate(), "Book due date must be 28 days.");
        
        // CD: 7 days
        assertTrue(libraryService.borrowBook(user1, cd1));
        Loan cdLoan = libraryService.getUserLoans(user1).stream().filter(l -> l.getItem().equals(cd1)).findFirst().orElseThrow();
        assertEquals(LocalDate.now().plusDays(7), cdLoan.getDueDate(), "CD due date must be 7 days.");
    }

    @Test
    void testBorrowBook_alreadyBorrowedItem_failure() {
        libraryService.borrowBook(user1, book1);
        assertFalse(libraryService.borrowBook(admin, book1), "Borrowing a loaned item should fail.");
    }

    // ============================= 2. Borrowing Restriction Tests (US4.1) =============================
    
    @Test
    void testBorrowBook_userHasFine_failure() {
        user1.addFine(50.0); // Simulate pending fine
        assertFalse(libraryService.borrowBook(user1, book1), "Borrowing must fail due to pending fines (US4.1).");
    }
    
    @Test
    void testBorrowBook_userHasOverdueLoan_failure() {
        // Loan item and manually set it overdue
        libraryService.borrowBook(user1, cd1);
        Loan cdLoan = libraryService.getUserLoans(user1).stream().filter(l -> l.getItem().equals(cd1)).findFirst().orElseThrow();
        cdLoan.setDueDate(LocalDate.now().minusDays(1)); // Make it 1 day overdue
        
        assertFalse(libraryService.borrowBook(user1, book1), "Borrowing must fail due to having an overdue item (US4.1).");
    }


    // ============================= 3. Return & Fine Strategy/Observer Tests (US5.1 & US3.3) =============================

    @Test
    void testReturnBook_bookOverdue_correctFineAndNotified() {
        // Setup overdue loan
        libraryService.borrowBook(user1, book1);
        Loan loan = libraryService.getUserLoans(user1).stream().filter(l -> l.getItem().equals(book1) && !l.isReturned()).findFirst().orElseThrow();
        loan.setDueDate(LocalDate.now().minusDays(5)); // 5 days overdue
        
        // Return item
        libraryService.returnBook(user1, book1);
        
        // Assert Fine: 5 days * 10.0 = 50.0
        assertEquals(50.0, user1.getFineBalance(), 0.001, "Book fine calculation (10.0/day) failed.");
        
        // Verify Notification (Observer Pattern)
        String expectedMessagePart = "A fine of 50.00 has been charged";
        verify(mockNotifier, times(1)).notify(eq(user1), startsWith(expectedMessagePart));
    }

    @Test
    void testReturnBook_cdOverdue_correctFineAndNotified() {
        // Setup overdue loan
        libraryService.borrowBook(user1, cd1);
        Loan loan = libraryService.getUserLoans(user1).stream().filter(l -> l.getItem().equals(cd1) && !l.isReturned()).findFirst().orElseThrow();
        loan.setDueDate(LocalDate.now().minusDays(3)); // 3 days overdue
        
        // Return item
        libraryService.returnBook(user1, cd1);

        // Assert Fine: 3 days * 20.0 = 60.0
        assertEquals(60.0, user1.getFineBalance(), 0.001, "CD fine calculation (20.0/day) failed.");

        // Verify Notification (Observer Pattern)
        String expectedMessagePart = "A fine of 60.00 has been charged";
        verify(mockNotifier, times(1)).notify(eq(user1), startsWith(expectedMessagePart));
    }
    
    @Test
    void testReturnBook_onTime_noFineAndNoNotification() {
        libraryService.borrowBook(user1, book1);
        // Loan is returned today, no overdue days
        assertTrue(libraryService.returnBook(user1, book1));
        
        assertEquals(0.0, user1.getFineBalance(), 0.001, "No fine should be charged for on-time return.");
        verify(mockNotifier, never()).notify(eq(user1), anyString());
    }

    // ============================= 4. Unregister Restriction Tests (US4.2) =============================

    @Test
    void testUnregisterUser_activeLoan_failure() {
        libraryService.borrowBook(user1, book1);
        assertFalse(libraryService.unregisterUser(admin, user1.getId()), "Unregistering must fail due to active loan (US4.2).");
    }

    @Test
    void testUnregisterUser_pendingFine_failure() {
        // Generate a fine first
        libraryService.borrowBook(user1, book1);
        Loan loan = libraryService.getUserLoans(user1).stream().filter(l -> l.getItem().equals(book1) && !l.isReturned()).findFirst().orElseThrow();
        loan.setDueDate(LocalDate.now().minusDays(1)); 
        libraryService.returnBook(user1, book1); // User now has a fine
        
        assertTrue(user1.getFineBalance() > 0, "Fine must be present.");
        assertFalse(libraryService.unregisterUser(admin, user1.getId()), "Unregistering must fail due to pending fine (US4.2).");
    }
    
    @Test
    void testUnregisterUser_success() {
        // Needs a user without loans or fines
        UserAccount tempUser = new UserAccount("TEMP", "Temp User", "temp@del.com", "001", "del", "User");
        libraryService.registerUser(tempUser);
        
        assertTrue(libraryService.unregisterUser(admin, "TEMP"), "Unregistering a clean user should succeed.");
    }
}