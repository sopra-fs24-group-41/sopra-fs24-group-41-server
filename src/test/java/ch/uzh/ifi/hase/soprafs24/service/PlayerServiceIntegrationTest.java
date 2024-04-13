package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@SpringBootTest
public class PlayerServiceIntegrationTest {

    @Qualifier("playerRepository")
    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    PlayerService playerService;

    @BeforeEach
    public void setup() {
        playerRepository.deleteAll();
    }

    @Test
    public void checkToken_validInput_success() {
        // TODO: to be implemented
    }

    @Test
    public void checkToken_invalidToken_throwsNotFoundException() {
        // TODO: to be implemented
    }

    @Test
    public void removePlayer_success() {
        // TODO: to be implemented
    }
}
