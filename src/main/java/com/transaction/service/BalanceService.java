package com.transaction.service;

import com.transaction.models.Balance;
import com.transaction.models.Bank;
import com.transaction.models.Currency;
import com.transaction.repository.BalanceRepository;
import com.transaction.repository.BankRepository;
import com.transaction.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BalanceService {

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    public void changeBalance(Integer user_id,Integer transaction_type, BigDecimal value,
                              Integer bank_id, String currency_code,
                              LocalDateTime time){

        Optional<Bank> bank = bankRepository.findById(bank_id);
        Optional<Currency> currency = currencyRepository.findById(currency_code);
        if (currency.isPresent() && bank.isPresent()){
            Optional<Balance> optionalBalance = balanceRepository.findByUser_idAndCurrencyAndBankAnd(user_id, currency, bank);
            if (!optionalBalance.isPresent()) {
                throw new IllegalArgumentException("Balance not found for user_id: " + user_id + ", bank_id: " + bank_id + ", currency_code: " + currency_code);
            }

            Balance balance = optionalBalance.get();
            if (transaction_type == 1) {
                balance.setAmount(balance.getAmount().add(value));
            } else if (transaction_type == 2) {
                if (balance.getAmount().compareTo(value) < 0) {
                    throw new IllegalArgumentException("Insufficient funds");
                }
                balance.setAmount(balance.getAmount().subtract(value));
            }

            balance.setLast_updated(time);

            balanceRepository.save(balance);
        }
    }
}
