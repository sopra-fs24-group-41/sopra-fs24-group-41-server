package ch.uzh.ifi.hase.soprafs24.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertTrue(user.equals(user2));
        assertTrue(user2.equals(user));
    }

    @Test
    void notEqual_returnsFalse() {
        User user2 = new User();
        user2.setUsername("abcd");
        user2.setPassword("1234");
        user2.setId(1235L);

        assertFalse(user.equals(user2));
        assertFalse(user2.equals(user));
    }

    @Test
    void compareWithNull_returnsFalse() {
        User user2 = null;

        assertFalse(user.equals(user2));
    }
}
