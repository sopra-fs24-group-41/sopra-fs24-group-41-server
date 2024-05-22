package ch.uzh.ifi.hase.soprafs24.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {
    private Lobby lobby;

    private Player player1;
    private Player player2;

    @BeforeEach
    void setup() {
        lobby = new Lobby();
        player1 = new Player();
        player2 = new Player();

        lobby.setPlayers(List.of(player1, player2));
        player1.setLobby(lobby);
        player2.setLobby(lobby);
    }

    @Test
    void equals_returnsTrue() {
        Lobby lobby2 = new Lobby();
        lobby2.setPlayers(List.of(player1, player2));

        lobby.setCode(1234);
        lobby2.setCode(1234);

        lobby.setName("abcd");
        lobby2.setName("abcd");

        assertEquals(lobby, lobby2);
        assertEquals(lobby2, lobby);
    }

    @Test
    void notEqual_returnsFalse() {
        Lobby lobby2 = new Lobby();
        lobby2.setPlayers(List.of(player1, player2));

        lobby.setCode(1234);
        lobby2.setCode(1234);

        lobby.setName("abcd");
        lobby2.setName("abcdefghi");

        assertNotEquals(lobby, lobby2);
        assertNotEquals(lobby2, lobby);
    }

    @Test
    void compareWithNull_returnsFalse() {
        Lobby lobby2 = null;

        assertNotEquals(lobby, lobby2);
    }
}
