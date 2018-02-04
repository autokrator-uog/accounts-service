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

    @POST
    @Timed
    public Account withdrawBalance(@PathParam("accountID") int accountId, int withdraw) {
        BigDecimal balance = dao.getBalance(accountId);
        if (balance == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        else if (balance > withdraw){
            System.err.println("Withdrawl amount is greater than the balance.");
            //How should I be handling this situation?
            //Create an error exception as is done in the if loop above?
        }
        return new Account(accountId, balance - withdraw);
    }

    @POST
    @Timed
    public Account depositBalance(@PathParam("accountID") int accountId, int deposit) {
        BigDecimal balance = dao.getBalance(accountId);
        if (balance == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return new Account(accountId, balance + deposit);
    }

}
