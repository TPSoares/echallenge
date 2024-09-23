package com.tpsoares.ebanxchallenge.dataprovider.gateway;

import com.tpsoares.ebanxchallenge.core.gateway.AccountGateway;
import com.tpsoares.ebanxchallenge.dataprovider.entity.AccountEntity;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountGatewayImpl implements AccountGateway {

    private final Map<String, AccountEntity> accountStore = new ConcurrentHashMap<>();

    @Override
    public Optional<AccountEntity> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(accountStore.get(id));
    }

    @Override
    public AccountEntity save(AccountEntity account) {
        if (account == null || account.getId() == null) {
            throw new IllegalArgumentException();
        }
        accountStore.put(account.getId(), account);
        return account;
    }

    @Override
    public void reset() {
        accountStore.clear();
    }
}

