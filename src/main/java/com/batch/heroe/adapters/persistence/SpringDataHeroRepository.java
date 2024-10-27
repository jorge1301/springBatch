package com.batch.heroe.adapters.persistence;

import com.batch.heroe.core.domain.Hero;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataHeroRepository extends JpaRepository<Hero, Long> {
}
