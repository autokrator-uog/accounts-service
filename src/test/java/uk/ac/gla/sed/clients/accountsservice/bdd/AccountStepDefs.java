package uk.ac.gla.sed.clients.accountsservice.bdd;

import cucumber.api.java8.En;
import uk.ac.gla.sed.clients.accountsservice.jdbi.AccountDAO;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class AccountStepDefs implements En {
    private DbTestFixture db;

    public AccountStepDefs() {
        this.db = BddTestRunnerTest.db;

        Given("there is a user named (\\w+) with accountId (\\d+)", (String personName, Integer accountId) -> {
            final AccountDAO dao = db.getAccountDAO();
            dao.createAccount(accountId);
        });

        Given("accountId (\\d+) has a balance of £([\\d+.]+) in it", (Integer accountId, String balanceStr) -> {
            BigDecimal balance = new BigDecimal(balanceStr);

            final AccountDAO dao = db.getAccountDAO();
            dao.updateBalance(accountId, balance);
        });

        Then("accountId (\\d+) now has a balance of £([\\d+.]+) in it", (Integer accountId, String balanceStr) -> {
            BigDecimal expectedBalance = new BigDecimal(balanceStr).setScale(2, RoundingMode.HALF_UP);

            final AccountDAO dao = db.getAccountDAO();
            BigDecimal actualBalance = dao.getBalance(accountId).setScale(2, RoundingMode.HALF_UP);

            assertEquals(expectedBalance, actualBalance);
        });

        Then("there is now an account with accountId (\\d+)", (Integer accountId) -> {
            final AccountDAO dao = db.getAccountDAO();

            BigDecimal actualBalance = dao.getBalance(accountId);
            if (actualBalance == null) {
                fail("There is no account with accountId " + accountId);
            }
        });
    }
}
