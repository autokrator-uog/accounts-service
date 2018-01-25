package uk.ac.gla.sed.clients.accountsservice.rest.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class StatementItem {
    private Integer itemNo;
    private BigDecimal amount;
    private String note;

    public StatementItem() {
    }

    public StatementItem(Integer itemNo, BigDecimal amount, String note) {
        this.itemNo = itemNo;
        this.amount = amount;
        this.note = note;
    }

    @JsonProperty
    public Integer getItemNo() {
        return itemNo;
    }

    @JsonProperty
    public void setItemNo(Integer itemNo) {
        this.itemNo = itemNo;
    }

    @JsonProperty
    public BigDecimal getAmount() {
        return amount;
    }

    @JsonProperty
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @JsonProperty
    public String getNote() {
        return note;
    }

    @JsonProperty
    public void setNote(String note) {
        this.note = note;
    }
}
