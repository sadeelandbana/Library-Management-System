package com.library.model;

//CD inherits from Book.

public class CD extends Book {

    public CD(String title, String artist, String isbn){
        super(title, artist, isbn);
        this.type = "CD";
    }

    public String getArtist(){ 
    	return author;
    	} // author stores the artist
}
