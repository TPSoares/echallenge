package com.tpsoares.ebanxchallenge.core.usecase;

import com.tpsoares.ebanxchallenge.core.gateway.AccountGateway;
import com.tpsoares.ebanxchallenge.dataprovider.entity.AccountEntity;
import com.tpsoares.ebanxchallenge.core.domain.AccountDomain;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AccountUseCase {

    private final AccountGateway accountGateway;

    public AccountUseCase(AccountGateway accountGateway) {
        this.accountGateway = accountGateway;
    }

    public Optional<AccountDomain> getAccountBalance(String accountId) {
        return accountGateway.findById(accountId)
                .map(account -> AccountDomain.builder()
                        .id(account.getId())
                        .balance(account.getBalance())
                        .build());
    }

    public Optional<AccountDomain> deposit(String accountId, Integer amount) {
        AccountEntity account = accountGateway.findById(accountId)
                .orElseGet(() -> AccountEntity.builder()
                        .id(accountId)
                        .balance(0)
                        .build());

        account.setBalance(account.getBalance() + amount);
        accountGateway.save(account);

        return Optional.of(AccountDomain.builder()
                .id(account.getId())
                .balance(account.getBalance())
                .build());
    }

    public Optional<AccountDomain> withdraw(String accountId, Integer amount) {
        return accountGateway.findById(accountId)
                .map(accountEntity -> {
                    accountEntity.setBalance(accountEntity.getBalance() - amount);
                    accountGateway.save(accountEntity);
                    return AccountDomain.builder()
                            .id(accountEntity.getId())
                            .balance(accountEntity.getBalance())
                            .build();
                });
    }

    public Map<String, Optional<AccountDomain>> transfer(String originId, String destinationId, Integer amount) {
        Optional<AccountDomain> origin = withdraw(originId, amount);
        Optional<AccountDomain> destination = deposit(destinationId, amount);

        return Map.of("origin", origin, "destination", destination);
    }

    public void reset() {
        accountGateway.reset();
    }
}
