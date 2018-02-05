package uk.ac.gla.sed.clients.accountsservice.core;

import io.dropwizard.lifecycle.Managed;
import uk.ac.gla.sed.clients.accountsservice.core.events.AccountCreationRequest;
import uk.ac.gla.sed.clients.accountsservice.core.events.ConfirmedCredit;
import uk.ac.gla.sed.clients.accountsservice.core.events.ConfirmedDebit;
import uk.ac.gla.sed.clients.accountsservice.core.events.PendingTransaction;
import uk.ac.gla.sed.clients.accountsservice.core.handlers.AccountCreationHandler;
import uk.ac.gla.sed.clients.accountsservice.core.handlers.ConfirmedStatementEventHandler;
import uk.ac.gla.sed.clients.accountsservice.core.handlers.PendingTransactionHandler;
import uk.ac.gla.sed.clients.accountsservice.jdbi.AccountDAO;
import uk.ac.gla.sed.clients.accountsservice.jdbi.StatementDAO;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;
import uk.ac.gla.sed.shared.eventbusclient.api.EventBusClient;

import java.util.concurrent.ExecutorService;

public class EventProcessor implements Managed {
    private final EventBusClient eventBusClient;
    private final PendingTransactionHandler pendingTransactionHandler;
    private final AccountCreationHandler accountCreationHandler;
    private final ConfirmedStatementEventHandler confirmedStatementEventHandler;
    private final ExecutorService workers;

    public EventProcessor(String eventBusURL, AccountDAO accountDAO, StatementDAO statementDAO, ExecutorService es) {
        this(new EventBusClient(eventBusURL), accountDAO, statementDAO, es);
    }

    public EventProcessor(EventBusClient eventBusClient, AccountDAO accountDAO, StatementDAO statementDAO, ExecutorService es) {
        this.eventBusClient = eventBusClient;
        this.pendingTransactionHandler = new PendingTransactionHandler(accountDAO, this.eventBusClient);
        this.accountCreationHandler = new AccountCreationHandler(accountDAO, this.eventBusClient);
        this.confirmedStatementEventHandler = new ConfirmedStatementEventHandler(statementDAO);
        this.workers = es;
    }

    @Override
    public void start() {
        this.eventBusClient.start();
        workers.submit(new ConsumeEventTask());
    }

    @Override
    public void stop() {
        this.eventBusClient.stop();
    }


    class ConsumeEventTask implements Runnable {
        @Override
        public void run() {
            while(true) {
                try {
                    Event incomingEvent = eventBusClient.getIncomingEventsQueue().take();
                    switch (incomingEvent.getType()) {
                        case "PendingTransaction":
                            PendingTransaction parsedEvent = new PendingTransaction(incomingEvent);
                            pendingTransactionHandler.processTransaction(parsedEvent);
                            break;
                        case "AccountCreationRequest":
                            AccountCreationRequest request = new AccountCreationRequest(incomingEvent);
                            accountCreationHandler.processAccountCreationRequest(request);
                            break;

                        case "ConfirmedCredit":
                            ConfirmedCredit confirmedCredit = new ConfirmedCredit(incomingEvent);
                            confirmedStatementEventHandler.processConfirmedCredit(confirmedCredit);
                            break;
                        case "ConfirmedDebit":
                            ConfirmedDebit confirmedDebit = new ConfirmedDebit(incomingEvent);
                            confirmedStatementEventHandler.processConfirmedDebit(confirmedDebit);
                            break;

                        default:
                            // ignore
                            break;
                    }

                } catch (InterruptedException interrupt) {
                    System.out.println("ConsumeEventTask interrupted...");
                    return;
                }
            }
        }
    }

    public EventBusClient getEventBusClient() {
        return eventBusClient;
    }
}
