package com.upc.ejercicio.controller;

import com.upc.ejercicio.exception.ValidationException;
import com.upc.ejercicio.model.Account;
import com.upc.ejercicio.repository.AccountRepository;
import com.upc.ejercicio.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank/v1")
public class AccountController {
    private final AccountService _accountService;
    private final AccountRepository _accountRepository;

    public AccountController(AccountService accountService, AccountRepository accountRepository) {
        _accountService = accountService;
        _accountRepository = accountRepository;
    }

    //URL: http://localhost:8080/api/bank/v1/accounts
    //Method: GET
    @Transactional(readOnly = true)
    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return new ResponseEntity<>(_accountRepository.findAll(), HttpStatus.OK);
    }

    //URL: http://localhost:8080/api/bank/v1/accounts/
    //Method: POST
    @Transactional
    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        validateAccount(account);
        existsAccountByNameCustomerAndNumberAccount(account);
        return new ResponseEntity<>(_accountService.createAccount(account), HttpStatus.CREATED);
    }

    //Validation Rules for the Account
    private void validateAccount(Account account) {
        if (account.getNameCustomer() == null || account.getNameCustomer().trim().isEmpty()) {
            throw new ValidationException("El nombre del cliente debe ser obligatorio");
        }
        if (account.getNameCustomer().trim().length() > 30) {
            throw new ValidationException("El nombre del cliente no debe exceder los 30 caracteres");
        }
        if (account.getNumberAccount() == null || account.getNumberAccount().trim().isEmpty()) {
            throw new ValidationException("El numero de cuenta debe ser obligatorio");
        }
        if (account.getNumberAccount().trim().length() != 13) {
            throw new ValidationException("El numero de cuenta debe tener una longitud de 13 caracteres");
        }
    }

    private void existsAccountByNameCustomerAndNumberAccount(Account account) {
        if (_accountRepository.existsByNameCustomerAndNumberAccount(account.getNameCustomer(), account.getNumberAccount())) {
            throw new ValidationException("No se puede registrar la cuenta porque ya existe uno con estos datos");
        }
    }
}
