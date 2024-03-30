package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;

/**
 * Composite key for PlayerWord Entity
 */
public class PlayerWordId implements Serializable {
    private long player;
    private long word;
}
