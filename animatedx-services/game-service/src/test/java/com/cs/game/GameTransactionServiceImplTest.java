package com.cs.game;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * @author Hadi Movaghar
 */
@RunWith(MockitoJUnitRunner.class)
public class GameTransactionServiceImplTest {

    @InjectMocks
    private GameTransactionServiceImpl gameTransactionServiceImpl;

    @Mock
    private GameTransactionRepository gameTransactionRepository;

    private List<Object[]> getLeadersForTheWeek() {
        final Object[] result = {"game_id", "111", 3655, 9, 29, "s1h3.png", "Alessandro", "wish-master", "The Wish Master", new BigDecimal(18200), 0};
        final List<Object[]> results = new ArrayList<>();
        results.add(result);
        return results;
    }

    @Test
    public void returnPenthouseResults() {
        final Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_WEEK, -7);

        when(gameTransactionRepository.getLeaderPlayersByWeek(any(Date.class), any(Date.class), anyInt())).thenReturn(getLeadersForTheWeek());

        final List<LeaderboardEntry> resultList = gameTransactionServiceImpl.getLeaderPlayersOfTheWeek(startDate.getTime());

        assertThat(resultList.size(), is(1));
    }
}
