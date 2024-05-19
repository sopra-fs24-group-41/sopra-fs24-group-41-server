package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.achievements.Achievement;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.achievements.CreatedMud;
import ch.uzh.ifi.hase.soprafs24.entity.achievements.CreatedZaddy;
import ch.uzh.ifi.hase.soprafs24.repository.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        achievements.addAll(Set.of(get(new CreatedMud()), get(new CreatedZaddy())));
    }

    public void awardAchievements(Player player, Combination combination) {
        User user = player.getUser();
        if (user == null) return;

        for (Achievement achievement : achievements) {
            if (!user.hasAchievement(achievement)){
                achievement.unlock(player, combination);
                messagingTemplate.convertAndSend(String.format("/topic/achievements/%d", user.getId()), achievement);
            }
        }
    }
}
