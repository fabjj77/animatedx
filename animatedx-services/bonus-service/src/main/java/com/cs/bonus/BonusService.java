package com.cs.bonus;

import com.cs.payment.DCPaymentTransaction;
import com.cs.payment.EventCode;
import com.cs.payment.Money;
import com.cs.payment.PaymentTransaction;
import com.cs.player.Player;
import com.cs.promotion.PlayerCriteria;
import com.cs.user.User;
import com.cs.util.Pair;

import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Hadi Movaghar
 */
public interface BonusService {

    Bonus getBonus(final Long bonusId);

    Bonus getCreditsConversionBonus();

    List<Bonus> getAvailableBonuses(final Player player, final TriggerEvent triggerEvent);

    List<Bonus> getAvailableBonuses(final Player player, final BonusType bonusType);

    Bonus createBonus(final Bonus bonus, final Long promotionId, final Long levelId, final User user);

    Bonus updateBonus(final Bonus bonus, final Long promotionId, final User user);

    List<PlayerBonus> getCurrentPlayerBonuses(Player player);

    Page<PlayerBonus> getAllPlayerBonuses(final Player player, final Integer page, final Integer size);

    List<PlayerBonus> getPlayerBonusesTiedToPayment(final Player player, final PaymentTransaction payment);

    List<PlayerBonus> getPlayerBonusesTiedToPayment(final Player player, final DCPaymentTransaction payment);

    List<PlayerBonus> getOldReservedPlayerBonuses(final Player player, final Date date);

    @Nonnull
    PlayerBonus updatePlayerBonus(@Nonnull final PlayerBonus playerBonus);

    @Nullable
    PlayerBonus grantBonusToPlayer(final Player player, final Bonus bonus);

    @Nullable
    List<PlayerBonus> grantScheduledBonusesByCriteria(final Player player, final Bonus bonus, final PlayerCriteria playerCriteria);

    void useBonuses(final Player player, final List<Bonus> bonuses, final PlayerCriteria playerCriteria);

    @Nullable
    PlayerBonus useBonus(@Nonnull final Player player, @Nonnull final Bonus bonus, @Nonnull final PlayerCriteria playerCriteria);

    void grantItemBonus(@Nonnull final Player player, @Nonnull final Bonus bonus);

    PlayerBonus grantLinkBonus(final Player player, final String bonusCode);

    PlayerBonus handleFreeRoundsCompletionBonus(final Player player, final Money amount, final Long bonusProgramId);

    PlayerBonus finishCurrentAndActivateNextBonus(final Player player, final PlayerBonus current, final BonusStatus status);

    PlayerBonus cancelPlayerBonus(final Long playerBonusId);

    Pair<Pair<Money, EventCode>, PlayerBonus> updatePlayerBonus(final Long playerBonusId, final User user, final Money requestedBonusBalance);

    PlayerBonus addAdjustableBonus(final Player player, final User user, final Money amount, final Money maxRedemptionAmount, final Date validFrom, final Date validTo);

    PlayerBonus getPlayerBonus(final Long playerBonusId);

    @Nullable
    PlayerBonus getActivePlayerBonus(final Player player);

    List<PlayerBonus> getVoidablePlayerBonuses(final Player player);

    PlayerBonus grantBackOfficeBonus(final Player player, final User user, final Long bonusId, final Money amount, @Nullable final Money maxRedemptionAmount,
                                     final Date validFrom, @Nullable final Date validTo);

    Map<BigInteger, Pair<Money, Money>> getPlayersInactiveReservedBonusBalances();

    List<String> getActiveFreeRounds(final Player player);

    Map<BigInteger, Money> getAffiliatePlayersBonusBalances();

    void handleScheduledBonuses(Player player);
}
