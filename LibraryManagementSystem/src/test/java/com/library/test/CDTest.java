package com.library.test;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.library.model.CD;

public class CDTest {

    @Test
    public void testGetType() {
        CD cd = new CD("Greatest Hits", "Adele", "9876543210");
        assertEquals("CD", cd.getType(), "Type should be 'CD'");
    }

    @Test
    public void testGetArtist() {
        CD cd = new CD("Greatest Hits", "Adele", "9876543210");
        assertEquals("Adele", cd.getArtist(), "Artist should match");
    }

    @Test
    public void testToString() {
        CD cd = new CD("Greatest Hits", "Adele", "9876543210");
        assertEquals("Greatest Hits by Adele", cd.toString(), "toString should return 'Title by Artist'");
    }
}
