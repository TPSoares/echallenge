package core.usecase;

import core.domain.AccountDomain;
import core.exception.AccountNotFoundException;
import core.gateway.AccountGateway;
import dataprovider.entity.AccountEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AccountUseCase {
    private final AccountGateway accountGateway;

    public AccountUseCase(AccountGateway accountGateway) {
        this.accountGateway = accountGateway;
    }

    public AccountDomain deposit(String accountId, Integer amount) {
        AccountEntity account = accountGateway.findById(accountId)
                .orElseGet(() -> new AccountEntity(accountId, 0));

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
