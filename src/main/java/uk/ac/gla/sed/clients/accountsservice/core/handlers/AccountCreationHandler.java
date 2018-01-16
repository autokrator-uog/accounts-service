package uk.ac.gla.sed.clients.accountsservice.core.handlers;

import uk.ac.gla.sed.clients.accountsservice.core.events.AccountCreated;
import uk.ac.gla.sed.clients.accountsservice.core.events.AccountCreationRequest;
import uk.ac.gla.sed.clients.accountsservice.jdbi.AccountDAO;
import uk.ac.gla.sed.shared.eventbusclient.api.EventBusClient;

import java.util.logging.Logger;

public class AccountCreationHandler {
    private static final Logger LOG = Logger.getLogger(AccountCreationHandler.class.getName());

    private final AccountDAO dao;
    private final EventBusClient client;
    public AccountCreationHandler(AccountDAO dao, EventBusClient client) {
        this.dao = dao;
        this.client = client;
    }

    public void processAccountCreationRequest(AccountCreationRequest request) {
        Integer highestAccountId = dao.getHighestAccountId();
        if (highestAccountId == null) highestAccountId = 0;

        int newAccountId = highestAccountId + 1;
        dao.createAccount(newAccountId);

        AccountCreated event = new AccountCreated(request, newAccountId);
        client.sendEvent(event, request);
        LOG.info("Creating account " + newAccountId);
    }
}
