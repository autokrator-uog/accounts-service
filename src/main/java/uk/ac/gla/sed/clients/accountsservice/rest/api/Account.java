package uk.ac.gla.sed.clients.accountsservice.rest.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Account {
    private Integer accountId;

    private BigDecimal balance;

    public Account() {}

    public Account(Integer accountId, BigDecimal balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    @JsonProperty
    public Integer getAccountId() {
        return accountId;
    }

    @JsonProperty
    public BigDecimal getBalance() {
        return balance;
    }
}
