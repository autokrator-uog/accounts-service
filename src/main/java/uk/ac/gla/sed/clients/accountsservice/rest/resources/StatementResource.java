package uk.ac.gla.sed.clients.accountsservice.rest.resources;

import com.codahale.metrics.annotation.Timed;
import uk.ac.gla.sed.clients.accountsservice.jdbi.StatementDAO;
import uk.ac.gla.sed.clients.accountsservice.rest.api.StatementItem;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/account/{accountID}/statement")
@Produces(MediaType.APPLICATION_JSON)
public class StatementResource {
    private final StatementDAO dao;

    public StatementResource(StatementDAO dao) {
        this.dao = dao;
    }

    @GET
    @Timed
    public List<StatementItem> getAccountStatement(@PathParam("accountID") int accountId) {
        return dao.getAccountStatement(accountId);
    }
}
