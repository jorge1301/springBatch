package com.batch.heroe.adapters.api.dto;

public record Heroes(
        Long id,
        String alias,
        String name,
        String lastName,
        int age,
        String power,
        String status
        ) {
}
