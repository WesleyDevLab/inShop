package com.akartkam.inShop.service;

import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import com.akartkam.inShop.domain.Account;

@RunWith(MockitoJUnitRunner.class)
@Component("mockAccountAuditorAwareImpl")
public class MockAccountAuditorAwareImpl implements AuditorAware<Account> {

    @Mock 
    private AccountService accountService;

    @Override
    public Account getCurrentAuditor() {
    	Account account = new Account();
    	account.setId(UUID.fromString("0b90130d-2c19-4001-b61a-ef100d7b950e"));
    	account.setFirstName("Artur");
    	account.setLastName("Akchurin");
    	account.setMiddleName("Kamilevich");
    	account.setUsername("akartkam");
    	when(accountService.getCurrentAccount()).thenReturn(account);
        return accountService.getCurrentAccount();
    }
}
