package com.cs.promotion;

import com.cs.avatar.Level;
import com.cs.bonus.Bonus;
import com.cs.user.User;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Omid Alaepour
 */
@Entity
@Table(name = "promotions")
public class Promotion implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final Long CUSTOMER_SUPPORT_PROMOTION = 1L;
    public static final Long LEVEL_PROMOTION = 2L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @Column(name = "valid_from", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "promotion.validFrom.notNull")
    private Date validFrom;

    @Column(name = "valid_to", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "promotion.validFrom.notNull")
    private Date validTo;

    @Column(name = "name", nullable = false)
    @Nonnull
    @NotNull(message = "promotion.name.notNull")
    private String name;

    @Column(name = "promotion_type", nullable = false)
    @Type(type = "com.cs.promotion.PromotionTriggerType")
    @Nonnull
    @NotNull(message = "promotion.promotionTriggers.notNull")
    private Set<PromotionTrigger> promotionTriggers;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @Nonnull
    @NotNull(message = "promotion.createdBy.notNull")
    private User createdBy;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "promotion.createdDate.notNull")
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    @Nullable
    private User modifiedBy;

    @Column(name = "modified_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date modifiedDate;

    @OneToMany(mappedBy = "promotion")
    @Nullable
    private List<Bonus> bonusList;

    @OneToMany(mappedBy = "pk.promotion")
    @Nullable
    private List<PlayerPromotion> playerPromotions;

    @ManyToOne
    @JoinColumn(name = "required_level", nullable = false)
    @Nonnull
    @NotNull(message = "promotion.level.notNull")
    private Level level;

    @Column(name = "auto_grant_bonuses", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Nonnull
    private Boolean autoGrantBonuses;

    @Nonnull
    public Long getId() {
        return id;
    }

    public void setId(@Nonnull final Long id) {
        this.id = id;
    }

    @Nonnull
    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(@Nonnull final Date validFrom) {
        this.validFrom = validFrom;
    }

    @Nonnull
    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(@Nonnull final Date validTo) {
        this.validTo = validTo;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull final String name) {
        this.name = name;
    }


    @Nonnull
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(@Nonnull final User createdBy) {
        this.createdBy = createdBy;
    }

    @Nonnull
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nonnull final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Nullable
    public User getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(@Nullable final User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Nullable
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(@Nullable final Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Nullable
    public List<PlayerPromotion> getPlayerPromotions() {
        return playerPromotions;
    }

    public void setPlayerPromotions(@Nullable final List<PlayerPromotion> playerPromotions) {
        this.playerPromotions = playerPromotions;
    }

    @Nullable
    public List<Bonus> getBonusList() {
        return bonusList;
    }

    public void setBonusList(@Nullable final List<Bonus> bonusList) {
        this.bonusList = bonusList;
    }

    @Nonnull
    public Set<PromotionTrigger> getPromotionTriggers() {
        return promotionTriggers;
    }

    public void setPromotionTriggers(@Nonnull final Set<PromotionTrigger> promotionTriggers) {
        this.promotionTriggers = promotionTriggers;
    }

    @Nonnull
    public Level getLevel() {
        return level;
    }

    public void setLevel(@Nonnull final Level level) {
        this.level = level;
    }

    @Nonnull
    public Boolean isAutoGrantBonuses() {
        return autoGrantBonuses;
    }

    public void setAutoGrantBonuses(@Nonnull final Boolean autoGrantBonuses) {
        this.autoGrantBonuses = autoGrantBonuses;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Promotion that = (Promotion) o;

        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(id)
                .addValue(validFrom)
                .addValue(validTo)
                .addValue(name)
                .addValue(promotionTriggers)
                .addValue(createdBy)
                .addValue(createdDate)
                .addValue(modifiedBy)
                .addValue(modifiedDate)
                .addValue(level)
                .addValue(autoGrantBonuses)
                .toString();
    }
}
