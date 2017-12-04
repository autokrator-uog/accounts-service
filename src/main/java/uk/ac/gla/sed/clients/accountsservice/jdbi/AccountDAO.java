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


    @SqlUpdate("INSERT INTO accounts (id) VALUES (:id);")
    void createAccount(@Bind("id") int accountId);

    @SqlQuery("SELECT balance FROM accounts WHERE id=:id;")
    BigDecimal getBalance(@Bind("id") int accountId);

    @SqlUpdate("UPDATE accounts SET balance=:balance WHERE id=:id;")
    void updateBalance(@Bind("id") int accountId, @Bind("balance") BigDecimal newBalance);

    @SqlUpdate("BEGIN TRANSACTION; UPDATE accounts SET balance=balance + :amount WHERE id=:to; UPDATE accounts SET balance=balance - :amount WHERE id=:from; COMMIT;")
    void syncPerformBalanceTransaction(@Bind("from") int fromAccountId, @Bind("to") int toAccountId, @Bind("amount") BigDecimal amount);
}
