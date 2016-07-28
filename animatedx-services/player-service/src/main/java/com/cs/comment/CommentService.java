package com.cs.comment;

import com.cs.player.PlayerComment;
import com.cs.user.User;

import org.springframework.data.domain.Page;

/**
 * @author Hadi Movaghar
 */
public interface CommentService {
    PlayerComment addPlayerComment(final Long playerId, final String comment, final User user);

    Page<PlayerComment> getPlayerComments(final Long playerId, final Integer pageNumber, final Integer size);
}
