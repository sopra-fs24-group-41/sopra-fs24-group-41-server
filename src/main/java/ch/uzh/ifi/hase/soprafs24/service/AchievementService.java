package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.repository.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class AchievementService {
    private final AchievementRepository achievementRepository;

    @Autowired
    public AchievementService(@Qualifier("achievementRepository") AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }
}
