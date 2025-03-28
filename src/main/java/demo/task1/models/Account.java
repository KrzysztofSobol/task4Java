package demo.task1.models;

import jakarta.persistence.*;
import jdk.jfr.Name;
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

@NamedQueries({
        @NamedQuery(
                name = "Account.findByNameAndAddress",
                query = "SELECT a FROM Account a WHERE a.name = :name AND a.address = :address"
        ),
        @NamedQuery(
                name = "Account.findByNameStartWith",
                query = "SELECT a FROM Account a WHERE a.name LIKE :prefix"
        ),
        @NamedQuery(
                name = "Account.findByBalanceBetween",
                query = "SELECT a FROM Account a WHERE a.balance BETWEEN :min AND :max"
        ),
        @NamedQuery(
                name = "Account.findByTheRichest",
                query = "SELECT a FROM Account a WHERE a.balance = (SELECT MAX(a2.balance) FROM Account a2)"
        ),
        @NamedQuery(
                name = "Account.findByEmptyHistory",
                query = "SELECT a FROM Account a WHERE a.operations IS EMPTY"
        ),
        @NamedQuery(
                name = "Account.findByMostOperations",
                query = "SELECT a FROM Account a WHERE SIZE(a.operations) = (SELECT MAX(SIZE(a2.operations)) FROM Account a2)"
        )
})

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
