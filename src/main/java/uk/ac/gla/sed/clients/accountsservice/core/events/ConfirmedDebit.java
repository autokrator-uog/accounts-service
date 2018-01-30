package uk.ac.gla.sed.clients.accountsservice.core.events;

import com.eclipsesource.json.Json;
import uk.ac.gla.sed.shared.eventbusclient.api.Consistency;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

import java.util.Date;

public class ConfirmedDebit extends Event {
    private final AcceptedTransaction acceptedTransaction;

    public ConfirmedDebit(AcceptedTransaction transaction, Consistency consistency) {
        super("ConfirmedDebit", Json.object().asObject(), consistency);

        this.acceptedTransaction = transaction;

        this.data.set("AccountID", transaction.getTransaction().getFromAccountId());
        this.data.set("Amount", transaction.getTransaction().getAmount().toString());
        this.data.set("Date", new Date().toString());
    }

    public AcceptedTransaction getAcceptedTransaction() {
        return acceptedTransaction;
    }
}
