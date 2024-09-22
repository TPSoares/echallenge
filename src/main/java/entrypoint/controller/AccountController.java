package entrypoint.controller;

import core.domain.AccountDomain;
import core.usecase.AccountUseCase;
import entrypoint.dto.BalanceResponse;
import entrypoint.dto.DepositResponse;
import entrypoint.dto.EventRequest;
import entrypoint.dto.TransferResponse;
import entrypoint.dto.WithdrawResponse;
import entrypoint.enums.TransactionType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class AccountController {

    AccountUseCase accountUseCase;

    public AccountController(AccountUseCase accountUseCase) {
        this.accountUseCase = accountUseCase;
    }

    @PostMapping
    public ResponseEntity<?> handleEvent(@RequestBody EventRequest request) {

        TransactionType transactionType = TransactionType.fromString(request.getType());

        if (transactionType == TransactionType.DEPOSIT) {
            AccountDomain account = accountUseCase.deposit(request.getOrigin(), request.getAmount());
            return ResponseEntity.ok(DepositResponse.builder()
                    .destination(
                            BalanceResponse.builder()
                                    .id(account.getId())
                                    .balance(account.getBalance())
                                    .build())
                    .build()
            );
        } else if (transactionType == TransactionType.WITHDRAW) {
            AccountDomain account = accountUseCase.withdraw(request.getOrigin(), request.getAmount());
            return ResponseEntity.ok(WithdrawResponse.builder()
                    .origin(
                            BalanceResponse.builder()
                                    .id(account.getId())
                                    .balance(account.getBalance())
                                    .build())
                    .build()
            );
        } else if (transactionType == TransactionType.TRANSFER) {
            var accounts = accountUseCase.transfer(request.getOrigin(), request.getDestination(), request.getAmount());
            return ResponseEntity.ok(TransferResponse.builder()
                    .origin(
                            BalanceResponse.builder()
                                    .id(accounts.get("origin").getId())
                                    .balance(accounts.get("origin").getBalance())
                                    .build())
                    .destination(
                            BalanceResponse.builder()
                                    .id(accounts.get("destination").getId())
                                    .balance(accounts.get("destination").getBalance())
                                    .build())
                    .build()
            );
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/reset")
    public ResponseEntity<?> reset() {
        accountUseCase.reset();
        return ResponseEntity.ok().build();
    }
}
