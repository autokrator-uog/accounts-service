package uk.ac.gla.sed.clients.accountsservice.bdd;

import com.eclipsesource.json.Json;
import cucumber.api.java.Before;
import cucumber.api.java8.En;
import org.mockito.ArgumentCaptor;
import org.skife.jdbi.v2.DBI;
import uk.ac.gla.sed.clients.accountsservice.core.EventProcessor;
import uk.ac.gla.sed.clients.accountsservice.jdbi.AccountDAO;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;
import uk.ac.gla.sed.shared.eventbusclient.api.EventBusClient;

import java.math.BigDecimal;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class EventBusStepDefs implements En {
    private ExecutorService es;
    private EventProcessor eventProcessor;

    DbTestFixture dbTestFixture;
    DBI dbi;
    AccountDAO dao;
    EventBusClient mockedEventBusClient;

    @Before
    public void setUp() throws Throwable {
        es = Executors.newSingleThreadExecutor();

        dbTestFixture = new DbTestFixture();
        dbTestFixture.before();
        dbi = dbTestFixture.getDbi();
        dao = dbi.onDemand(AccountDAO.class);

        mockedEventBusClient = mock(EventBusClient.class);
        eventProcessor = new EventProcessor(mockedEventBusClient, dao, es);
    }

    private void runEventProcessor() {
        try {
            eventProcessor.start();
            es.awaitTermination(1000, TimeUnit.MILLISECONDS);
            eventProcessor.stop();
        }
        catch (Exception e) {
            fail(e);
        }
    }

    public EventBusStepDefs() {
        When("a PendingTransaction event is received for moving £([\\d+.]+) from account (\\d+) to account (\\d+) with ID (\\w+)",
                (String amountStr, Integer fromAccountId, Integer toAccountId, String transactionId) -> {
                    BigDecimal amount = new BigDecimal(amountStr);

            Event event = new Event("PendingTransaction", Json.object().asObject()
                    .set("TransactionID", transactionId)
                    .set("FromAccountID", fromAccountId)
                    .set("ToAccountID", toAccountId)
                    .set("Amount", amount.toString())
            );

            BlockingQueue<Event> bq = new LinkedBlockingQueue<>();
            bq.add(event);
            when(mockedEventBusClient.getIncomingEventsQueue()).thenReturn(bq);

            runEventProcessor();
        });

        Then("a (\\w+) event was created in response to PendingTransaction ID (\\w+)", (String eventType, String pendingTransactionId) -> {
            ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
            verify(this.mockedEventBusClient, atLeastOnce()).sendEvent(eventCaptor.capture());

            for (Event val : eventCaptor.getAllValues()) {
                if (val.getType().equals(eventType)) {
                    assertEquals(pendingTransactionId, val.getData().getString("TransactionID", ""));
                    return;
                }
            }

            fail("Event not produced...");
        });

        Then("a (\\w+) event was created for account (\\d+) with amount £([\\d.]+)", (String eventType, Integer accountId, String amountStr) -> {
            BigDecimal amount = new BigDecimal(amountStr);

            ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
            verify(this.mockedEventBusClient, atLeastOnce()).sendEvent(eventCaptor.capture());

            for (Event val : eventCaptor.getAllValues()) {
                if (val.getType().equals(eventType)) {
                    assertEquals(amount, new BigDecimal(val.getData().getString("Amount", "")));
                    assertEquals(accountId.intValue(), val.getData().getInt("AccountID", -1));
                    return;
                }
            }

            fail("Event not produced...");
        });
    }
}
