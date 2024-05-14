package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Achievement;
import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Service
@Transactional
public class AchievementService {
    private final AchievementRepository achievementRepository;

    @Autowired
    public AchievementService(@Qualifier("achievementRepository") AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public Achievement getAchievement(Achievement achievement) {
        Achievement foundAchievement = achievementRepository.findByName(achievement.getName());
        if (foundAchievement == null) {
            return achievementRepository.saveAndFlush(achievement);
        }
        return foundAchievement;
    }

    public void awardAchievements(Player player, Combination combination) {
        User user = player.getUser();
        if (user == null) return;

        if (combination.getResult().getName().equals("mud")) {
            user.addAchievement(getAchievement(new Achievement(
                    "This stuff is everywhere!",
                    "Create mud through any combination",
                    "MudPuddle.png")));
        }
    }
}
