package com.tpsoares.ebanxchallenge.entrypoint.controller;

import com.tpsoares.ebanxchallenge.core.domain.AccountDomain;
import com.tpsoares.ebanxchallenge.core.usecase.AccountUseCase;
import com.tpsoares.ebanxchallenge.entrypoint.dto.BalanceResponse;
import com.tpsoares.ebanxchallenge.entrypoint.dto.DepositResponse;
import com.tpsoares.ebanxchallenge.entrypoint.dto.EventRequest;
import com.tpsoares.ebanxchallenge.entrypoint.dto.TransferResponse;
import com.tpsoares.ebanxchallenge.entrypoint.dto.WithdrawResponse;
import com.tpsoares.ebanxchallenge.entrypoint.enums.TransactionType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AccountController {

    AccountUseCase accountUseCase;

    public AccountController(AccountUseCase accountUseCase) {
        this.accountUseCase = accountUseCase;
    }

    @GetMapping("/balance")
    public BalanceResponse getBalance(@RequestParam String account_id) {
        AccountDomain account = accountUseCase.getAccountBalance(account_id);

        return BalanceResponse.builder()
                .balance(account.getBalance())
                .build();
    }


    @PostMapping("/event")
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
