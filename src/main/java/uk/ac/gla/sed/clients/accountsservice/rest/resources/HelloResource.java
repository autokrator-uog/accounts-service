package uk.ac.gla.sed.clients.accountsservice.rest.resources;

import com.codahale.metrics.annotation.Timed;
import uk.ac.gla.sed.clients.accountsservice.rest.api.Hello;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class HelloResource {
    @GET
    @Timed
    public Hello getHome() {
        return new Hello();
    }
}
