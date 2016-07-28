package com.cs.casino.level;

import com.cs.avatar.Level;
import com.cs.avatar.LevelDto;
import com.cs.level.LevelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Joakim Gottzén
 */
@RestController
@RequestMapping(value = "/api/levels", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class LevelController {
    private final LevelService levelService;

    @Autowired
    public LevelController(final LevelService levelService) {
        this.levelService = levelService;
    }

    @RequestMapping(method = GET)
    @ResponseStatus(OK)
    public List<LevelDto> getAllLevels() {
        return convert(levelService.getAllLevels());
    }

    private List<LevelDto> convert(final List<Level> allLevels) {
        final List<LevelDto> levelDtos = new ArrayList<>(allLevels.size());
        for (final Level level : allLevels) {
            levelDtos.add(new LevelDto(level));
        }
        return levelDtos;
    }
}
