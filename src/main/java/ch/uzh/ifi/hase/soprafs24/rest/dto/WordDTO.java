package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class WordDTO {
    private String name;
    private boolean newlyDiscovered;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNewlyDiscovered() {
        return newlyDiscovered;
    }

    public void setNewlyDiscovered(boolean newlyDiscovered) {
        this.newlyDiscovered = newlyDiscovered;
    }
}
