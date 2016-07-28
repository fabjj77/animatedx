package com.cs.administration.player;

import com.cs.player.PlayerComment;

import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class PlayerCommentDto {
    @XmlElement
    @Nullable
    private Date createdDate;

    @XmlElement
    @Nonnull
    @NotEmpty(message = "commentDto.comment.notEmpty")
    private String comment;

    @XmlElement
    @Nullable
    private String user;

    @SuppressWarnings("UnusedDeclaration")
    public PlayerCommentDto() {
    }

    public PlayerCommentDto(final PlayerComment playerComment) {
        comment = playerComment.getComment();
        createdDate = playerComment.getCreatedDate();
        user = playerComment.getUser().getEmailAddress();
    }

    @Nonnull
    public String getComment() {
        return comment;
    }
}
