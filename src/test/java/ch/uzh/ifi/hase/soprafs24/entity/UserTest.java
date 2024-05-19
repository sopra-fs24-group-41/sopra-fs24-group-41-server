package ch.uzh.ifi.hase.soprafs24.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;

    @BeforeEach
    void setup() {
        user = new User();

        user.setUsername("abcd");
        user.setPassword("1234");
        user.setId(1234L);
    }

    @Test
    void equals_returnsTrue() {
        User user2 = new User();
        user2.setUsername("abcd");
        user2.setPassword("1234");
        user2.setId(1234L);

        assertEquals(user, user2);
        assertEquals(user2, user);
    }

    @Test
    void notEqual_returnsFalse() {
        User user2 = new User();
        user2.setUsername("abcd");
        user2.setPassword("1234");
        user2.setId(1235L);

        assertNotEquals(user, user2);
        assertNotEquals(user2, user);
    }

    @Test
    void compareWithNull_returnsFalse() {
        User user2 = null;

        assertNotEquals(user, user2);
    }
}
