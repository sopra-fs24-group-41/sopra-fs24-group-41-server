package ch.uzh.ifi.hase.soprafs24.exceptions;

public class WebsocketLobbyException extends RuntimeException {

    private String code;

    public WebsocketLobbyException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getLobbyCode() {
        return code;
    }
}
