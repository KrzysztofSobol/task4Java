package demo.task1.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter

@Entity
@Table(name = "ACCOUNTS")
public class Account extends AbstractModel{
    private String name;
    private String address;
    private BigDecimal balance = new BigDecimal(0);

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountOperation> operations = new ArrayList<>();

    public Account(String name, String address, BigDecimal balance) {
        this.name = name;
        this.address = address;
        this.balance = balance;
    }

    public Account(Account account) {
        this.name = account.getName();
        this.address = account.getAddress();
        this.balance = account.getBalance();
    }
}
