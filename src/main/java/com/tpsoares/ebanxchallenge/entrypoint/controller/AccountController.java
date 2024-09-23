package com.tpsoares.ebanxchallenge.entrypoint.controller;

import com.tpsoares.ebanxchallenge.core.domain.AccountDomain;
import com.tpsoares.ebanxchallenge.core.usecase.AccountUseCase;
import com.tpsoares.ebanxchallenge.entrypoint.dto.BalanceResponse;
import com.tpsoares.ebanxchallenge.entrypoint.dto.DepositResponse;
import com.tpsoares.ebanxchallenge.entrypoint.dto.EventRequest;
import com.tpsoares.ebanxchallenge.entrypoint.dto.TransferResponse;
import com.tpsoares.ebanxchallenge.entrypoint.dto.WithdrawResponse;
import com.tpsoares.ebanxchallenge.entrypoint.enums.TransactionType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/")
public class AccountController {

    private final AccountUseCase accountUseCase;

    public AccountController(AccountUseCase accountUseCase) {
        this.accountUseCase = accountUseCase;
    }

    @GetMapping("/balance")
    public ResponseEntity<Integer> getBalance(@RequestParam String account_id) {
        return accountUseCase.getAccountBalance(account_id)
                .map(account -> ResponseEntity.ok(account.getBalance()))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(0));
    }

    @PostMapping("/event")
    public ResponseEntity<?> handleEvent(@RequestBody EventRequest request) {
        TransactionType transactionType = TransactionType.fromString(request.getType());

        switch (transactionType) {
            case DEPOSIT:
                return handleDeposit(request.getDestination(), request.getAmount());
            case WITHDRAW:
                return handleWithdraw(request.getOrigin(), request.getAmount());
            case TRANSFER:
                return handleTransfer(request.getOrigin(), request.getDestination(), request.getAmount());
            default:
                return ResponseEntity.badRequest().body("Invalid transaction type");
        }
    }

    private ResponseEntity<?> handleDeposit(String destinationId, Integer amount) {
        Optional<AccountDomain> account = accountUseCase.deposit(destinationId, amount);

        if (account.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
        }

        DepositResponse depositResponse = DepositResponse.builder()
                .destination(BalanceResponse.builder()
                        .id(account.get().getId())
                        .balance(account.get().getBalance())
                        .build())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(depositResponse);
    }

    private ResponseEntity<?> handleWithdraw(String originId, Integer amount) {
        Optional<AccountDomain> account = accountUseCase.withdraw(originId, amount);

        if (account.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
        }

        WithdrawResponse withdrawResponse = WithdrawResponse.builder()
                .origin(BalanceResponse.builder()
                        .id(account.get().getId())
                        .balance(account.get().getBalance())
                        .build())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(withdrawResponse);
    }

    private ResponseEntity<?> handleTransfer(String originId, String destinationId, Integer amount) {
        var accounts = accountUseCase.transfer(originId, destinationId, amount);

        if (accounts.values().stream().anyMatch(Optional::isEmpty)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TransferResponse.builder()
                        .origin(getBalanceResponse(accounts.get("origin")))
                        .destination(getBalanceResponse(accounts.get("destination")))
                        .build());
    }

    private BalanceResponse getBalanceResponse(Optional<AccountDomain> accountOpt) {
        return accountOpt.map(account -> BalanceResponse.builder()
                        .id(account.getId())
                        .balance(account.getBalance())
                        .build())
                .orElse(BalanceResponse.builder()
                        .id("unknown")
                        .balance(0)
                        .build());
    }

    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        accountUseCase.reset();
        return ResponseEntity.ok("OK");
    }
}
