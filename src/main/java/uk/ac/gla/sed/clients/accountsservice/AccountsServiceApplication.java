package uk.ac.gla.sed.clients.accountsservice;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import uk.ac.gla.sed.clients.accountsservice.core.EventProcessor;
import uk.ac.gla.sed.clients.accountsservice.core.ReceiptProcessor;
import uk.ac.gla.sed.clients.accountsservice.health.EventBusHealthCheck;
import uk.ac.gla.sed.clients.accountsservice.jdbi.AccountDAO;
import uk.ac.gla.sed.clients.accountsservice.jdbi.StatementDAO;
import uk.ac.gla.sed.clients.accountsservice.rest.resources.AccountResource;
import uk.ac.gla.sed.clients.accountsservice.rest.resources.HelloResource;
<<<<<<< HEAD
import uk.ac.gla.sed.clients.accountsservice.rest.resources.StatementResource;
=======
>>>>>>> 3a791250cf8366448a1a4b55c00b75645d5d8b2f
import uk.ac.gla.sed.shared.eventbusclient.api.EventBusClient;

import java.math.BigDecimal;

@SuppressWarnings("WeakerAccess")
public class AccountsServiceApplication extends Application<AccountsServiceConfiguration> {
    @Override
    public String getName() {
        return "accounts-service";
    }

    @Override
    public void run(AccountsServiceConfiguration config, Environment environment) {
        String eventBusURL = config.getEventBusURL();

        final Jdbi jdbi = Jdbi.create(config.getDataSourceFactory().build(environment.metrics(), "postgresql"));
        jdbi.installPlugin(new SqlObjectPlugin());

        final Handle handle = jdbi.open();
        final AccountDAO accountDAO = handle.attach(AccountDAO.class);
        final StatementDAO statementDAO = handle.attach(StatementDAO.class);

        // create dummy data
<<<<<<< HEAD
        statementDAO.deleteTableIfExists();
        accountDAO.deleteTableIfExists();
        accountDAO.createAccountTable();
        accountDAO.createAccount(1);
        accountDAO.createAccount(2);
        accountDAO.createAccount(3);
        accountDAO.updateBalance(1, new BigDecimal("5000.24"));
        accountDAO.updateBalance(2, new BigDecimal("30.245"));
        accountDAO.updateBalance(3, new BigDecimal("1000000000.25"));
=======
        dao.deleteTableIfExists();
        dao.createAccountTable();
        dao.deleteConsistencyTableIfExists();
        dao.createConsistencyTable();
        dao.createAccount(1);
        dao.createAccount(2);
        dao.createAccount(3);
        dao.updateBalance(1, new BigDecimal("5000.24"));
        dao.updateBalance(2, new BigDecimal("30.245"));
        dao.updateBalance(3, new BigDecimal("1000000000.25"));
>>>>>>> 3a791250cf8366448a1a4b55c00b75645d5d8b2f

        statementDAO.createAccountStatementsTable();

        /* MANAGED LIFECYCLES */
        final EventProcessor eventProcessor = new EventProcessor(
                eventBusURL,
                accountDAO,
                statementDAO,
                environment.lifecycle().executorService("eventproessor").build()
        );
        EventBusClient eventBusClient = eventProcessor.getEventBusClient();
        final ReceiptProcessor receiptProcessor = new ReceiptProcessor(
                eventBusClient,
                dao,
                environment.lifecycle().executorService("receiptprocessor").build()
        );
        environment.lifecycle().manage(eventProcessor);
        environment.lifecycle().manage(receiptProcessor);


        /* HEALTH CHECKS */
        final EventBusHealthCheck eventBusHealthCheck = new EventBusHealthCheck(eventBusURL);
        environment.healthChecks().register("event-bus", eventBusHealthCheck);
        // postgres is automatically checked

        /* RESOURCES */
        environment.jersey().register(new HelloResource());
        environment.jersey().register(new AccountResource(accountDAO));
        environment.jersey().register(new StatementResource(statementDAO));
    }

    public static void main(String[] args) throws Exception {
        new AccountsServiceApplication().run(args);
    }
}
