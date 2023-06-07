package com.upc.ejercicio.service.implement;

import com.upc.ejercicio.model.Account;
import com.upc.ejercicio.repository.AccountRepository;
import com.upc.ejercicio.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository repository;

    @Override
    public Account createAccount(Account account) {
        return repository.save(account);
    }
}
