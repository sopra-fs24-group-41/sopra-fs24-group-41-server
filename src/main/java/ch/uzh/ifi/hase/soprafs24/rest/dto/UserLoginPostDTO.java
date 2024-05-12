package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class UserLoginPostDTO {

    private String username;

    private String password;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username.replaceAll("[\n\r]", "_");
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
