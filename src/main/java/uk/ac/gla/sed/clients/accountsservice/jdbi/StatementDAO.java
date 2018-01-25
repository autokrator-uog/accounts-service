package uk.ac.gla.sed.clients.accountsservice.jdbi;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import uk.ac.gla.sed.clients.accountsservice.rest.api.StatementItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface StatementDAO {
    @SqlUpdate("CREATE TABLE accounts_statements (" +
            "account_id int," +
            "statement_item int," +
            "amount NUMERIC(15,2)," +
            "note varchar(255)," +
            "PRIMARY KEY (account_id, statement_item)," +
            "FOREIGN KEY (account_id) REFERENCES accounts(id)" +
            ");")
    void createAccountStatementsTable();

    @SqlUpdate("DROP TABLE IF EXISTS accounts_statements;")
    void deleteTableIfExists();

    @SqlQuery(
            "SELECT statement_item " +
                    "FROM accounts_statements " +
                    "WHERE account_id = ? " +
                    "ORDER BY statement_item DESC " +
                    "LIMIT 1;")
    Optional<Integer> getHighestItemNumberForAccountId(int accountId);


    @SqlUpdate("INSERT INTO accounts_statements " +
            "(account_id, statement_item, amount, note) " +
            "VALUES (:id, :item_no, :amount, :note);")
    void putStatement(@Bind("id") int accountId, @Bind("item_no") int itemNo, @Bind("amount") BigDecimal amount, @Bind("note") String note);


    @SqlQuery(
            "SELECT statement_item, amount, note " +
                    "FROM accounts_statements " +
                    "WHERE account_id=:account_id " +
                    "ORDER BY statement_item DESC;")
    @RegisterBeanMapper(StatementItem.class)
    List<StatementItem> getAccountStatement(@Bind("account_id") int accountId);
}
