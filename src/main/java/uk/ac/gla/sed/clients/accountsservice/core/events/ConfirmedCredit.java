package uk.ac.gla.sed.clients.accountsservice.core.events;

import com.eclipsesource.json.Json;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

import java.math.BigDecimal;
import java.util.Date;

public class ConfirmedCredit extends Event {
    private final AcceptedTransaction acceptedTransaction;

    public ConfirmedCredit(AcceptedTransaction transaction) {
        super("ConfirmedCredit", Json.object().asObject(), null);

        this.acceptedTransaction = transaction;

        this.data.set("AccountID", transaction.getTransaction().getToAccountId());
        this.data.set("Amount", transaction.getTransaction().getAmount().toString());
        this.data.set("Date", new Date().toString());
    }

    public AcceptedTransaction getAcceptedTransaction() {
        return acceptedTransaction;
    }
    
    public ConfirmedCredit(int accountID, BigDecimal amount, String date) {
    	super("ConfirmedCredit", Json.object().asObject(), null);
    	
    	this.acceptedTransaction = null;
    	
    	this.data.set("AccountID", accountID);
        this.data.set("Amount", amount.toString());
        this.data.set("Date", date);
    }
}
