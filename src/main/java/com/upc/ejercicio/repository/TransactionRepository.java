package com.upc.ejercicio.repository;

import com.upc.ejercicio.model.Account;
import com.upc.ejercicio.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Boolean existsByCreateDateAndAccount(LocalDate createDate, Account account);
    List<Transaction> findByCreateDateBetween(LocalDate startDate, LocalDate endDate);
    List<Transaction> findByAccount_Id(Long id);
}
