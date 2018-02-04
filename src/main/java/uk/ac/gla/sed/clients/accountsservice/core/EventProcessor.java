package uk.ac.gla.sed.clients.accountsservice.core;

import io.dropwizard.lifecycle.Managed;
import uk.ac.gla.sed.clients.accountsservice.core.events.AccountCreationRequest;
import uk.ac.gla.sed.clients.accountsservice.core.events.PendingTransaction;
import uk.ac.gla.sed.clients.accountsservice.core.handlers.AccountCreationHandler;
import uk.ac.gla.sed.clients.accountsservice.core.handlers.PendingTransactionHandler;
import uk.ac.gla.sed.clients.accountsservice.jdbi.AccountDAO;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;
import uk.ac.gla.sed.shared.eventbusclient.api.EventBusClient;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.RegisterMessage;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class EventProcessor implements Managed {
    private final EventBusClient eventBusClient;
    private final PendingTransactionHandler pendingTransactionHandler;
    private final AccountCreationHandler accountCreationHandler;
    private final ExecutorService workers;

    public EventProcessor(String eventBusURL, AccountDAO dao, ExecutorService es) {
        this(new EventBusClient(eventBusURL), dao, es);
    }

    public EventProcessor(EventBusClient eventBusClient, AccountDAO dao, ExecutorService es) {
        this.eventBusClient = eventBusClient;
        this.pendingTransactionHandler = new PendingTransactionHandler(dao, this.eventBusClient);
        this.accountCreationHandler = new AccountCreationHandler(dao, this.eventBusClient);
        this.workers = es;
    }

    @Override
    public void start() throws Exception {
        this.eventBusClient.start();
        ArrayList<String> interestedEvents = new ArrayList<>();
        interestedEvents.add("PendingTransaction");
        interestedEvents.add("AccountCreationRequest");
        RegisterMessage registration = new RegisterMessage("accounts", interestedEvents);
        eventBusClient.register(registration);
        workers.submit(new ConsumeEventTask());
    }

    @Override
    public void stop() throws Exception {
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
