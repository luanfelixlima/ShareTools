package com.fesa.sharetools.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
public class Loan {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "tool_id", nullable = false)
    private Tool tool;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "borrower_id", nullable = false)
    private User borrower;

    @Getter
    @Setter
    @Column(nullable = false)
    private LocalDate loanDate;

    @Getter
    @Setter
    @Column(nullable = false)
    private LocalDate returnDate;

}

