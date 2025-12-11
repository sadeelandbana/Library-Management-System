package tests;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.library.model.Book;

public class BookTest {

    @Test
    public void testGetters() {
        Book book = new Book("Java Basics", "Bana Aloul", "1234567890");

        assertEquals("Java Basics", book.getTitle(), "Title should match");
        assertEquals("Bana Aloul", book.getAuthor(), "Author should match");
        assertEquals("1234567890", book.getIsbn(), "ISBN should match");
        assertEquals("Book", book.getType(), "Type should be 'Book'");
    }

    @Test
    public void testToString() {
        Book book = new Book("Java Basics", "Bana Aloul", "1234567890");

        assertEquals("Java Basics by Bana Aloul", book.toString(), "toString should return 'Title by Author'");
    }
}