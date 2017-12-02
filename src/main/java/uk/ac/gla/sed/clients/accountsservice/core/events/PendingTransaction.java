package uk.ac.gla.sed.clients.accountsservice.core.events;

import com.eclipsesource.json.Json;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

import java.math.BigDecimal;

public class PendingTransaction extends Event {
    private String transactionId;
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;

    public PendingTransaction(Event e) throws IllegalArgumentException {
        if (!e.getType().equals("PendingTransaction")) {
            throw new IllegalArgumentException("Event must be a PendingTransaction...");
        }

        this.type = e.getType();

        this.data = Json.object().asObject();
        this.data.merge(e.getData());

        this.transactionId = data.getString("TransactionID", "");
        this.fromAccountId = data.getString("FromAccountID", "");
        this.toAccountId = data.getString("ToAccountID", "");
        this.amount = new BigDecimal(data.getString("Amount", "0"));
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
