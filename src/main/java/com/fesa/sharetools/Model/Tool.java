package com.fesa.sharetools.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Tool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private boolean available;
}
