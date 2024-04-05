package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class PlayerJoinedDTO {

    private String playerToken;

    private LobbyGetDTO lobby;

    public String getPlayerToken() {
        return playerToken;
    }

    public void setPlayerToken(String playerToken) {
        this.playerToken = playerToken;
    }

    public LobbyGetDTO getLobby() {
        return lobby;
    }

    public void setLobby(LobbyGetDTO lobby) {
        this.lobby = lobby;
    }
}
