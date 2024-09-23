package com.tpsoares.ebanxchallenge.core.gateway;

import com.tpsoares.ebanxchallenge.dataprovider.entity.AccountEntity;

import java.util.Optional;

public interface AccountGateway {
    Optional<AccountEntity> findById(String id);
    AccountEntity save(AccountEntity account);
    void reset();
}