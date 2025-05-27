package com.fesa.sharetools.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tools")
public class Tool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private boolean available = true;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    // --- NEW: Transient field for active loan ---
    @Transient // This annotation marks the field as not persistent in the database
    @Getter
    @Setter
    private Loan currentLoan; // This will hold the active loan if one exists
    // --- END NEW ---
}