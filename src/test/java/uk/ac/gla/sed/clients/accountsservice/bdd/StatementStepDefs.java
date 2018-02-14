package uk.ac.gla.sed.clients.accountsservice.bdd;

import cucumber.api.java8.En;
import uk.ac.gla.sed.clients.accountsservice.jdbi.StatementDAO;
import uk.ac.gla.sed.clients.accountsservice.rest.api.StatementItem;
import uk.ac.gla.sed.clients.accountsservice.rest.resources.StatementResource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatementStepDefs implements En {
    private DbTestFixture db;

    private List<StatementItem> response;

    public StatementStepDefs() {
        db = BddTestRunnerTest.db;

        Given("there is a statement item for accountId (\\d+) with amount £([\\d+.-]+)", (Integer accountId, String amountStr) -> {
            BigDecimal amount = new BigDecimal(amountStr).setScale(2, RoundingMode.HALF_UP);

            final StatementDAO dao = db.getStatementDAO();
            dao.putStatement(accountId, 0, amount, "Test item");
        });

        When("the statement for accountId (\\d+) is requested", (Integer accountId) -> {
            StatementResource resource = new StatementResource(db.getStatementDAO());

            response = resource.getAccountStatement(accountId);
        });

        Then("the response is an empty list", () -> {
            assertTrue(response.isEmpty());
        });

        Then("the response contains a statement with amount £([\\d+.-]+)", (String amountStr) -> {
            BigDecimal amount = new BigDecimal(amountStr).setScale(2, RoundingMode.HALF_UP);

            StatementItem item = response.get(0);
            assertEquals(amount, item.getAmount());
        });

        Then("the statement for accountId (\\d+) contains an entry with amount £([\\d+.-]+)", (Integer accountId, String amountStr) -> {
            BigDecimal amount = new BigDecimal(amountStr).setScale(2, RoundingMode.HALF_UP);

            final StatementDAO dao = db.getStatementDAO();

            List<StatementItem> statement = dao.getAccountStatement(accountId);
            assertEquals(1, statement.size());

            assertEquals(amount, statement.get(0).getAmount());
        });
    }
}
