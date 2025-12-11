package com.library;

import com.library.service.BookFineStrategy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for BookFineStrategy.
 * Verifies that the fine calculation for a Book is 10.0 per overdue day.
 */
public class BookFineStrategyTest {

    private final BookFineStrategy strategy = new BookFineStrategy();

    @Test
    void testCalculateFine_zeroDays() {
        // 0 days overdue should result in 0 fine
        assertEquals(0.0, strategy.calculateFine(0), 0.001, "Fine should be 0.0 for 0 days overdue.");
    }

    @Test
    void testCalculateFine_fiveDays() {
        // 5 days overdue: 5 * 10.0 = 50.0
        assertEquals(50.0, strategy.calculateFine(5), 0.001, "Fine should be 50.0 for 5 days overdue.");
    }

    @Test
    void testCalculateFine_largeDays() {
        // 15 days overdue: 15 * 10.0 = 150.0
        assertEquals(150.0, strategy.calculateFine(15), 0.001, "Fine should be 150.0 for 15 days overdue.");
    }

    @Test
    void testCalculateFine_negativeDays() {
        // A negative input (should not happen in real scenario) should return a negative fine
        assertEquals(-10.0, strategy.calculateFine(-1), 0.001, "Fine should handle negative days calculation.");
    }
}