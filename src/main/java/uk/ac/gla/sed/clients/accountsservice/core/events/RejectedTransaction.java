package uk.ac.gla.sed.clients.accountsservice.core.events;

import com.eclipsesource.json.Json;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

public class RejectedTransaction extends Event {
    private String transactionId;
    private String reason;

    public RejectedTransaction(PendingTransaction transaction, String reason) {
        this.transactionId = transaction.getTransactionId();
        this.reason = reason;

        this.type = "RejectedTransaction";

        this.data = Json.object().asObject();
        this.data.set("TransactionID", this.transactionId);
        this.data.set("Reason", this.reason);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getReason() {
        return reason;
    }
}
