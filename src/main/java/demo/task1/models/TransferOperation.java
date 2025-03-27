package demo.task1.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

@Entity
@Table(name = "TRANSFER_OPERATIONS")
public class TransferOperation extends AccountOperation {
    private String title;

    @ManyToOne
    @JoinColumn(name = "other_account_id")
    private Account otherAccount;
}
