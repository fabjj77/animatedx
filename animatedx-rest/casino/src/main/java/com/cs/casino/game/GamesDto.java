package com.cs.casino.game;

import com.cs.game.GameCategory;
import com.cs.game.GameDto;
import com.cs.game.GameInfo;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Joakim Gottzén
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class GamesDto {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nonnull
    private List<CategoryDto> categories;

    @SuppressWarnings("UnusedDeclaration")
    public GamesDto() {
    }

    public GamesDto(final List<GameInfo> activeGames) {
        final AbstractMap<GameCategory, List<GameDto>> map = Maps.newEnumMap(GameCategory.class);
        final List<GameDto> featuredGames = new ArrayList<>(activeGames.size());
        for (final GameInfo game : activeGames) {
            List<GameDto> gameDtos = map.get(game.getGame().getCategory());
            if (gameDtos == null) {
                gameDtos = new ArrayList<>(activeGames.size());
                map.put(game.getGame().getCategory(), gameDtos);
            }
            final GameDto gameDto = new GameDto(game);
            gameDtos.add(gameDto);
            if (game.getGame().getFeatured()) {
                featuredGames.add(gameDto);
            }
        }
        categories = new ArrayList<>();
        categories.add(new CategoryDto("featured", "featured", featuredGames));
        for (final GameCategory gameCategory : GameCategory.getPlayableCategories()) {
            categories.add(new CategoryDto(gameCategory.name(), gameCategory.getSlug(), map.get(gameCategory)));
        }
    }

    @XmlRootElement
    @XmlAccessorType(FIELD)
    private static final class CategoryDto {

        @XmlElement
        @Nonnull
        private final String name;

        @XmlElement
        @Nonnull
        private final String slug;

        @XmlElement
        @Nullable
        private final List<GameDto> games;

        @XmlElement
        @Nonnull
        private final Integer count;

        private CategoryDto(@Nonnull final String name, @Nonnull final String slug, @Nullable final List<GameDto> games) {
            this.name = name;
            this.slug = slug;
            this.games = games;
            count = games != null ? games.size() : 0;
        }
    }
}
