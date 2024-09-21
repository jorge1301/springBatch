package com.batch.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hero")
public class Hero {
    @Id
    private Long id;
    private String alias;
    private String name;
    private String lastName;
    private int age;
    private String power;
    private String status;
}


