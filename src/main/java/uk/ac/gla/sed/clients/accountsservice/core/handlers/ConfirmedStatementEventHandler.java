package uk.ac.gla.sed.clients.accountsservice.core.handlers;

import uk.ac.gla.sed.clients.accountsservice.core.events.ConfirmedCredit;
import uk.ac.gla.sed.clients.accountsservice.core.events.ConfirmedDebit;
import uk.ac.gla.sed.clients.accountsservice.jdbi.StatementDAO;

import java.math.BigDecimal;
import java.util.logging.Logger;

public class ConfirmedStatementEventHandler {
    private static final Logger LOG = Logger.getLogger(AccountCreationHandler.class.getName());

    private final StatementDAO dao;

    public ConfirmedStatementEventHandler(StatementDAO dao) {
        this.dao = dao;
    }

    public void processConfirmedCredit(ConfirmedCredit confirmedCredit) {
        int accountNo = confirmedCredit.getAccountId();
        BigDecimal amount = confirmedCredit.getAmount();

        int highestItemNo = dao.getHighestItemNumberForAccountId(accountNo).orElse(1);

        try {
            dao.putStatement(accountNo, highestItemNo, amount, "");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public void processConfirmedDebit(ConfirmedDebit confirmedDebit) {
        int accountNo = confirmedDebit.getAccountId();
        BigDecimal amount = confirmedDebit.getAmount().negate();

        int highestItemNo = dao.getHighestItemNumberForAccountId(accountNo).orElse(1);
        dao.putStatement(accountNo, highestItemNo, amount, "");
    }
}
