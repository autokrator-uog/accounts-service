package uk.ac.gla.sed.clients.accountsservice.rest.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Hello {

    @JsonProperty
    public String getMessage() {
        return "This is the Accounts Service.";
    }
}
