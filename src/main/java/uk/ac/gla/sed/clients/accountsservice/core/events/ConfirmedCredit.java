package uk.ac.gla.sed.clients.accountsservice.core.events;

import com.eclipsesource.json.Json;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

import java.math.BigDecimal;
import java.util.Date;

public class ConfirmedCredit extends Event {
    private Integer accountId;
    private BigDecimal amount;

    public ConfirmedCredit(AcceptedTransaction transaction) {
        super("ConfirmedCredit", Json.object().asObject());

        this.accountId = transaction.getTransaction().getToAccountId();
        this.amount = transaction.getTransaction().getAmount();

        this.data.set("AccountID", this.accountId);
        this.data.set("Amount", this.amount.toString());
        this.data.set("Date", new Date().toString());
    }

    public ConfirmedCredit(Event e) {
        super(e.getType(), Json.object().asObject().merge(e.getData()));

        if (!type.equals("ConfirmedCredit")) {
            throw new IllegalArgumentException("Event must be a ConfirmedCredit...");
        }

        this.accountId = this.data.getInt("AccountID", -1);
        if (this.accountId == -1) {
            throw new IllegalArgumentException("AccountID invalid!");
        }

        this.amount = new BigDecimal(this.data.getString("Amount", "-1"));
        if (this.amount.equals(new BigDecimal(-1))) {
            throw new IllegalArgumentException("Amount invalid!");
        }
    }

    public Integer getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
