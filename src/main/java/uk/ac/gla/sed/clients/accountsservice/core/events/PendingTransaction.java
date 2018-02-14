package uk.ac.gla.sed.clients.accountsservice.core.events;

import com.eclipsesource.json.Json;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

import java.math.BigDecimal;

public class PendingTransaction extends Event {
    private String transactionId;
    private Integer fromAccountId;
    private Integer toAccountId;
    private BigDecimal amount;

    public PendingTransaction(Event e) throws IllegalArgumentException {
<<<<<<< HEAD
        super(e.getType(), Json.object().asObject().merge(e.getData()), null);
=======
        super(e.getType(), Json.object().asObject().merge(e.getData()), e.getConsistency());
>>>>>>> master

        if (!type.equals("PendingTransaction")) {
            throw new IllegalArgumentException("Event must be a PendingTransaction...");
        }

        this.transactionId = data.getString("TransactionID", "");
        this.fromAccountId = data.getInt("FromAccountID", -1);
        this.toAccountId = data.getInt("ToAccountID", -1);
        this.amount = new BigDecimal(data.getString("Amount", "0"));
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Integer getFromAccountId() {
        return fromAccountId;
    }

    public Integer getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
