package com.cs.casino.payment;

import com.cs.audit.AuditService;
import com.cs.audit.PlayerActivityType;
import com.cs.bonus.BonusService;
import com.cs.bonus.PlayerBonus;
import com.cs.casino.bonus.CasinoPlayerBonusDto;
import com.cs.casino.security.CurrentPlayer;
import com.cs.casino.security.PlayerUser;
import com.cs.payment.BankAccountDto;
import com.cs.payment.DCPaymentTransaction;
import com.cs.payment.DepositDetails;
import com.cs.payment.EventCode;
import com.cs.payment.MethodDto;
import com.cs.payment.PaymentMethod;
import com.cs.payment.PaymentPageableDto;
import com.cs.payment.PaymentService;
import com.cs.payment.PaymentStatus;
import com.cs.payment.PaymentTransaction;
import com.cs.payment.PayoutDetailReference;
import com.cs.payment.devcode.DevcodePaymentService;
import com.cs.payment.transaction.PaymentTransactionFacade;
import com.cs.player.Player;
import com.cs.player.PlayerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cs.persistence.Constants.CASINO_DEFAULT_SIZE;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Omid Alaepour
 */
@RestController
@RequestMapping(value = "/api/payments", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class PaymentController {

    private final AuditService auditService;
    private final BonusService bonusService;
    private final DevcodePaymentService devcodePaymentService;
    private final PaymentService paymentService;
    private final PaymentTransactionFacade paymentTransactionFacade;
    private final PlayerService playerService;

    @Autowired
    public PaymentController(final AuditService auditService, final BonusService bonusService, final DevcodePaymentService devcodePaymentService,
                             final PaymentService paymentService,
                             final PaymentTransactionFacade paymentTransactionFacade, final PlayerService playerService) {
        this.auditService = auditService;
        this.bonusService = bonusService;
        this.devcodePaymentService = devcodePaymentService;
        this.paymentService = paymentService;
        this.paymentTransactionFacade = paymentTransactionFacade;
        this.playerService = playerService;
    }

    @RequestMapping(method = GET)
    @ResponseStatus(OK)
    public PaymentPageableDto getTransactionHistory(@CurrentPlayer final PlayerUser currentPlayer,
                                                    @RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date startDate,
                                                    @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date endDate,
                                                    @RequestParam(value = "code", required = false) final EventCode eventCode,
                                                    @RequestParam(value = "status", required = false) final PaymentStatus paymentStatus,
                                                    @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                    @RequestParam(value = "size", required = false, defaultValue = CASINO_DEFAULT_SIZE) final Integer size) {

//        final Page<PaymentTransaction> transactions = paymentTransactionFacade.getPayments(currentPlayer.getId(), eventCode, paymentStatus, startDate, endDate, page,
//                                                                                           size);
//        final Page<DCPaymentTransaction> dcPaymentTransactions = devcodePaymentService.getPayments(currentPlayer.getId(), startDate, endDate, page, size);

        // TODO:Joakim Remove the size parameter when Adyen is removed form the system, the size is necessary for returning the correct number of elements
        final Page<PaymentTransaction> transactions = paymentTransactionFacade.getPayments(currentPlayer.getId(), eventCode, paymentStatus, startDate, endDate, 0,
                                                                                           1000);
        final Page<DCPaymentTransaction> dcPaymentTransactions = devcodePaymentService.getPayments(currentPlayer.getId(), startDate, endDate, 0, 1000);
        return new PaymentPageableDto(transactions, dcPaymentTransactions, page, size);
    }

    @RequestMapping(method = GET, value = "/methods")
    @ResponseStatus(HttpStatus.OK)
    public List<MethodDto> getPaymentMethods(@CurrentPlayer final PlayerUser currentPlayer) {
        final Player player = playerService.getPlayer(currentPlayer.getId());
        final List<PaymentMethod> allowedPaymentMethods = paymentService.getAllowedPaymentMethodsByCountry(player.getAddress().getCountry());
        return convert(allowedPaymentMethods);
    }

    private List<MethodDto> convert(final List<PaymentMethod> allowedPaymentMethodTypes) {
        final List<MethodDto> methods = new ArrayList<>();
        for (final PaymentMethod allowedPaymentMethod : allowedPaymentMethodTypes) {
            methods.add(new MethodDto(allowedPaymentMethod));
        }
        return methods;
    }

    @SuppressWarnings("AccessStaticViaInstance")
    @RequestMapping(method = GET, value = "/withdraw")
    @ResponseStatus(OK)
    public WithdrawStoreDto getPlayerWithdrawDetail(@CurrentPlayer final PlayerUser currentPlayer, @RequestHeader("X-Forwarded-For") final String host) {
        final Player player = playerService.getPlayer(currentPlayer.getId());

        auditService.trackPlayerActivityWithIpAddress(player, PlayerActivityType.REQ_GET_WITHDRAW_DETAILS, host);
        final List<PayoutDetailReference> references = paymentService.sendPayoutInformationRequest(player);
        final boolean playerAllowedToStoreBankAccount = paymentService.isPlayerAllowedToStoreBankAccount(references);
        final List<PlayerBonus> voidablePlayerBonuses = bonusService.getVoidablePlayerBonuses(player);
        return new WithdrawStoreDto(WithdrawDetailDto.getWithdrawDetailList(references), playerAllowedToStoreBankAccount, CasinoPlayerBonusDto.of(voidablePlayerBonuses));
    }

    @RequestMapping(method = POST, value = "/withdraw")
    @ResponseStatus(NO_CONTENT)
    public void withdraw(@CurrentPlayer final PlayerUser currentPlayer, @RequestBody(required = true) @Valid final WithdrawDto withdrawDto,
                         @RequestHeader("X-Forwarded-For") final String host) {
        auditService.trackPlayerActivityWithIpAddress(playerService.getPlayer(currentPlayer.getId()), PlayerActivityType.REQ_SEND_WITHDRAW_REQUEST, host);
        paymentService.withdraw(currentPlayer.getId(), withdrawDto.getWithdrawReference(), withdrawDto.getPaymentMethod(), withdrawDto.getAmountInCents(),
                                withdrawDto.getPassword());
    }

    @RequestMapping(method = POST, value = "/deposit")
    @ResponseStatus(OK)
    public DepositDto prepareDeposit(@CurrentPlayer final PlayerUser currentPlayer, @RequestBody @Valid final DepositRequestDto deposit,
                                     @RequestHeader("X-Forwarded-For") final String host) {
        auditService.trackPlayerActivityWithIpAddress(playerService.getPlayer(currentPlayer.getId()), PlayerActivityType.REQ_SEND_DEPOSIT_REQUEST, host);
        final DepositDetails depositDetails = paymentService.prepareDeposit(currentPlayer.getId(), deposit.getAmount(), deposit.getSkin(), deposit.getPaymentMethod(),
                                                                            deposit.getBonusId());
        return new DepositDto(depositDetails);
    }

    @RequestMapping(method = POST, value = "/bank")
    @ResponseStatus(OK)
    public void storeBankAccountForWithdrawal(@CurrentPlayer final PlayerUser currentPlayer, @RequestBody @Valid final BankAccountDto bankAccountDto,
                                              @RequestHeader("X-Forwarded-For") final String host) {
        final Player player = playerService.getPlayer(currentPlayer.getId());
        auditService.trackPlayerActivityWithIpAddress(playerService.getPlayer(currentPlayer.getId()), PlayerActivityType.REQ_STORE_BANK_ACCOUNT_FOR_WITHDRAW, host);
        paymentService.storeBankAccountForWithdrawal(player, bankAccountDto.getIban(), bankAccountDto.getBic(), bankAccountDto.getBankName(), bankAccountDto.getName());
    }
}
