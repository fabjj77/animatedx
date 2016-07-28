package com.cs.game;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlEnum
public enum GameCategory {
    SLOTS("slots", 100.0D), CASINO("casino", 10.0D), BLACKJACK("blackjack", 10.0D), ROULETTE("roulette", 10.0D), VIDEO_POKER("video-poker", 10.0D), OTHER("other", 0.0D);

    // Only create the list of playable categories once, during bootstrap
    private static final Collection<GameCategory> playableCategories;
    static {
        playableCategories = Collections.unmodifiableCollection(Collections2.filter(Arrays.asList(values()), new Predicate<GameCategory>() {
            @Override
            public boolean apply(@Nullable final GameCategory input) {
                return input != OTHER;
            }
        }));
    }

    private final String slug;
    private final Double turnoverContributionFraction;

    GameCategory(final String slug, final Double turnoverContributionPercentage) {
        this.slug = slug;
        turnoverContributionFraction = turnoverContributionPercentage / 100;
    }

    public String getSlug() {
        return slug;
    }

    public Double getTurnoverContributionFraction() {
        return turnoverContributionFraction;
    }

    public static Collection<GameCategory> getPlayableCategories() {
        return playableCategories;
    }
}
