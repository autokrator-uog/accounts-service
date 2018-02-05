package uk.ac.gla.sed.clients.accountsservice.jdbi;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.math.BigDecimal;

public interface AccountDAO {
    @SqlUpdate("CREATE TABLE accounts (id int PRIMARY KEY, balance NUMERIC(15,2) DEFAULT 0.00 NOT NULL);")
    void createAccountTable();

    @SqlUpdate("DROP TABLE IF EXISTS accounts;")
    void deleteTableIfExists();


    @SqlUpdate("INSERT INTO accounts (id) VALUES (:id);")
    void createAccount(@Bind("id") int accountId);

    @SqlQuery("SELECT id FROM accounts ORDER BY id DESC LIMIT 1;")
    Integer getHighestAccountId();

    @SqlQuery("SELECT balance FROM accounts WHERE id=:id;")
    BigDecimal getBalance(@Bind("id") int accountId);

    @SqlUpdate("UPDATE accounts SET balance=:balance WHERE id=:id;")
    void updateBalance(@Bind("id") int accountId, @Bind("balance") BigDecimal newBalance);

    @SqlUpdate("BEGIN TRANSACTION; UPDATE accounts SET balance=balance + :amount WHERE id=:to; UPDATE accounts SET balance=balance - :amount WHERE id=:from; COMMIT;")
    void syncPerformBalanceTransaction(@Bind("from") int fromAccountId, @Bind("to") int toAccountId, @Bind("amount") BigDecimal amount);
}
