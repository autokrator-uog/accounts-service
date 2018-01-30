package uk.ac.gla.sed.clients.accountsservice.core.events;

import com.eclipsesource.json.Json;
import uk.ac.gla.sed.shared.eventbusclient.api.Consistency;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

public class AccountCreationRequest extends Event {
    private String requestId;

    public AccountCreationRequest(Event e) {
        super(e.getType(), Json.object().asObject().merge(e.getData()),
                new Consistency("ACR-" + e.getData().getString("RequestID", "") , "*"));

        if (!type.equals("AccountCreationRequest")) {
            throw new IllegalArgumentException("Event must be a PendingTransaction...");
        }

        this.requestId = data.getString("RequestID", "");
    }

    public String getRequestId() {
        return requestId;
    }
}
