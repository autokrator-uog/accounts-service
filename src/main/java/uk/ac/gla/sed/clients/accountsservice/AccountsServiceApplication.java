package uk.ac.gla.sed.clients.accountsservice;

import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;
import uk.ac.gla.sed.clients.accountsservice.core.EventProcessor;
import uk.ac.gla.sed.clients.accountsservice.health.EventBusHealthCheck;
import uk.ac.gla.sed.clients.accountsservice.jdbi.AccountDAO;
import uk.ac.gla.sed.clients.accountsservice.rest.resources.AccountResource;
import uk.ac.gla.sed.clients.accountsservice.rest.resources.HelloResource;

import java.math.BigDecimal;

public class AccountsServiceApplication extends Application<AccountsServiceConfiguration> {
    @Override
    public String getName() {
        return "accounts-service";
    }

    @Override
    public void initialize(Bootstrap<AccountsServiceConfiguration> bootstrap) {
        super.initialize(bootstrap);

        bootstrap.addBundle(new DBIExceptionsBundle());
        // redelivery would take place here...
    }

    @Override
    public void run(AccountsServiceConfiguration config, Environment environment) throws Exception {
        String eventBusURL = config.getEventBusURL();

        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, config.getDataSourceFactory(), "postgresql");

        final AccountDAO dao = jdbi.onDemand(AccountDAO.class);

        // create dummy data
        dao.deleteTableIfExists();
        dao.createAccountTable();
        dao.createAccount(1);
        dao.createAccount(2);
        dao.createAccount(3);
        dao.updateBalance(1, new BigDecimal("5000.24"));
        dao.updateBalance(2, new BigDecimal("30.245"));
        dao.updateBalance(3, new BigDecimal("1000000000.25"));

        System.out.println(dao.getBalance(1));
        System.out.println(dao.getBalance(2));
        System.out.println(dao.getBalance(3));

        /* MANAGED LIFECYCLES */
        final EventProcessor eventProcessor = new EventProcessor(
                eventBusURL,
                dao,
                environment.lifecycle().executorService("eventproessor").build()
        );
        environment.lifecycle().manage(eventProcessor);

        /* HEALTH CHECKS */
        final EventBusHealthCheck eventBusHealthCheck = new EventBusHealthCheck(eventBusURL);
        environment.healthChecks().register("event-bus", eventBusHealthCheck);
        // postgres is automatically checked

        /* RESOURCES */
        environment.jersey().register(new HelloResource());
        environment.jersey().register(new AccountResource(dao));
    }

    public static void main(String[] args) throws Exception {
        new AccountsServiceApplication().run(args);
    }
}
