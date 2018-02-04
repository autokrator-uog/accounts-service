package uk.ac.gla.sed.clients.accountsservice.core;

import io.dropwizard.lifecycle.Managed;
import uk.ac.gla.sed.clients.accountsservice.core.handlers.ConsistencyHandler;
import uk.ac.gla.sed.clients.accountsservice.jdbi.AccountDAO;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;
import uk.ac.gla.sed.shared.eventbusclient.api.EventBusClient;
import uk.ac.gla.sed.shared.eventbusclient.api.ReturningEvent;

import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class ReceiptProcessor implements Managed {
    private static final Logger LOG = Logger.getLogger(ReceiptProcessor.class.getName());

    private final EventBusClient eventBusClient;
    private final ExecutorService workers;
    private final AccountDAO dao;
    private final ConsistencyHandler consistencyHandler;

    public ReceiptProcessor(EventBusClient eventBusClient, AccountDAO dao, ExecutorService es) {
        this.eventBusClient = eventBusClient;
        this.dao = dao;
        this.workers = es;
        this.consistencyHandler = new ConsistencyHandler(dao);
    }

    @Override
    public void start() throws Exception {
        workers.submit(new ReceiptProcessor.ConsumeReturningEvent());
    }

    @Override
    public void stop() throws Exception {
        this.eventBusClient.stop();
    }

    class ConsumeReturningEvent implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    ReturningEvent returnedEvent = eventBusClient.getReturningQueue().take();
                    switch (returnedEvent.getStatus()) {
                        case SUCCESS:
                            LOG.info("We made it to success!");// We may need to do things on a successful returned event.
                            break;
                        case INCONSISTENT:
                            Event inconsistentEvent = returnedEvent.getEvent();
                            inconsistentEvent.setConsistency(consistencyHandler.getConsistency(inconsistentEvent));
                            eventBusClient.sendEvent(inconsistentEvent, null);
                            LOG.info("We made it to inconsistent");
                            break;
                        default:
                            LOG.info("Returning event had an invalid status. How did this happen?");
                            break;
                    }
                } catch (InterruptedException interrupt) {
                    LOG.info("ConsumeReturningEvent interrupted...");
                    return;
                }
            }
        }

    }
}
