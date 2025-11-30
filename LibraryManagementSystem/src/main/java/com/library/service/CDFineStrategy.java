package com.library.service;

//Implements the fine calculation for a CD (20.0 per day).
 
public class CDFineStrategy implements FineStrategy {
    
    /**
     * Calculates the fine amount for a CD.
     * @param overdueDays The number of days the item is late.
     * @return The calculated fine amount (double).
     */
    @Override
    public double calculateFine(int overdueDays){
        // Fine is 20.0 per day for CDs
        return 20.0 * overdueDays;
    }
}