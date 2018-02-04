package uk.ac.gla.sed.clients.accountsservice.core.handlers;

import uk.ac.gla.sed.clients.accountsservice.jdbi.AccountDAO;
import uk.ac.gla.sed.shared.eventbusclient.api.Consistency;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

import java.util.logging.Logger;

public class ConsistencyHandler {
    private static final Logger LOG = Logger.getLogger(ConsistencyHandler.class.getName());

    private final AccountDAO dao;

    public ConsistencyHandler(AccountDAO dao) {
        this.dao = dao;
    }

    public Consistency getConsistency(Event event) {
        String accountID = event.getData().get("AccountID").toString();

        String consistencyKey = "acc-" + accountID;
        Integer consistencyValue = dao.getConstistencyValue(consistencyKey);

        if (consistencyValue == null) {
            consistencyValue = 0;
            dao.createConsistencyEntry(consistencyKey);
        }
        dao.incrementConsistencyValue(consistencyKey);


        return new Consistency(consistencyKey, String.valueOf(consistencyValue));
    }
}
