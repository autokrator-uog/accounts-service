package uk.ac.gla.sed.clients.accountsservice.core.events;

import com.eclipsesource.json.Json;
import uk.ac.gla.sed.shared.eventbusclient.api.Consistency;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

public class AccountCreated extends Event {
    private final AccountCreationRequest request;
    private final int accountId;

    public AccountCreated(AccountCreationRequest request, int accountId) {
        super("AccountCreated", Json.object().asObject(), new Consistency("create-" + request.getRequestId(), "*"));

        this.request = request;
        this.data.set("RequestID", this.request.getRequestId());

        this.accountId = accountId;
        this.data.set("AccountID", this.accountId);
    }

    public AccountCreationRequest getRequest() {
        return request;
    }

    public int getAccountId() {
        return accountId;
    }
}
