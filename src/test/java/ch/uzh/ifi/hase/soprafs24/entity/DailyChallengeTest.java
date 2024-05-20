package ch.uzh.ifi.hase.soprafs24.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DailyChallengeTest {
    private DailyChallenge dailyChallenge;

    @BeforeEach
    void setup() {
        dailyChallenge = new DailyChallenge(1, new Word("targetWord"));
    }

    @Test
    void equals_returnsTrue() {
        DailyChallenge dailyChallenge2 = new DailyChallenge(1, new Word("targetWord"));

        assertEquals(dailyChallenge, dailyChallenge2);
        assertEquals(dailyChallenge2, dailyChallenge);
    }

    @Test
    void notEqual_returnsFalse() {
        DailyChallenge dailyChallenge2 = new DailyChallenge(1234, new Word("targetWord"));

        assertNotEquals(dailyChallenge, dailyChallenge2);
        assertNotEquals(dailyChallenge2, dailyChallenge);
    }

    @Test
    void compareWithNull_returnsFalse() {
        DailyChallenge dailyChallenge2 = null;

        assertNotEquals(dailyChallenge, dailyChallenge2);
    }
}
