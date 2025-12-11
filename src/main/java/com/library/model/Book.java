package com.library.model;

//Represents a Book entity. 
//CD inherits from this class.
public class Book {
    protected String title;
    protected String author;
    protected String isbn;
    protected String type; // "Book" or "CD"

    public Book(String title, String author, String isbn){
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.type = "Book";
    }

    public String getTitle(){ 
    	return title;
    	}
    
    public String getAuthor(){ 
    	return author;
    	}
    
    public String getIsbn(){
    	return isbn;
    	}
    
    public String getType(){ 
    	return type;
    	}

    @Override
    public String toString(){ return title + " by " + author; }
}
