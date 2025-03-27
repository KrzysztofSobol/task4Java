package demo.task1;

import demo.task1.models.Account;
import java.math.BigDecimal;
import java.lang.reflect.Field;

public class TestDataUtil {
    public static Account createTestAccountA() {
        Account account = new Account();
        account.setId(1L);
        account.setName("x");
        account.setAddress("y");
        account.setBalance(BigDecimal.ZERO);

        return account;
    }

    public static Account createTestAccountB() {
        Account account = new Account();
        account.setId(2L);
        account.setName("x");
        account.setAddress("y");
        account.setBalance(BigDecimal.ONE);

        return account;
    }
}
