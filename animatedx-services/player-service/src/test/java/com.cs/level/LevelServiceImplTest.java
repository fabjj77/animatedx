package com.cs.level;

import com.cs.avatar.Level;
import com.cs.persistence.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author Omid Alaepour.
 */
@RunWith(MockitoJUnitRunner.class)
public class LevelServiceImplTest {

    @InjectMocks
    private LevelServiceImpl levelService;

    @Mock
    LevelRepository levelRepository;

    private Level createLevel() {
        final Level level = new Level();
        level.setLevel(10L);
        level.setStatus(Status.ACTIVE);
        return level;
    }

    @Test
    public void getLevel_ShouldReturnLevel() {
        final Level level = createLevel();
        when(levelRepository.findOne(anyLong())).thenReturn(level);

        assertThat(levelService.getLevel(anyLong()), is(sameInstance(level)));
    }

    @Test
    public void getLevel_ShouldReturnNullWhenLevelIsInactive() {
        final Level level = createLevel();
        level.setStatus(Status.INACTIVE);
        when(levelRepository.findOne(eq(level.getLevel()))).thenReturn(level);

        final Level returnedLevel = levelService.getLevel(level.getLevel());

        assertThat(returnedLevel, is(nullValue()));
    }

    @Test
    public void getAllLevels_ShouldReturnLevels() {
        final Level level = createLevel();
        final List<Level> levelList = new ArrayList<>();
        levelList.add(level);
        when(levelRepository.findByStatus(Status.ACTIVE)).thenReturn(levelList);

        assertThat(levelService.getAllLevels(), is(sameInstance(levelList)));
    }
}
