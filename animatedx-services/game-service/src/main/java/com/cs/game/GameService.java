package com.cs.game;

import com.cs.bonus.Bonus;
import com.cs.game.Channel;
import com.cs.persistence.Language;
import com.cs.player.Player;
import com.cs.user.User;

import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * @author Hadi Movaghar
 */
public interface GameService {

    void registerPlayer(final Player player, final Channel channel);

    String loginPlayer(final Player player, final Channel channel);

    void logoutPlayer(final Player player);

    boolean activateBonus(final Player player, final Bonus bonus);

    List<String> getActiveFreeRounds(final Player player);

    Set<Game> refreshGames(final User user);

    void refreshGamesInfos(final User user);

    Page<Game> getGames(@Nonnull final User user, @Nullable final String gameId, @Nullable final String name, @Nullable final GameCategory category,
                        @Nullable final Boolean featured, @Nonnull final Integer page, @Nonnull final Integer size);

    Game updateGame(final Game game, final User user);

    List<GameInfo> getActiveGames(final Language language);

    List<GameInfo> getActiveTouchGames(final Language language);

    GameInfo getGameInfo(final Game game, final Language language);

    GameInfo getTouchGameInfo(final Game game, final Language language);

    void resetSessionTime(final Player player);

    boolean isSessionTimeOver(final Player player);

    void logoutOverTimedPlayer(final Player player);

    @Nullable
    Game getGame(@Nullable String gameId);
}
