package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class PlayerPostDTO {

    private String playerName;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName.substring(0, Math.min(playerName.length(), 20)).replaceAll("[\n\r]", "_");
    }
}
