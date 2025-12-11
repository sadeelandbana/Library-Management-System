package com.library.model;

import java.time.LocalDate;

public class Loan {
    private UserAccount user;
    private Book item; // Book or CD
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private boolean returned = false;

    public Loan(UserAccount user, Book item){
        this.user = user;
        this.item = item;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(14);
    }

    // Getters
    public UserAccount getUser(){ 
    	return user;
    	}
    
    public Book getItem(){ 
    	return item;
    	}
    
    public LocalDate getBorrowDate(){ 
    	return borrowDate;
    	}
    
    public LocalDate getDueDate(){ 
    	return dueDate;
    	}
    
    public boolean isReturned(){ 
    	return returned; 
    	}

    // Setters for tests
    public void setBorrowDate(LocalDate date){
    	this.borrowDate = date;
    	}
    
    public void setDueDate(LocalDate date){ 
    	this.dueDate = date;
    	}
    
    public void setReturned(boolean r){ 
    	this.returned = r;
    	}
}
