package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.DailyChallengeRecord;
import ch.uzh.ifi.hase.soprafs24.rest.dto.DailyChallengeRecordGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.DailyChallengeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DailyChallengeController {
    private final DailyChallengeService dailyChallengeService;

    DailyChallengeController(DailyChallengeService dailyChallengeService) {
        this.dailyChallengeService = dailyChallengeService;
    }

    @GetMapping("/challenges/records")
    @ResponseStatus(HttpStatus.OK)
    public List<DailyChallengeRecordGetDTO> getAllRecordDTOs() {
        List<DailyChallengeRecord> records = dailyChallengeService.getRecords();
        List<DailyChallengeRecordGetDTO> recordDTOs = new ArrayList<>();

        for (DailyChallengeRecord recordItem : records) {
            recordDTOs.add(DTOMapper.INSTANCE.convertEntityToDailyChallengeRecordGetDTO(recordItem));
        }

        return recordDTOs;
    }
}
