package pl.bykowski.rectangleapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DebtorDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private float debt;
    private float totalDebt;
    private LocalDate date;
    private String reasonForTheDebt;
    private String userName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debtorDetails_Test")
    private Debtor debtor;
}