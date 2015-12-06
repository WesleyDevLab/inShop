package com.akartkam.inShop.service;

import java.util.List;

import org.springframework.validation.Errors;

import com.akartkam.inShop.domain.Account;

public interface AccountService {
	boolean registerAccount(Account account, String password, Errors errors);
	Account getAccountByUsername(String username);
	Account getCurrentAccount();
	List<Account> getAllAccount();
}
