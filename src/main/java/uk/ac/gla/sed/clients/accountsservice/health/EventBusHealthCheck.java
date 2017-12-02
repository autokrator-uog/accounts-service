package uk.ac.gla.sed.clients.accountsservice.health;

import com.codahale.metrics.health.HealthCheck;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.wsWrapper;

import java.net.URI;

public class EventBusHealthCheck extends HealthCheck {
    private final String eventBusURL;

    public EventBusHealthCheck(String eventBusURL) {
        this.eventBusURL = eventBusURL;
    }

    @Override
    protected Result check() throws Exception {
        try {
            URI uri = URI.create(this.eventBusURL);
            final wsWrapper client = new wsWrapper(uri);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Result.unhealthy("Can't connect to Event bus. Exception: " + e.getMessage());
        }

        return Result.healthy();
    }
}
