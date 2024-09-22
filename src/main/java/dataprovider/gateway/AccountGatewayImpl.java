package dataprovider.gateway;

import core.gateway.AccountGateway;
import dataprovider.entity.AccountEntity;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountGatewayImpl implements AccountGateway {

    private final Map<String, AccountEntity> accountStore = new ConcurrentHashMap<>();

    @Override
    public Optional<AccountEntity> findById(String id) {
        return Optional.ofNullable(accountStore.get(id));
    }

    @Override
    public AccountEntity save(AccountEntity account) {
        accountStore.put(account.getId(), account);
        return account;
    }

    @Override
    public void reset() {
        accountStore.clear(); // Clear all data for /reset endpoint
    }
}

