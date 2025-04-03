package demo.task1.models;

import jakarta.persistence.*;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

@Entity
@Table(name = "ACCOUNT_OPERATIONS")
@Inheritance(strategy = InheritanceType.JOINED)

@NamedQuery(
        name = "Operation.findByDateRange",
        query = "SELECT ao FROM AccountOperation ao WHERE ao.account.id = :accountId AND ao.createdAt BETWEEN :startDate AND :endDate"
)
@NamedQuery(
        name = "Operation.findByMostFrequentType",
        query = "SELECT ao.type FROM AccountOperation ao WHERE ao.account.id = :accountId GROUP BY ao.type ORDER BY COUNT(ao) DESC"
)

public class AccountOperation extends AbstractModel {

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private OperationType type;
}