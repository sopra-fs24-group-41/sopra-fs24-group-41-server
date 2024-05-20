package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.achievements.*;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@Transactional
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final Set<Achievement> achievements = new HashSet<Achievement>();

    @Autowired
    public AchievementService(@Qualifier("achievementRepository") AchievementRepository achievementRepository, SimpMessagingTemplate messagingTemplate) {
        this.achievementRepository = achievementRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Achievement get(Achievement achievement) {
        return achievementRepository.findByName(achievement.getName())
                .orElse(achievementRepository.saveAndFlush(achievement));
    }

    public List<Achievement> getAchievements() {
        return achievements.stream().toList();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setup() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackage("ch.uzh.ifi.hase.soprafs24.entity.achievements")
                .setScanners(Scanners.SubTypes));

        Set<Class<? extends Achievement>> achievementClasses = reflections.getSubTypesOf(Achievement.class);

        for (Class<? extends Achievement> achievementClass : achievementClasses) {
            try {
                achievements.add(get(achievementClass.getDeclaredConstructor().newInstance()));
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                String errorMessage = String.format("Achievement %s could not be instantiated! Exception: %s", achievementClass.getSimpleName(), e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
            }
        }
    }

    public void awardAchievements(Player player, Combination combination) {
        User user = player.getUser();
        if (user == null) return;

        for (Achievement achievement : achievements) {
            if (!user.hasAchievement(achievement) && achievement.unlockConditionFulfilled(player, combination)) {
                user.addAchievement(achievement);
                messagingTemplate.convertAndSend(String.format("/topic/achievements/%d", user.getId()), achievement);
            }
        }
    }
}
