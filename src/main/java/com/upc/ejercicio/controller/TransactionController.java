package com.upc.ejercicio.controller;

import com.upc.ejercicio.exception.ResourceNotFoundException;
import com.upc.ejercicio.exception.ValidationException;
import com.upc.ejercicio.model.Account;
import com.upc.ejercicio.model.Transaction;
import com.upc.ejercicio.repository.AccountRepository;
import com.upc.ejercicio.repository.TransactionRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bank/v1")
public class TransactionController {
    private final AccountRepository _accountRepository;
    private final TransactionRepository _transactionRepository;

    public TransactionController(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        _accountRepository = accountRepository;
        _transactionRepository = transactionRepository;
    }

    //URL: http://localhost:8080/api/bank/v1/accounts/accountId/transactions
    //Method: POST
    @Transactional
    @PostMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<Transaction> createTransaction(@PathVariable(value = "accountId") Long accountId, @RequestBody Transaction transaction) {
        Account account = _accountRepository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("No se encontro la cuenta con el id: " + accountId));

        existsByCreateDateAndAccount(transaction, account);
        validateTransaction(transaction);
        validationRulesType(transaction);
        transaction.setCreateDate(LocalDate.now());

        return new ResponseEntity<>(_transactionRepository.save(transaction), HttpStatus.CREATED);
    }

    //URL: http://localhost:8080/api/bank/v1/transactions/filterByNameCustomer?nameCustomer=Luis
    //Method: GET
    @Transactional(readOnly = true)
    @GetMapping("/transactions/filterByNameCustomer")
    public ResponseEntity<List<Transaction>> getTransactionByNameCustomer(@RequestParam(name = "nameCustomer") String nameCustomer) {
        Account account = _accountRepository.findByNameCustomer(nameCustomer);

        if (account == null) {
            throw new ValidationException("No se encontro la cuenta con el nombre del cliente: " + nameCustomer);
        }
        Long accountId = account.getId();

        return new ResponseEntity<>(_transactionRepository.findByAccount_Id(accountId), HttpStatus.OK);
    }

    //URL: http://localhost:8080/api/bank/v1/transactions/filterByCreateDateRange?startDate=2022-11-28&endDate=2022-11-29
    //Method: GET
    @Transactional(readOnly = true)
    @GetMapping("/transactions/filterByCreateDateRange")
    public ResponseEntity<List<Transaction>> getTransactionByCreateDateRange(@RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                             @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Transaction> transactions = _transactionRepository.findByCreateDateBetween(startDate, endDate);

        if (transactions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron transacciones en el rango de fechas especificado.");
        }
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    //Validation Rules for the Transaction
    private void validateTransaction(Transaction transaction) {
        if (transaction.getType() == null || transaction.getType().trim().isEmpty()) {
            throw new ValidationException("El tipo de transacción bancaria debe ser obligatorio");
        }
        if (transaction.getAmount() <= 0.0) {
            throw new ValidationException("El monto en una transacción bancaria debe ser mayor de S/.0.0");
        }
        if (!transaction.getType().equalsIgnoreCase("Retiro") && !transaction.getType().equalsIgnoreCase("Deposito")) {
            throw new ValidationException("El tipo de transacción bancaria debe ser Retiro o Deposito");
        }
        if (transaction.getAmount() > transaction.getBalance() && transaction.getType().equalsIgnoreCase("Retiro")) {
            throw new ValidationException("En una transacción bancaria tipo retiro el monto no puede ser mayor al saldo");
        }
    }

    private void existsByCreateDateAndAccount(Transaction transaction, Account account) {
        if (_transactionRepository.existsByCreateDateAndAccount(transaction.getCreateDate(), account)) {
            throw new ValidationException("Ya existe una transacción bancaria con la fecha de creación: " + transaction.getCreateDate() + " y la cuenta: " + account.getId());
        }
    }

    private void validationRulesType(Transaction transaction) {
        if (transaction.getType().equalsIgnoreCase("Retiro")) {
            transaction.setBalance(transaction.getBalance() - transaction.getAmount());
        }
        else {
            transaction.setBalance(transaction.getBalance() + transaction.getAmount());
        }
    }
}
