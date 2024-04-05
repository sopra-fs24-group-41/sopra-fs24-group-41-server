package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class PlayerNestedInLobbyGetDTO {

    private long id;

    private String name;

    private long points;

    private UserGetDTO user;

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
}
