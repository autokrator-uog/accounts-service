package uk.ac.gla.sed.clients.accountsservice.rest.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Hello {
    private String message = "This is the Accounts Service.";

    @JsonProperty
    public String getMessage() {
        return message;
    }
}
