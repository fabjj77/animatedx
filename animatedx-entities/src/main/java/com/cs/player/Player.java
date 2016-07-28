package com.cs.player;

import com.cs.affiliate.PlayerAffiliate;
import com.cs.avatar.Avatar;
import com.cs.avatar.Level;
import com.cs.bonus.PlayerBonus;
import com.cs.item.PlayerItem;
import com.cs.payment.Currency;
import com.cs.persistence.Language;
import com.cs.persistence.Status;
import com.cs.promotion.PlayerPromotion;
import com.cs.validation.ValidationProperties;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Joakim Gottzén
 */
@Entity
@Table(name = "players")
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @Column(name = "first_name", nullable = false, length = 30)
    @Nonnull
    @NotEmpty(message = "player.firstName.notEmpty")
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 30)
    @Nonnull
    @NotEmpty(message = "player.lastName.notEmpty")
    private String lastName;

    @Column(name = "email_address", nullable = false, length = 50, unique = true)
    @Nonnull
    @Email(message = "player.emailAddress.notValid")
    private String emailAddress;

    @Column(name = "password", nullable = false, length = 70)
    @Nonnull
    @Pattern(regexp = ValidationProperties.BCRYPT_PASSWORD_PATTERN, message = "player.password.notValid")
    private String password;

    @Column(name = "nickname", nullable = false, length = 30, unique = true)
    @Nonnull
    @NotEmpty(message = "player.nickname.notEmpty")
    private String nickname;

    @Column(name = "birthday", nullable = false)
    @Temporal(DATE)
    @Nonnull
    @Past(message = "playerDto.birthday.notValid")
    private Date birthday;

    @ManyToOne
    @JoinColumn(name = "avatar_id", nullable = false)
    @Nonnull
    @Valid
    private Avatar avatar;

    @ManyToOne
    @JoinColumn(name = "level", nullable = false)
    @Nonnull
    @Valid
    private Level level;

    @Embedded
    @Nonnull
    @Valid
    private Address address;

    @Column(name = "currency", nullable = false, length = 3)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "player.currency.notNull")
    private Currency currency;

    @Column(name = "phone_number", nullable = false, length = 30)
    @Nonnull
    @NotEmpty(message = "player.phoneNumber.notEmpty")
    private String phoneNumber;

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "player.status.notNull")
    private Status status;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "player.createdDate.notNull")
    private Date createdDate;

    @Column(name = "modified_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date modifiedDate;

    @Column(name = "verification")
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "player.playerVerification.notNull")
    private Verification playerVerification;

    @Column(name = "email_verification")
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "player.emailVerification.notNull")
    private Verification emailVerification;

    @Column(name = "block_type")
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "player.blockType.notNull")
    private BlockType blockType;

    @Column(name = "block_end_date")
    @Temporal(TIMESTAMP)
    @Nonnull
    private Date blockEndDate;

    @Column(name = "trust_level", nullable = false, length = 20)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "player.trustLevel.notNull")
    private TrustLevel trustLevel;

    @Column(name = "language", nullable = false, length = 20)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "player.language.notNull")
    private Language language;

    @OneToOne(mappedBy = "player")
    @Nonnull
    @Valid
    private Wallet wallet;

    @Column(name = "failed_login_attempts")
    @Nonnull
    private Integer failedLoginAttempts;

    @Column(name = "last_failed_login_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date lastFailedLoginDate;

    @Column(name = "receive_promotion")
    @Enumerated(STRING)
    @Nonnull
    private ReceivePromotion receivePromotion;

    @OneToMany(mappedBy = "pk.player")
    @Nonnull
    private List<PlayerItem> playerItems = new ArrayList<>();

    @OneToOne(mappedBy = "player", fetch = LAZY)
    @Nullable
    private PlayerAffiliate playerAffiliate;

    @OneToMany(mappedBy = "pk.player")
    @Nonnull
    private List<PlayerPromotion> playerPromotions = new ArrayList<>();

    @OneToMany(mappedBy = "pk.player")
    @Nullable
    private List<PlayerBonus> playerBonuses = new ArrayList<>();

    @OneToOne(mappedBy = "player")
    @Nonnull
    private PlayerLimitation playerLimitation;

    @Column(name = "test_account", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Nonnull
    private Boolean testAccount;

    public Player() {
    }

    public Player(@Nonnull final Long id) {
        this.id = id;
    }

    public Player(@Nonnull final String firstName, @Nonnull final String lastName, @Nonnull final String emailAddress, @Nonnull final String password,
                  @Nonnull final String nickname, @Nonnull final Date birthday, @Nonnull final Avatar avatar, @Nonnull final Level level, @Nonnull final Address address,
                  @Nonnull final Currency currency, @Nonnull final String phoneNumber, @Nonnull final Status status, @Nonnull final TrustLevel trustLevel,
                  @Nonnull final BlockType blockType, @Nonnull final Language language) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.password = password;
        this.nickname = nickname;
        this.birthday = birthday;
        this.avatar = avatar;
        this.level = level;
        this.address = address;
        this.currency = currency;
        this.phoneNumber = phoneNumber;
        this.status = status;
        emailVerification = Verification.UNVERIFIED;
        playerVerification = Verification.UNVERIFIED;
        this.trustLevel = trustLevel;
        this.blockType = blockType;
        this.language = language;
        failedLoginAttempts = 0;
    }

    @Nonnull
    public Long getId() {
        return id;
    }

    public void setId(@Nonnull final Long id) {
        this.id = id;
    }

    @Nonnull
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@Nonnull final String firstName) {
        this.firstName = firstName;
    }

    @Nonnull
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@Nonnull final String lastName) {
        this.lastName = lastName;
    }

    @Nonnull
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(@Nonnull final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Nonnull
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nonnull final String password) {
        this.password = password;
    }

    @Nonnull
    public String getNickname() {
        return nickname;
    }

    public void setNickname(@Nonnull final String nickname) {
        this.nickname = nickname;
    }

    @Nonnull
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(@Nonnull final Date birthday) {
        this.birthday = birthday;
    }

    @Nonnull
    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(@Nonnull final Avatar avatar) {
        this.avatar = avatar;
    }

    @Nonnull
    public Level getLevel() {
        return level;
    }

    public void setLevel(@Nonnull final Level level) {
        this.level = level;
    }

    @Nonnull
    public Address getAddress() {
        return address;
    }

    public void setAddress(@Nonnull final Address address) {
        this.address = address;
    }

    @Nonnull
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(@Nonnull final Currency currency) {
        this.currency = currency;
    }

    @Nonnull
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@Nonnull final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Nonnull
    public Status getStatus() {
        return status;
    }

    public void setStatus(@Nonnull final Status status) {
        this.status = status;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Nullable
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(@Nullable final Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Nonnull
    public Verification getPlayerVerification() {
        return playerVerification;
    }

    public void setPlayerVerification(@Nonnull final Verification playerVerification) {
        this.playerVerification = playerVerification;
    }

    @Nonnull
    public Verification getEmailVerification() {
        return emailVerification;
    }

    public void setEmailVerification(@Nonnull final Verification emailVerification) {
        this.emailVerification = emailVerification;
    }

    @Nonnull
    public BlockType getBlockType() {
        return blockType;
    }

    public void setBlockType(@Nonnull final BlockType blockType) {
        this.blockType = blockType;
    }

    @Nonnull
    public Date getBlockEndDate() {
        return blockEndDate;
    }

    public void setBlockEndDate(@Nonnull final Date blockEndDate) {
        this.blockEndDate = blockEndDate;
    }

    @Nonnull
    public TrustLevel getTrustLevel() {
        return trustLevel;
    }

    public void setTrustLevel(@Nonnull final TrustLevel trustLevel) {
        this.trustLevel = trustLevel;
    }

    @Nonnull
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(@Nonnull final Language language) {
        this.language = language;
    }

    @Nonnull
    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(@Nonnull final Wallet wallet) {
        this.wallet = wallet;
    }

    @Nonnull
    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(@Nonnull final Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    @Nullable
    public Date getLastFailedLoginDate() {
        return lastFailedLoginDate;
    }

    public void setLastFailedLoginDate(@Nullable final Date lastFailedLoginDate) {
        this.lastFailedLoginDate = lastFailedLoginDate;
    }

    @Nonnull
    public ReceivePromotion getReceivePromotion() {
        return receivePromotion;
    }

    public void setReceivePromotion(@Nonnull final ReceivePromotion receivePromotion) {
        this.receivePromotion = receivePromotion;
    }

    @Nonnull
    public List<PlayerItem> getPlayerItems() {
        return playerItems;
    }

    public void setPlayerItems(@Nonnull final List<PlayerItem> playerItems) {
        this.playerItems = playerItems;
    }

    @Nullable
    public PlayerAffiliate getPlayerAffiliate() {
        return playerAffiliate;
    }

    public void setPlayerAffiliate(@Nullable final PlayerAffiliate playerAffiliate) {
        this.playerAffiliate = playerAffiliate;
    }

    @Nonnull
    public List<PlayerPromotion> getPlayerPromotions() {
        return playerPromotions;
    }

    public void setPlayerPromotions(@Nonnull final List<PlayerPromotion> playerPromotions) {
        this.playerPromotions = playerPromotions;
    }

    @Nullable
    public List<PlayerBonus> getPlayerBonuses() {
        return playerBonuses;
    }

    public void setPlayerBonuses(@Nullable final List<PlayerBonus> playerBonuses) {
        this.playerBonuses = playerBonuses;
    }

    @Nonnull
    public PlayerLimitation getPlayerLimitation() {
        return playerLimitation;
    }

    public void setPlayerLimitation(@Nonnull final PlayerLimitation playerLimitation) {
        this.playerLimitation = playerLimitation;
    }

    @Nonnull
    public Boolean isTestAccount() {
        return testAccount;
    }

    public void setTestAccount(@Nonnull final Boolean testAccount) {
        this.testAccount = testAccount;
    }

    public boolean isEmailVerified() {
        return emailVerification == Verification.VERIFIED;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @SuppressWarnings("ConstantConditions")
    public void updateFromPlayer(final Player player) {
        if (player.firstName != null) {
            firstName = player.firstName;
        }
        if (player.lastName != null) {
            lastName = player.lastName;
        }
        if (player.emailAddress != null) {
            emailAddress = player.emailAddress;
        }
        if (player.password != null) {
            password = player.password;
        }
        if (player.nickname != null) {
            nickname = player.nickname;
        }
        if (player.address != null) {
            address.updateFromAddress(player.address);
        }
        if (player.phoneNumber != null) {
            phoneNumber = player.phoneNumber;
        }
        if (player.receivePromotion != null) {
            receivePromotion = player.receivePromotion;
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void updateFromPlayerBackOffice(final Player player) {
        updateFromPlayer(player);

        if (player.birthday != null) {
            birthday = player.birthday;
        }
        if (player.status != null) {
            // Reset failed login attempts if it's an unlock update
            if (status == Status.BAD_CREDENTIALS_LOCKED && player.status == Status.ACTIVE) {
                failedLoginAttempts = 0;
            }
            status = player.status;
        }
        if (player.playerVerification != null) {
            // If the player is unverified and it's a KYC update, set the trust level to GREEN
            if (playerVerification == Verification.UNVERIFIED && player.playerVerification == Verification.VERIFIED) {
                trustLevel = TrustLevel.GREEN;
            }
            playerVerification = player.playerVerification;
        }
        if (player.emailVerification != null) {
            emailVerification = player.emailVerification;
        }
        if (player.trustLevel != null) {
            trustLevel = player.trustLevel;
        }
        if (player.testAccount != null) {
            testAccount = player.testAccount;
        }
    }

    public boolean isCashbackEligible() {
        return level.getCashbackPercentage().compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Player that = (Player) o;

        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(id)
                .addValue(firstName)
                .addValue(lastName)
                .addValue(emailAddress)
//              Do not EVER print the password anywhere //Joakim
//                .addValue(password)
                .addValue(nickname)
                .addValue(birthday)
                .addValue(avatar != null ? avatar.getId() : null)
                .addValue(level != null ? level.getLevel() : null)
                .addValue(address)
                .addValue(currency)
                .addValue(phoneNumber)
                .addValue(status)
                .addValue(playerVerification)
                .addValue(createdDate)
                .addValue(modifiedDate)
                .addValue(trustLevel)
                .addValue(blockType)
                .addValue(blockEndDate)
                .addValue(language)
                .addValue(failedLoginAttempts)
                .addValue(lastFailedLoginDate)
                .addValue(receivePromotion)
                .addValue(testAccount)
                .toString();
    }
}
