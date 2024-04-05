package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;

import java.util.List;

public class LobbyGetDTO {

    private long code;

    private String name;

    private Boolean publicAccess;

    private LobbyStatus status;

    private GameMode mode;

    private PlayerNestedInLobbyGetDTO owner;

    private List<PlayerNestedInLobbyGetDTO> players;

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(Boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

    public LobbyStatus getStatus() {
        return status;
    }

    public void setStatus(LobbyStatus status) {
        this.status = status;
    }

    public GameMode getMode() {
        return mode;
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    public PlayerNestedInLobbyGetDTO getOwner() {
        return owner;
    }

    public void setOwner(PlayerNestedInLobbyGetDTO owner) {
        this.owner = owner;
    }

    public List<PlayerNestedInLobbyGetDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerNestedInLobbyGetDTO> players) {
        this.players = players;
    }
}
