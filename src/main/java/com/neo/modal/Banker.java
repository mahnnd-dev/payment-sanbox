package com.neo.modal;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Pm_Banker")
public class Banker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // mã thẻ
    private String cardNumber;
    // tên trên thẻ
    private String cardHolder;
    // ngày của thẻ
    private String cardDate;
}

