package com.library;

import com.library.service.CDFineStrategy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for CDFineStrategy.
 * Verifies that the fine calculation for a CD is 20.0 per overdue day.
 */
public class CDFineStrategyTest {

    private final CDFineStrategy strategy = new CDFineStrategy();

    @Test
    void testCalculateFine_zeroDays() {
        // 0 days overdue should result in 0 fine
        assertEquals(0.0, strategy.calculateFine(0), 0.001, "Fine should be 0.0 for 0 days overdue.");
    }

    @Test
    void testCalculateFine_fiveDays() {
        // 5 days overdue: 5 * 20.0 = 100.0
        assertEquals(100.0, strategy.calculateFine(5), 0.001, "Fine should be 100.0 for 5 days overdue.");
    }

    @Test
    void testCalculateFine_largeDays() {
        // 15 days overdue: 15 * 20.0 = 300.0
        assertEquals(300.0, strategy.calculateFine(15), 0.001, "Fine should be 300.0 for 15 days overdue.");
    }
}