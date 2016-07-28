package com.cs.affiliate;

import com.cs.player.Player;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AffiliateServiceImplTest {

    @InjectMocks
    private AffiliateServiceImpl affiliateService;

    @Mock
    private PlayerAffiliateRepository playerAffiliateRepository;

    @Test
    public void createPlayerAffiliate_shouldExtractTheAffiliateIdWhenCorrect() {
        final Player player = new Player(1L);
        final String affiliateId = "123456";
        final String lastPart = "LAKF324903SDBFJDSKFJ234923028083";
        final String bTag = affiliateId + "_" + lastPart;
        final String ipAddress = "123.122.222.222";

        when(playerAffiliateRepository.save(any(PlayerAffiliate.class))).then(AdditionalAnswers.returnsFirstArg());

        final PlayerAffiliate playerAffiliate = affiliateService.createPlayerAffiliate(player, bTag, ipAddress);

        assertThat(playerAffiliate.getPlayer(), is(equalTo(player)));
        assertThat(playerAffiliate.getAffiliateId(), is(equalTo(affiliateId)));
        assertThat(playerAffiliate.getBTag(), is(equalTo(bTag)));
        assertThat(playerAffiliate.getCreatedDate(), is(notNullValue()));
        assertThat(playerAffiliate.getReportedDate(), is(nullValue()));
    }

    @Test
    public void createPlayerAffiliate_shouldHandleUnsupportedBTag() {
        final Player player = new Player(1L);
        final String bTag = "akl;dfjasdkl;fjakl;sdfj";
        final String ipAddress = "123.122.222.222";

        when(playerAffiliateRepository.save(any(PlayerAffiliate.class))).then(AdditionalAnswers.returnsFirstArg());

        final PlayerAffiliate playerAffiliate = affiliateService.createPlayerAffiliate(player, bTag, ipAddress);

        assertThat(playerAffiliate.getPlayer(), is(equalTo(player)));
        assertThat(playerAffiliate.getAffiliateId(), isEmptyString());
        assertThat(playerAffiliate.getBTag(), is(equalTo(bTag)));
        assertThat(playerAffiliate.getCreatedDate(), is(notNullValue()));
        assertThat(playerAffiliate.getReportedDate(), is(nullValue()));
    }
}
