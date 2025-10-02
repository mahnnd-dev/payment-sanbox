package com.neo.modal;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Pm_Partner")
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tmnCode;
    private String secretKey;
    private String ipnUrl;
    private String environment;
}

