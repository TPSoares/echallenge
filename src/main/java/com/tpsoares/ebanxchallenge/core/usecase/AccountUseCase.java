package com.tpsoares.ebanxchallenge.core.usecase;

import com.tpsoares.ebanxchallenge.core.exception.AccountNotFoundException;
import com.tpsoares.ebanxchallenge.core.gateway.AccountGateway;
import com.tpsoares.ebanxchallenge.dataprovider.entity.AccountEntity;
import com.tpsoares.ebanxchallenge.core.domain.AccountDomain;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AccountUseCase {
    private final AccountGateway accountGateway;

    public AccountUseCase(AccountGateway accountGateway) {
        this.accountGateway = accountGateway;
    }

    public AccountDomain getAccountBalance(String accountId) {
        AccountEntity account = accountGateway.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        return AccountDomain.builder()
                .id(account.getId())
                .balance(account.getBalance())
                .build();
    }

    public AccountDomain deposit(String accountId, Integer amount) {
        AccountEntity account = accountGateway.findById(accountId)
                .orElseGet(() -> AccountEntity.builder()
                                .id(accountId)
                                .balance(amount)
                                .build()
                );

        account.setBalance(account.getBalance() + amount);
        accountGateway.save(account);

        return AccountDomain.builder()
                .id(account.getId())
                .balance(account.getBalance())
                .build();
    }

    public AccountDomain withdraw(String accountId, Integer amount) {
        AccountEntity account = accountGateway.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        account.setBalance(account.getBalance() - amount);
        accountGateway.save(account);

        return AccountDomain.builder()
                .id(account.getId())
                .balance(account.getBalance())
                .build();
    }

    public Map<String, AccountDomain> transfer(String originId, String destinationId, Integer amount) {
        AccountDomain origin = withdraw(originId, amount);
        AccountDomain destination = deposit(destinationId, amount);

        return Map.of("origin", origin, "destination", destination);
    }

    public void reset() {
        accountGateway.reset();
    }
}
