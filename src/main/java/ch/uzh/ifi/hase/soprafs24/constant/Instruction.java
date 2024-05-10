package ch.uzh.ifi.hase.soprafs24.constant;

public enum Instruction {
    START, STOP, KICK, UPDATE;

    public String toString() {
        return this.name().toLowerCase();
    }
}
