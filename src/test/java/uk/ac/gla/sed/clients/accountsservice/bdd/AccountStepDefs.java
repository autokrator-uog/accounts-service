package uk.ac.gla.sed.clients.accountsservice.bdd;

import cucumber.api.java8.En;
import cucumber.api.java.Before;
import org.skife.jdbi.v2.DBI;
import uk.ac.gla.sed.clients.accountsservice.jdbi.AccountDAO;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountStepDefs implements En {
    DbTestFixture db;
    DBI dbi;

    @Before
    public void setUpdDBFixture() throws Throwable {
        db = new DbTestFixture();
        db.before();
        dbi = db.getDbi();
    }

    public AccountStepDefs() {
        Given("there is a user named (\\w+) with accountId (\\d+)", (String personName, Integer accountId) -> {
            final AccountDAO dao = dbi.onDemand(AccountDAO.class);
            dao.createAccount(accountId);
        });

        Given("accountId (\\d+) has a balance of £([\\d+.]+) in it", (Integer accountId, String balanceStr) -> {
            BigDecimal balance = new BigDecimal(balanceStr);

            final AccountDAO dao = dbi.onDemand(AccountDAO.class);
            dao.updateBalance(accountId, balance);
        });

        Then("accountId (\\d+) now has a balance of £([\\d+.]+) in it", (Integer accountId, String balanceStr) -> {
            BigDecimal expectedBalance = new BigDecimal(balanceStr).setScale(2, RoundingMode.HALF_UP);

            final AccountDAO dao = dbi.onDemand(AccountDAO.class);
            BigDecimal actualBalance = dao.getBalance(accountId).setScale(2, RoundingMode.HALF_UP);

            assertEquals(expectedBalance, actualBalance);
        });
    }
}
