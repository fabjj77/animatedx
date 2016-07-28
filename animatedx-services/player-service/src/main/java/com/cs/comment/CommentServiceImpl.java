package com.cs.comment;

import com.cs.player.Player;
import com.cs.player.PlayerComment;
import com.cs.player.PlayerService;
import com.cs.player.QPlayerComment;
import com.cs.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;

import java.util.Date;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Hadi Movaghar
 */
@Service
@Transactional(isolation = READ_COMMITTED)
public class CommentServiceImpl implements CommentService {
    private final PlayerCommentRepository playerCommentRepository;
    private final PlayerService playerService;

    @Autowired
    public CommentServiceImpl(final PlayerCommentRepository playerCommentRepository, final PlayerService playerService) {
        this.playerCommentRepository = playerCommentRepository;
        this.playerService = playerService;
    }

    @Override
    public PlayerComment addPlayerComment(final Long playerId, final String comment, final User user) {
        final Player player = playerService.getPlayer(playerId);
        final PlayerComment playerComment = new PlayerComment(player, new Date(), comment, user);
        playerCommentRepository.save(playerComment);
        return playerComment;
    }

    @Transactional(propagation = SUPPORTS)
    @Override
    public Page<PlayerComment> getPlayerComments(final Long playerId, final Integer pageNumber, final Integer size) {
        final Player player = playerService.getPlayer(playerId);
        final QSort qSort = new QSort(new OrderSpecifier<>(Order.DESC, QPlayerComment.playerComment.createdDate));
        return playerCommentRepository.findAll(commentQueryByPlayer(player), new PageRequest(pageNumber, size, qSort));
    }

    private BooleanExpression commentQueryByPlayer(final Player player) {
        final QPlayerComment qPlayerComment = QPlayerComment.playerComment;
        return qPlayerComment.player.id.eq(player.getId());
    }
}
