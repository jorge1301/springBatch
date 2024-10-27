package com.batch.heroe.ports.outbound;

import com.batch.heroe.core.domain.Hero;

import java.util.List;

public interface HeroRepositoryPort {
    void deleteAll();

    void saveAll(List<Hero> heroes);

    Hero save(Hero hero);
}
