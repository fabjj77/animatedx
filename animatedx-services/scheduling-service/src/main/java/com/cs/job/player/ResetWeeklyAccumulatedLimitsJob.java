package com.cs.job.player;

import com.cs.player.PlayerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Joakim Gottzén
 */
@Component
public class ResetWeeklyAccumulatedLimitsJob implements Job {

    private PlayerService playerService;

    @Autowired
    public void setPlayerService(final PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public void execute(final JobExecutionContext context)
            throws JobExecutionException {
        playerService.resetWeeklyAccumulatedLimits();
    }
}
