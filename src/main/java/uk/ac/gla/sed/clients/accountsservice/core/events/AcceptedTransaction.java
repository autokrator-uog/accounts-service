package uk.ac.gla.sed.clients.accountsservice.core.events;


import com.eclipsesource.json.Json;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

public class AcceptedTransaction extends Event {
    private String transactionId;

    public AcceptedTransaction(PendingTransaction transaction) {
        this.transactionId = transaction.getTransactionId();

        this.type = "AcceptedTransaction";

        this.data = Json.object().asObject();
        this.data.set("TransactionID", this.transactionId);
    }

    public String getTransactionId() {
        return transactionId;
    }
}
