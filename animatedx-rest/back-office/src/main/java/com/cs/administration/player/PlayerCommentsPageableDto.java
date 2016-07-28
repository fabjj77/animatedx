package com.cs.administration.player;

import com.cs.player.PlayerComment;

import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author Hadi Movaghar
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class PlayerCommentsPageableDto {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement
    @Nonnull
    private List<PlayerCommentDto> playerComments;

    @XmlElement
    @Nonnull
    private Long count;

    @SuppressWarnings("UnusedDeclaration")
    public PlayerCommentsPageableDto() {
    }

    public PlayerCommentsPageableDto(final Page<PlayerComment> playerComments) {
        this.playerComments = new ArrayList<>(playerComments.getSize());
        for (final PlayerComment playerComment : playerComments) {
            this.playerComments.add(new PlayerCommentDto(playerComment));
        }
        count = playerComments.getTotalElements();
    }
}
