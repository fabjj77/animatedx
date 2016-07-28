package com.cs.avatar;

import com.cs.persistence.Language;
import com.cs.user.User;

import com.google.common.base.Objects;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Hadi Movaghar
 */
@Entity
@Table(name = "chapter_translations")
public class ChapterTranslation implements Serializable {
    private static final long serialVersionUID = 4L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column(name = "text_id", nullable = false)
    @Nonnull
    @NotNull(message = "chapterTranslation.translationId.notNull")
    private Integer textId;

    @Column(name = "language", nullable = false, length = 20)
    @Enumerated(STRING)
    @Nonnull
    @NotNull(message = "chapterTranslation.language.notNull")
    private Language language;

    @Column(name = "translation", nullable = false, length = 1000)
    @Nonnull
    @NotEmpty(message = "chapterTranslation.translation.notEmpty")
    private String translation;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @Nonnull
    @NotNull(message = "chapterTranslation.createdBy.notNull")
    private User createdBy;

    @Column(name = "created_date", nullable = false)
    @Temporal(TIMESTAMP)
    @Nonnull
    @NotNull(message = "chapterTranslation.createdDate.notNull")
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    @Nullable
    private User modifiedBy;

    @Column(name = "modified_date")
    @Temporal(TIMESTAMP)
    @Nullable
    private Date modifiedDate;

    public ChapterTranslation() {
    }

    public ChapterTranslation(@Nonnull final Integer textId, @Nonnull final Language language, @Nonnull final String translation, @Nonnull final User createdBy,
                              @Nonnull final Date createdDate) {
        this.textId = textId;
        this.language = language;
        this.translation = translation;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    @Nonnull
    public Integer getTextId() {
        return textId;
    }

    public void setTextId(@Nonnull final Integer textId) {
        this.textId = textId;
    }

    @Nonnull
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(@Nonnull final Language language) {
        this.language = language;
    }

    @Nonnull
    public String getTranslation() {
        return translation;
    }

    public void setTranslation(@Nonnull final String translation) {
        this.translation = translation;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ChapterTranslation that = (ChapterTranslation) o;

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
                .addValue(textId)
                .addValue(language)
                .addValue(translation)
                .addValue(createdBy)
                .addValue(createdDate)
                .addValue(modifiedBy)
                .addValue(modifiedDate)
                .toString();
    }
}
