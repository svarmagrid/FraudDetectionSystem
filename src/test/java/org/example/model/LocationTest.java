package org.example.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {

    private Location createLocation(){
        return new Location(1, "Hyderabad", "Telangana", "India");
    }

    @Test
    void testShouldInitializeCorrectly() {
        Location location = createLocation();

        assertEquals(1, location.getLocationId());
        assertEquals("Hyderabad", location.getCity());
        assertEquals("Telangana", location.getState());
        assertEquals("India", location.getCountry());
    }

    @Test
    void testShouldHandlNullValues() {
        Location location = new Location(2, null, null, null);

        assertEquals(2, location.getLocationId());
        assertNull(location.getCity());
        assertNull(location.getState());
        assertNull(location.getCountry());
    }

    @Test
    void testEmptyStrings() {
        Location location = new Location(3, "", "", "");
        assertEquals(3, location.getLocationId());
        assertEquals("", location.getCity());
        assertEquals("", location.getState());
        assertEquals("", location.getCountry());
    }
}