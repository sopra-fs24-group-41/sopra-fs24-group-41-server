package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;

public class LobbyPutDTO {

    private GameMode mode;

    private Boolean publicAccess;

    private String name;

    Integer gameTime;

    public GameMode getMode() {
        return mode;
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    public Boolean getPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(Boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGameTime(Integer gameTime) {this.gameTime = gameTime; }

    public Integer getGameTime() {return gameTime;}
}
