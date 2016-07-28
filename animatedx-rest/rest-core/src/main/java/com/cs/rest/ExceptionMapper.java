package com.cs.rest;

import com.cs.avatar.AvatarChangeException;
import com.cs.game.PlayerVerificationException;
import com.cs.payment.IncorrectBankAccountException;
import com.cs.payment.InvalidCreditAmountException;
import com.cs.payment.InvalidOriginalPaymentException;
import com.cs.payment.PaymentAmountException;
import com.cs.payment.WithdrawalFailedException;
import com.cs.payment.devcode.DCInvalidAuthorizationCodeException;
import com.cs.payment.devcode.DCInvalidPlayerException;
import com.cs.payment.devcode.DCInvalidSessionException;
import com.cs.payment.devcode.DCPlayerDetailException;
import com.cs.payment.devcode.DCTransactionAmountException;
import com.cs.persistence.AlreadyAssignedException;
import com.cs.persistence.BonusException;
import com.cs.persistence.CommunicationException;
import com.cs.persistence.ExpiredEntityException;
import com.cs.persistence.IllegalOperationException;
import com.cs.persistence.InvalidArgumentException;
import com.cs.persistence.InvalidPasswordException;
import com.cs.persistence.IpBlockedException;
import com.cs.persistence.NotFoundException;
import com.cs.player.BlockedPlayerException;
import com.cs.player.PlayerCreationException;
import com.cs.player.PlayerLimitationException;
import com.cs.rest.status.AccessDeniedMessage;
import com.cs.rest.status.AlreadyAssignedMessage;
import com.cs.rest.status.AvatarChangeMessage;
import com.cs.rest.status.BlockedPlayerMessage;
import com.cs.rest.status.CommunicationMessage;
import com.cs.rest.status.DCInvalidAuthorizationCodeMessage;
import com.cs.rest.status.DCInvalidPlayerMessage;
import com.cs.rest.status.DCInvalidSessionMessage;
import com.cs.rest.status.DCPlayerDetailMessage;
import com.cs.rest.status.DCTransactionAmountMessage;
import com.cs.rest.status.ExpiredEntityMessage;
import com.cs.rest.status.IllegalBonusMessage;
import com.cs.rest.status.IllegalOperationMessage;
import com.cs.rest.status.IllegalStateMessage;
import com.cs.rest.status.IncorrectBankAccountMessage;
import com.cs.rest.status.InvalidArgumentMessage;
import com.cs.rest.status.InvalidCreditAmountMessage;
import com.cs.rest.status.InvalidOriginalPaymentMessage;
import com.cs.rest.status.InvalidPasswordMessage;
import com.cs.rest.status.InvalidSessionMessage;
import com.cs.rest.status.IpBlockedMessage;
import com.cs.rest.status.NotFoundMessage;
import com.cs.rest.status.PaymentAmountMessage;
import com.cs.rest.status.PlayerCreationMessage;
import com.cs.rest.status.PlayerLimitationMessage;
import com.cs.rest.status.PlayerVerificationMessage;
import com.cs.rest.status.SessionExpiredMessage;
import com.cs.rest.status.ValidationMessage;
import com.cs.rest.status.WithdrawalFailedMessage;
import com.cs.security.AccessDeniedException;
import com.cs.security.InvalidSessionException;
import com.cs.security.SessionExpiredException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * @author Joakim Gottzén
 */
@ControllerAdvice
public class ExceptionMapper {

