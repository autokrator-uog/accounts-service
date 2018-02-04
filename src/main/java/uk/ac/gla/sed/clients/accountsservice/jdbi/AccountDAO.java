package uk.ac.gla.sed.clients.accountsservice.jdbi;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.math.BigDecimal;


public interface AccountDAO {
    @SqlUpdate("CREATE TABLE accounts (id int PRIMARY KEY, balance NUMERIC(15,2) DEFAULT 0.00 NOT NULL);")
    void createAccountTable();

    @SqlUpdate("DROP TABLE IF EXISTS accounts;")
    void deleteTableIfExists();

    @SqlUpdate("CREATE TABLE consistency (key TEXT PRIMARY KEY, value INT DEFAULT 0);")
    void createConsistencyTable();

    @SqlUpdate("DROP TABLE IF EXISTS consistency;")
    void deleteConsistencyTableIfExists();

    @SqlUpdate("INSERT INTO consistency (key) VALUES (:key);")
    void createConsistencyEntry(@Bind("key") String key);

    @SqlQuery("SELECT value FROM consistency WHERE key=:key;")
    Integer getConstistencyValue(@Bind("key") String key);

    @SqlUpdate("UPDATE consistency SET value=:value WHERE key=:key;")
    void setConsistencyValue(@Bind("key") String key, @Bind("value") int value);

    @SqlUpdate("UPDATE consistency SET value = value + 1 WHERE key=:key;")
    void incrementConsistencyValue(@Bind("key") String key);

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
