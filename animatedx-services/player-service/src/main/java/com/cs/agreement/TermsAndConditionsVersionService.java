package com.cs.agreement;

import com.cs.player.Player;
import com.cs.user.User;

import javax.annotation.Nonnull;

public interface TermsAndConditionsVersionService {

    @Nonnull
    TermsAndConditionsVersion createTermsAndConditions(@Nonnull final String version, final User user);

    @Nonnull
    TermsAndConditionsVersion activateTermsAndConditions(@Nonnull final Long id, final User user);

    void acceptLatestTermsAndConditions(@Nonnull final Player player);

    boolean hasAcceptedLatestTermsAndConditions(final Player player);
}
