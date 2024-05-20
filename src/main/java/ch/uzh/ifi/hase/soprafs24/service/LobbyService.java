package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameMode;
import ch.uzh.ifi.hase.soprafs24.constant.Instruction;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.websocket.InstructionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class LobbyService {
    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    private final LobbyRepository lobbyRepository;

    private final PlayerService playerService;

    private final SimpMessagingTemplate messagingTemplate;

    private final PlatformTransactionManager transactionManager;

    private static final String MESSAGE_LOBBY_BASE = "/topic/lobbies";
    private static final String MESSAGE_LOBBY_GAME = "/topic/lobbies/%d/game";

    @Autowired
    public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository, PlayerService playerService,
                        SimpMessagingTemplate messagingTemplate, PlatformTransactionManager transactionManager) {
        this.lobbyRepository = lobbyRepository;
        this.playerService = playerService;
        this.messagingTemplate = messagingTemplate;
        this.transactionManager = transactionManager;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void scheduleCheckLobbyStillActiveStartup() {
        scheduleCheckLobbyStillActive(180, 30); // change values here to adjust timings
    }

    public void scheduleCheckLobbyStillActive(long thresholdMinutes, long periodMinutes) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Runnable checkLobbies = () -> checkAndRemoveInactiveLobbies(thresholdMinutes);

        long initialDelay = 0;
        executorService.scheduleAtFixedRate(checkLobbies, initialDelay, periodMinutes, TimeUnit.MINUTES);
    }

    public void checkAndRemoveInactiveLobbies(long thresholdMinutes) {
        try {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.execute(status -> {
                List<Lobby> allLobbies = lobbyRepository.findAll();
                for (Lobby lobby : allLobbies) {
                    if (lobby.getLastModified() == null) continue;
                    long minutesDifference = ChronoUnit.MINUTES.between(lobby.getLastModified(), LocalDateTime.now());
                    if (minutesDifference >= thresholdMinutes) {
                        removeLobby(lobby);
                        messagingTemplate.convertAndSend(String.format(MESSAGE_LOBBY_GAME, lobby.getCode()),
                                new InstructionDTO(Instruction.KICK, null, "The lobby was closed due to inactivity"));
                        log.debug("Lobby with code {} was last active on {} and was closed due to inactivity", lobby.getCode(), lobby.getLastModified());
                    }
                }
                return null;
            });
            transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.execute(status -> {
                messagingTemplate.convertAndSend(MESSAGE_LOBBY_BASE,
                        new InstructionDTO(Instruction.UPDATE_LOBBY_LIST, getPublicLobbies().stream().map(DTOMapper.INSTANCE::convertEntityToLobbyGetDTO).toList()));
                return null;
            });
        } catch(Exception e) {
            log.error("Could not check lobbies for inactivity: ", e);
        }
    }

    public List<Lobby> getPublicLobbies() {
        return lobbyRepository.findAllByPublicAccess(true);
    }

    // TODO: decide if we want to remove this method
    public boolean allPlayersReady(Lobby lobby) {
        return lobby.getPlayers().stream().allMatch(player -> player.getStatus() == PlayerStatus.READY);
    }

    // TODO: decide if we want to remove this method
    public boolean allPlayersLost(Lobby lobby) {
        return lobby.getPlayers().stream().allMatch(player -> player.getStatus() == PlayerStatus.LOST);
    }

    public Lobby getLobbyByCode(long code) {
        Lobby foundLobby = lobbyRepository.findByCode(code);
        if (foundLobby == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("lobby with code %d does not exist", code));
        }
        return foundLobby;
    }

    public Player createLobbyFromUser(User user, Boolean publicAccess) {
        String lobbyName = user.getUsername() + "'s Lobby";
        Lobby lobby = new Lobby(generateLobbyCode(), lobbyName);
        lobby.setStatus(LobbyStatus.PREGAME);
        Player player = new Player(UUID.randomUUID().toString(), user.getUsername(), lobby);

        player.setOwnedLobby(lobby);
        lobby.setOwner(player);
        lobby.setPlayers(List.of(player));
        lobby.setPublicAccess(Objects.requireNonNullElse(publicAccess, true));
        lobby.setGameTime(0);
        lobby.setMode(GameMode.WOMBOCOMBO);

        Lobby savedLobby = lobbyRepository.saveAndFlush(lobby);
        user.setPlayer(savedLobby.getOwner());
        savedLobby.getOwner().setUser(user);

        log.debug("created new lobby {}", lobby);
        log.debug("created new player from user and set as lobby owner{}", player);
        return savedLobby.getOwner();
    }

    public Player joinLobbyFromUser(User user, long lobbyCode) {
        Lobby foundLobby = getLobbyByCode(lobbyCode);
        Player player;

        if (user.getPlayer() != null && user.getPlayer().getLobby() != null) {
            player = user.getPlayer();
            if (player.getLobby() != foundLobby) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        String.format("user is already in lobby with code %s, leave that lobby before joining another one",
                                player.getLobby().getCode()));
            }
            player.setToken(UUID.randomUUID().toString());
            log.debug("reset token for player {}", player);
        }
        else {
            if (foundLobby.getStatus() != LobbyStatus.PREGAME) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "This lobby does not accept new players, wait until the game is finished");
            }

            player = new Player(UUID.randomUUID().toString(), user.getUsername(), foundLobby);
            player.setUser(user);
            foundLobby.addPlayer(player);
            user.setPlayer(player);

            log.debug("user joined -> updated lobby {}", foundLobby);
            log.debug("created new player from user {}", player);
        }
        return player;
    }

    public Player joinLobbyAnonymous(String playerName,long lobbyCode) {
        if (playerName == null || playerName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "There was no playername provided or your playername is invalid, please specify a playername");
        }

        Lobby foundLobby = getLobbyByCode(lobbyCode);
        if (foundLobby.getStatus() != LobbyStatus.PREGAME) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "This lobby does not accept new players, wait until the game is finished");
        }

        if (foundLobby.getPlayers().stream().anyMatch(player -> player.getName().equals(playerName))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "The playername you entered is already taken, choose another");
        }

        Player player = new Player(UUID.randomUUID().toString(), playerName, foundLobby);
        foundLobby.addPlayer(player);
        log.debug("updated lobby {}", foundLobby);
        log.debug("created new anonymous player from player name {}", player);
        return player;
    }

    public Lobby updateLobby(Lobby lobby, LobbyPutDTO lobbyPutDTO) {
        List<Integer> validGameTimes = List.of(0, 60, 90, 120, 150, 180, 210, 240, 270, 300);
        if (lobbyPutDTO.getName() != null && lobbyPutDTO.getName().length() > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The lobby name is too long, please choose a name with 20 characters or less");
        }
        else if (lobbyPutDTO.getName() != null && lobbyPutDTO.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The lobby name cannot be empty");
        }
        else if (lobbyPutDTO.getGameTime() != null && !validGameTimes.contains(lobbyPutDTO.getGameTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The game time is invalid, please choose a valid game time");
        }

        lobby.resetUpdate();

        if (lobbyPutDTO.getMode() != null && !lobbyPutDTO.getMode().equals(lobby.getMode())) {
            lobby.setMode(lobbyPutDTO.getMode());
        }
        if (lobbyPutDTO.getName() != null && !Objects.equals(lobbyPutDTO.getName(), lobby.getName())) {
            lobby.setName(lobbyPutDTO.getName().strip());
        }
        if (lobbyPutDTO.getPublicAccess() != null && !Objects.equals(lobbyPutDTO.getPublicAccess(), lobby.getPublicAccess())) {
            lobby.setPublicAccess(lobbyPutDTO.getPublicAccess());
        }
        if (lobbyPutDTO.getGameTime()!=null && !Objects.equals(lobbyPutDTO.getGameTime(), lobby.getGameTime())) {
            lobby.setGameTime(lobbyPutDTO.getGameTime());
        }
        return lobby;
    }

    public void removeLobby(Lobby lobby) {
        if (lobby.getOwner() != null) {
            lobby.getOwner().setOwnedLobby(null);
            lobby.setOwner(null);
        }
        if (lobby.getPlayers() != null) {
            List<Player> playerList = lobby.getPlayers();
            for (int i = playerList.toArray().length-1; i>=0; i--) {
                playerService.removePlayer(playerList.get(i));
            }
        }
        lobbyRepository.delete(lobby);
        log.debug("successfully deleted lobby {}", lobby);
    }

    private long generateLobbyCode() {
        long code = ThreadLocalRandom.current().nextLong(1000, 10000);
        while (lobbyRepository.existsByCode(code)) {
            code = ThreadLocalRandom.current().nextLong(1000, 10000);
        }
        return code;
    }

}
