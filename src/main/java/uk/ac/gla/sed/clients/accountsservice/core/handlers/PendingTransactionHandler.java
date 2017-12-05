package uk.ac.gla.sed.clients.accountsservice.core.handlers;

import uk.ac.gla.sed.clients.accountsservice.core.events.*;
import uk.ac.gla.sed.clients.accountsservice.jdbi.AccountDAO;
import uk.ac.gla.sed.shared.eventbusclient.api.EventBusClient;

import java.math.BigDecimal;
import java.util.logging.Logger;

public class PendingTransactionHandler {
    private static final Logger LOG = Logger.getLogger(PendingTransactionHandler.class.getName());

    private final AccountDAO dao;
    private final EventBusClient client;
    public PendingTransactionHandler(AccountDAO dao, EventBusClient client) {
        this.dao = dao;
        this.client = client;
    }

    private void rejectTransaction(PendingTransaction transaction, String reason) {
        RejectedTransaction event = new RejectedTransaction(transaction, reason);
        client.sendEvent(event);

        LOG.fine(String.format("Rejected transaction %s because: %s", transaction.getTransactionId(), reason));
    }

    private void acceptTransaction(PendingTransaction transaction) {
        AcceptedTransaction event = new AcceptedTransaction(transaction);
        client.sendEvent(event);

        LOG.fine(String.format("Accepted transaction %s", transaction.getTransactionId()));

        ConfirmedCredit confirmedCredit = new ConfirmedCredit(event);
        ConfirmedDebit confirmedDebit = new ConfirmedDebit(event);
        client.sendEvent(confirmedCredit);
        client.sendEvent(confirmedDebit);

        LOG.fine(String.format("Account statements written for transaction %s", transaction.getTransactionId()));
    }

    public void processTransaction(PendingTransaction transaction) {
        LOG.fine(String.format("Processing transaction %s", transaction.getTransactionId()));

        int fromAccountId = transaction.getFromAccountId();
        int toAccountId = transaction.getToAccountId();

        BigDecimal balanceFromAccount = dao.getBalance(fromAccountId);
        if (balanceFromAccount == null) {
            rejectTransaction(transaction, "FromAccountID is invalid!");
            return;
        }

        BigDecimal balanceToAccount = dao.getBalance(toAccountId);
        if (balanceToAccount == null) {
            rejectTransaction(transaction, "ToAccountID is invalid!");
            return;
        }

        BigDecimal amount = transaction.getAmount();
        if (balanceFromAccount.compareTo(amount) < 0) {
            rejectTransaction(transaction,"The from account does not have sufficient funds to complete the transaction.");
            return;
        }

        dao.syncPerformBalanceTransaction(fromAccountId, toAccountId, amount);
        acceptTransaction(transaction);
    }
}
