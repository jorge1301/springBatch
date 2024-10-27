package com.batch.heroe.adapters.persistence;

import com.batch.heroe.core.domain.Hero;
import com.batch.heroe.ports.outbound.HeroRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaHeroRepository implements HeroRepositoryPort {
    private final SpringDataHeroRepository repository;

    public JpaHeroRepository(SpringDataHeroRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public void saveAll(List<Hero> heroes) {
        repository.saveAll(heroes);
    }

    @Override
    public Hero save(Hero hero) {
        return repository.save(hero);
    }
}