    private final Logger logger = LoggerFactory.getLogger(ExceptionMapper.class);

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public NotFoundMessage handleNotFoundException(final NotFoundException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return NotFoundMessage.of(exception);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public InvalidPasswordMessage handleInvalidPasswordException(final InvalidPasswordException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return InvalidPasswordMessage.of(exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ValidationMessage handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return ValidationMessage.of(exception);
    }

    @ExceptionHandler(InvalidArgumentException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public InvalidArgumentMessage handleIllegalArgumentException(final InvalidArgumentException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return InvalidArgumentMessage.of(exception);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public IllegalStateMessage handleIllegalStateException(final IllegalStateException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return IllegalStateMessage.of(exception);
    }

    @ExceptionHandler(BonusException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public IllegalBonusMessage handleBonusException(final BonusException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return IllegalBonusMessage.of(exception);
    }

    @ExceptionHandler(BlockedPlayerException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public BlockedPlayerMessage handleBlockedPlayerException(final BlockedPlayerException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return BlockedPlayerMessage.of(exception);
    }

    @ExceptionHandler(PlayerLimitationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public PlayerLimitationMessage handlePlayerLimitationException(final PlayerLimitationException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return PlayerLimitationMessage.of(exception);
    }

    @ExceptionHandler(CommunicationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public CommunicationMessage handleCommunicationException(final CommunicationException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return CommunicationMessage.of(exception);
    }

    @ExceptionHandler(InvalidOriginalPaymentException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public InvalidOriginalPaymentMessage handleInvalidOriginalPaymentException(final InvalidOriginalPaymentException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return InvalidOriginalPaymentMessage.of(exception);
    }

    @ExceptionHandler(PaymentAmountException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public PaymentAmountMessage handlePaymentAmountException(final PaymentAmountException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return PaymentAmountMessage.of(exception);
    }

    @ExceptionHandler(PlayerCreationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public PlayerCreationMessage handlePlayerCreationException(final PlayerCreationException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return PlayerCreationMessage.of(exception);
    }

    @ExceptionHandler(SessionExpiredException.class)
    @ResponseStatus(UNAUTHORIZED)
    @ResponseBody
    public SessionExpiredMessage handleSessionExpiredException(final SessionExpiredException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return SessionExpiredMessage.of(exception);
    }

    @ExceptionHandler(InvalidSessionException.class)
    @ResponseStatus(UNAUTHORIZED)
    @ResponseBody
    public InvalidSessionMessage handleInvalidSessionException(final InvalidSessionException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return InvalidSessionMessage.of(exception);
    }

    @ExceptionHandler(PlayerVerificationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public PlayerVerificationMessage handlePlayerVerificationException(final PlayerVerificationException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return PlayerVerificationMessage.of(exception);
    }

    @ExceptionHandler(ExpiredEntityException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ExpiredEntityMessage handleExpiredEntityException(final ExpiredEntityException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return ExpiredEntityMessage.of(exception);
    }

    @ExceptionHandler(AlreadyAssignedException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public AlreadyAssignedMessage handleAlreadyAssignedException(final AlreadyAssignedException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return AlreadyAssignedMessage.of(exception);
    }

    @ExceptionHandler(IncorrectBankAccountException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public IncorrectBankAccountMessage handleIncorrectBankAccountException(final IncorrectBankAccountException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return IncorrectBankAccountMessage.of(exception);
    }

    @ExceptionHandler(IllegalOperationException.class)
    @ResponseStatus(UNAUTHORIZED)
    @ResponseBody
    public IllegalOperationMessage handleIllegalOperationException(final IllegalOperationException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return IllegalOperationMessage.of(exception);
    }

    @ExceptionHandler(AvatarChangeException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public AvatarChangeMessage handleAvatarChangeException(final AvatarChangeException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return AvatarChangeMessage.of(exception);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(FORBIDDEN)
    @ResponseBody
    public AccessDeniedMessage handleAccessDeniedException(final AccessDeniedException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return AccessDeniedMessage.of(exception);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ValidationMessage handleViolationException(final ConstraintViolationException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return ValidationMessage.of(exception);
    }

    @ExceptionHandler(WithdrawalFailedException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public WithdrawalFailedMessage handleWithdrawalFailedException(final WithdrawalFailedException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return WithdrawalFailedMessage.of(exception);
    }

    @ExceptionHandler(InvalidCreditAmountException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public InvalidCreditAmountMessage handleInvalidCreditAmountException(final InvalidCreditAmountException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return InvalidCreditAmountMessage.of(exception);
    }

    @ExceptionHandler(IpBlockedException.class)
    @ResponseStatus(FORBIDDEN)
    @ResponseBody
    public IpBlockedMessage handleBlockedIpAddressException(final IpBlockedException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return IpBlockedMessage.of(exception);
    }

    @ExceptionHandler(DCInvalidPlayerException.class)
    @ResponseStatus(OK)
    @ResponseBody
    public DCInvalidPlayerMessage handleDCInvalidPlayerException(final DCInvalidPlayerException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return DCInvalidPlayerMessage.of(exception);
    }

    @ExceptionHandler(DCInvalidSessionException.class)
    @ResponseStatus(OK)
    @ResponseBody
    public DCInvalidSessionMessage handleDCInvalidSessionException(final DCInvalidSessionException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return DCInvalidSessionMessage.of(exception);
    }

    @ExceptionHandler(DCPlayerDetailException.class)
    @ResponseStatus(OK)
    @ResponseBody
    public DCPlayerDetailMessage handleDCPlayerDetailException(final DCPlayerDetailException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return DCPlayerDetailMessage.of(exception);
    }

    @ExceptionHandler(DCTransactionAmountException.class)
    @ResponseStatus(OK)
    @ResponseBody
    public DCTransactionAmountMessage handleDCTransactionAmountException(final DCTransactionAmountException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return DCTransactionAmountMessage.of(exception);
    }

    @ExceptionHandler(DCInvalidAuthorizationCodeException.class)
    @ResponseStatus(OK)
    @ResponseBody
    public DCInvalidAuthorizationCodeMessage handleDCInvalidAuthorizationCodeException(final DCInvalidAuthorizationCodeException exception) {
        logger.debug("Handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return DCInvalidAuthorizationCodeMessage.of(exception);
    }
}
