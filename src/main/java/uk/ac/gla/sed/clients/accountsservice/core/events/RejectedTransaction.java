package uk.ac.gla.sed.clients.accountsservice.core.events;

import com.eclipsesource.json.Json;
import uk.ac.gla.sed.shared.eventbusclient.api.Consistency;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

public class RejectedTransaction extends Event {
    private final String transactionId;
    private final String reason;

    public RejectedTransaction(PendingTransaction transaction, String reason) {
<<<<<<< HEAD
        super("RejectedTransaction", Json.object().asObject(), null);
=======
        super("RejectedTransaction", Json.object().asObject(), new Consistency("TXR-" + transaction.getConsistency().getKey().substring(3), "*"));
>>>>>>> master

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
