package uk.ac.gla.sed.clients.accountsservice.rest.resources;

import com.codahale.metrics.annotation.Timed;
import uk.ac.gla.sed.clients.accountsservice.jdbi.AccountDAO;
import uk.ac.gla.sed.clients.accountsservice.rest.api.Account;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

@Path("/account/{accountID}")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {
    private final AccountDAO dao;
    public AccountResource(AccountDAO dao) {
        this.dao = dao;
    }

    @GET
    @Timed
    public Account getAccountDetails(@PathParam("accountID") int accountId) {
        BigDecimal balance = dao.getBalance(accountId);
        if (balance == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return new Account(accountId, balance);
    }
}
