package com.upc.ejercicio.repository;

import com.upc.ejercicio.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Boolean existsByNameCustomerAndNumberAccount(String nameCustomer, String numberAccount);
    Account findByNameCustomer(String nameCustomer);
}
