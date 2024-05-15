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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;


@Service
@Transactional
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final Set<Achievement> achievements = new HashSet<Achievement>();

    @Autowired
    public AchievementService(@Qualifier("achievementRepository") AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public Achievement get(Achievement achievement) {
        return achievementRepository.findByName(achievement.getName())
                .orElse(achievementRepository.saveAndFlush(achievement));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setup() {
        achievements.addAll(Set.of(get(new CreatedMud()), get(new CreatedZaddy())));
    }

    public void awardAchievements(Player player, Combination combination) {
        User user = player.getUser();
        if (user == null) return;

        for (Achievement achievement : achievements) {
            achievement.unlock(player, combination);
        }
    }
}
