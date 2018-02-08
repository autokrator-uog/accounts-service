package uk.ac.gla.sed.clients.accountsservice.core.events;

import com.eclipsesource.json.Json;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

public class RejectedTransaction extends Event {
    private final String transactionId;
    private final String reason;

    public RejectedTransaction(PendingTransaction transaction, String reason) {
        super("RejectedTransaction", Json.object().asObject(), null);

        this.transactionId = transaction.getTransactionId();
        this.data.set("TransactionID", this.transactionId);

        this.reason = reason;
        this.data.set("Reason", this.reason);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getReason() {
        return reason;
    }
}
