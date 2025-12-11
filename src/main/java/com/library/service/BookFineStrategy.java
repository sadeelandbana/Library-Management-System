package com.library.service;

// Implements the fine calculation for a regular Book (10.0 per day).

public class BookFineStrategy implements FineStrategy {
    
    /**
     * Calculates the fine amount for a book.
     * @param overdueDays The number of days the item is late.
     * @return The calculated fine amount (double).
     */
    @Override
    public double calculateFine(int overdueDays){
        // Fine is 10.0 per day for Books
        return 10.0 * overdueDays;
    }
}