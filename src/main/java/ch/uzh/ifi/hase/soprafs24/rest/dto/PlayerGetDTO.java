package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class PlayerGetDTO {

    private long id;

    private String name;

    private long points;

    private UserGetDTO user;

    private LobbyGetDTO ownedLobby;

    private LobbyGetDTO lobby;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public UserGetDTO getUser() {
        return user;
    }

    public void setUser(UserGetDTO user) {
        this.user = user;
    }

    public LobbyGetDTO getOwnedLobby() {
        return ownedLobby;
    }

    public void setOwnedLobby(LobbyGetDTO ownedLobby) {
        this.ownedLobby = ownedLobby;
    }

    public LobbyGetDTO getLobby() {
        return lobby;
    }

    public void setLobby(LobbyGetDTO lobby) {
        this.lobby = lobby;
    }
}
