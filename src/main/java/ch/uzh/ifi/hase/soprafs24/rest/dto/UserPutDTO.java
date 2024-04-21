package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class UserPutDTO {
    private String username;
    private String favourite;

    private String profilePicture;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }

    public String getProfilePicture() { return profilePicture; }

    public void setProfilePicture(String profilePicture){this.profilePicture = profilePicture;}


}
