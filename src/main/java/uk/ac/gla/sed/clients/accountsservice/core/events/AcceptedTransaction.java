package uk.ac.gla.sed.clients.accountsservice.core.events;


import com.eclipsesource.json.Json;
import uk.ac.gla.sed.shared.eventbusclient.api.Consistency;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

public class AcceptedTransaction extends Event {
    private final PendingTransaction transaction;

    public AcceptedTransaction(PendingTransaction transaction) {
        super("AcceptedTransaction", Json.object().asObject(),
                new Consistency("TXA-" + transaction.getConsistency().getKey().substring(3), "*"));

        this.transaction = transaction;
        this.data.set("TransactionID", transaction.getTransactionId());
    }

    public PendingTransaction getTransaction() {
        return transaction;
    }
}
