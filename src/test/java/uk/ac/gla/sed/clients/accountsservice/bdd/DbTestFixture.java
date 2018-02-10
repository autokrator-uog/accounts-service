package uk.ac.gla.sed.clients.accountsservice.bdd;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.rules.ExternalResource;
import uk.ac.gla.sed.clients.accountsservice.jdbi.AccountDAO;
import uk.ac.gla.sed.clients.accountsservice.jdbi.StatementDAO;

class DbTestFixture extends ExternalResource {
    private static final String JDBC_URI = "jdbc:postgresql://postgres:5433/accountsservice";

    private Jdbi dbi;
    private Handle handle;

    private AccountDAO accountDAO;
    private StatementDAO statementDAO;

    DbTestFixture() {
        Environment environment = new Environment("test-env", Jackson.newObjectMapper(), null, new MetricRegistry(), null);


        dbi = Jdbi.create(getDataSourceFactory().build(environment.metrics(), null));
        dbi.installPlugin(new SqlObjectPlugin());

        handle = dbi.open();
        accountDAO = handle.attach(AccountDAO.class);
        statementDAO = handle.attach(StatementDAO.class);
    }

    @Override
    protected void before() {
        migrateDatabase();
    }

    private void migrateDatabase() {
        // create table structures
        statementDAO.deleteTableIfExists();
        accountDAO.deleteTableIfExists();
        accountDAO.deleteConsistencyTableIfExists();

        accountDAO.createAccountTable();
        statementDAO.createAccountStatementsTable();
        accountDAO.createConsistencyTable();
    }

    @Override
    protected void after() {
//        handle.close();
    }

    private DataSourceFactory getDataSourceFactory() {
        DataSourceFactory dataSourceFactory = new DataSourceFactory();

        // SQLITE TEST DB
//        dataSourceFactory.setDriverClass("org.sqlite.JDBC");
//        dataSourceFactory.setUrl("jdbc:sqlite:test.db");
//        dataSourceFactory.setUser("test");
//        dataSourceFactory.setPassword("test");

        // POSTGRES LIVE DB
        dataSourceFactory.setDriverClass("org.postgresql.Driver");
        dataSourceFactory.setUrl(JDBC_URI);
        dataSourceFactory.setUser("accountsservice");
        dataSourceFactory.setPassword("accountsservice");

        return dataSourceFactory;
    }

    public Jdbi getDbi() {
        return dbi;
    }

    public AccountDAO getAccountDAO() {
        return accountDAO;
    }

    public StatementDAO getStatementDAO() {
        return statementDAO;
    }
}
