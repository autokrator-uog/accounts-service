package uk.ac.gla.sed.clients.accountsservice.bdd;

import com.eclipsesource.json.Json;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java8.En;
import org.mockito.ArgumentCaptor;
import uk.ac.gla.sed.clients.accountsservice.core.EventProcessor;
import uk.ac.gla.sed.shared.eventbusclient.api.Consistency;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;
import uk.ac.gla.sed.shared.eventbusclient.api.EventBusClient;

import java.math.BigDecimal;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
public class EventBusStepDefs implements En {
    private ExecutorService es;
    private EventProcessor eventProcessor;

    private DbTestFixture dbTestFixture;
    private EventBusClient mockedEventBusClient;

    @SuppressWarnings("ConstantConditions")
    public EventBusStepDefs() {
        When("a[n]* PendingTransaction event is received for moving £([\\d+.]+) from account (\\d+) to account (\\d+) with ID (\\w+)",
                (String amountStr, Integer fromAccountId, Integer toAccountId, String transactionId) -> {
                    BigDecimal amount = new BigDecimal(amountStr);

            Event event = new Event("PendingTransaction", Json.object().asObject()
                    .set("TransactionID", transactionId)
                    .set("FromAccountID", fromAccountId)
                    .set("ToAccountID", toAccountId)
                    .set("Amount", amount.toString()), new Consistency("test", "*")
            );
            setupReceiveEvent(event);
            runEventProcessor();
        });

        When("a[n]* AccountCreationRequest event is received with RequestID (\\w+)", (String requestId) -> {
            Event event = new Event("AccountCreationRequest", Json.object().asObject()
                    .set("RequestID", requestId), new Consistency("test", "*")
            );
            setupReceiveEvent(event);
            runEventProcessor();
        });

        When("a[n]* (ConfirmedCredit|ConfirmedDebit) event is received for accountId (\\d+) with amount £([\\d+.]+)",
                (String type, Integer accountId, String amountStr) -> {
                    BigDecimal amount = new BigDecimal(amountStr);

                    Event event = new Event(type, Json.object().asObject()
                            .set("AccountID", accountId)
                            .set("Amount", amount.toString()),
                            new Consistency("test", "*")
                    );
                    setupReceiveEvent(event);
                    runEventProcessor();
                });

        Then("a[n]* (\\w+) event was created in response to PendingTransaction ID (\\w+)", (String eventType, String pendingTransactionId) -> {
            Event producedEvent = getProducedEventOrFail(eventType);

            assertEquals(pendingTransactionId, producedEvent.getData().getString("TransactionID", ""));
        });

        Then("a[n]* (\\w+) event was created for account (\\d+) with amount £([\\d.]+)", (String eventType, Integer accountId, String amountStr) -> {
            BigDecimal amount = new BigDecimal(amountStr);

            Event producedEvent = getProducedEventOrFail(eventType);

            assertEquals(amount, new BigDecimal(producedEvent.getData().getString("Amount", "")));
            assertEquals(accountId.intValue(), producedEvent.getData().getInt("AccountID", -1));
        });

        Then("a[n]* AccountCreated event was created in response to AccountCreationRequest RequestID (\\w+) that defines AccountID (\\d+)", (String requestId, Integer accountId) -> {
            Event producedEvent = getProducedEventOrFail("AccountCreated");

            assertEquals(requestId, producedEvent.getData().getString("RequestID", ""));
            assertEquals(accountId.intValue(), producedEvent.getData().getInt("AccountID", -1));
        });
    }

    @Before
    public void setUp() {
        es = Executors.newSingleThreadExecutor();

        dbTestFixture = BddTestRunnerTest.db;
        dbTestFixture.before();
        mockedEventBusClient = mock(EventBusClient.class);

        eventProcessor = new EventProcessor(mockedEventBusClient, dbTestFixture.getAccountDAO(), dbTestFixture.getStatementDAO(), es);
    }

    private void runEventProcessor() {
        try {
            eventProcessor.start();
            es.awaitTermination(10, TimeUnit.SECONDS);
            eventProcessor.stop();
        } catch (Exception e) {
            fail(e);
        }
    }

    private void setupReceiveEvent(Event e) {
        BlockingQueue<Event> bq = new LinkedBlockingQueue<>();
        bq.add(e);
        when(mockedEventBusClient.getIncomingEventsQueue()).thenReturn(bq);
    }

    private Event getProducedEventOrFail(String eventType) {
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(this.mockedEventBusClient, atLeastOnce()).sendEvent(eventCaptor.capture(), anyObject());

        for (Event val : eventCaptor.getAllValues()) {
            if (val.getType().equals(eventType)) {
                return val;
            }
        }

        fail("Event not produced...");
        return null;
    }

    @After
    public void after() {
        dbTestFixture.after();
    }
}
