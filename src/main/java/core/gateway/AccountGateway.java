package core.gateway;

import dataprovider.entity.AccountEntity;

import java.util.Optional;

public interface AccountGateway {
    Optional<AccountEntity> findById(String id);
    AccountEntity save(AccountEntity account);
    void reset();
}