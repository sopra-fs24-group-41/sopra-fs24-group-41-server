package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class PlayerJoinedDTO {

    private String playerToken;

    private long playerId;

    private LobbyGetDTO lobby;

    public String getPlayerToken() {
        return playerToken;
    }

    public void setPlayerToken(String playerToken) {
        this.playerToken = playerToken;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public LobbyGetDTO getLobby() {
        return lobby;
    }

    public void setLobby(LobbyGetDTO lobby) {
        this.lobby = lobby;
    }
}
