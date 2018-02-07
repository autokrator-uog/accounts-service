package uk.ac.gla.sed.clients.accountsservice.rest.resources;

import com.codahale.metrics.annotation.Timed;

import uk.ac.gla.sed.shared.eventbusclient.api.EventBusClient;

import uk.ac.gla.sed.clients.accountsservice.core.events.ConfirmedCredit;
import uk.ac.gla.sed.clients.accountsservice.core.events.ConfirmedDebit;
import uk.ac.gla.sed.clients.accountsservice.jdbi.AccountDAO;
import uk.ac.gla.sed.clients.accountsservice.rest.api.Account;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;

@Path("/account/{accountID}")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {
	private final AccountDAO dao;
	private final EventBusClient eventBus;

	public AccountResource(AccountDAO dao, EventBusClient eventBus) {
		this.dao = dao;
		this.eventBus = eventBus;
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

	@SuppressWarnings("unused")
	@POST
	@Timed
	public Account withdrawBalance(@PathParam("accountID") int accountId, @PathParam("withdraw") String withdraw) {
		BigDecimal balance = dao.getBalance(accountId);

		BigDecimal decimalWithdraw = new BigDecimal(withdraw);

		if (balance.compareTo(decimalWithdraw) < 0) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}

		else if (balance == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		BigDecimal outBalance = balance.subtract(decimalWithdraw, new MathContext(2));
		ConfirmedCredit eventCredit = new ConfirmedCredit(accountId, outBalance, new Date().toString());

		eventBus.sendEvent(eventCredit, null);

		return new Account(accountId, outBalance);

	}

	@POST
	@Timed
	public Account depositBalance(@PathParam("accountID") int accountId, @PathParam("deposit") String deposit) {
		BigDecimal balance = dao.getBalance(accountId);

		BigDecimal decimalDeposit = new BigDecimal(deposit);

		if (balance == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		BigDecimal outBalance = balance.add(decimalDeposit, new MathContext(2));

		ConfirmedDebit eventDebit = new ConfirmedDebit(accountId, outBalance, new Date().toString());

		eventBus.sendEvent(eventDebit, null);

		return new Account(accountId, outBalance);
	}

}
