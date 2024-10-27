package com.batch.heroe.batch.step.writter;

import com.batch.heroe.adapters.persistence.JpaHeroRepository;
import com.batch.heroe.core.domain.Hero;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class HeroWriter implements ItemWriter<Hero> {
    @Autowired
    private JpaHeroRepository heroRepository;

    @Override
    public void write(Chunk<? extends Hero> chunk) {
        List<Hero> heroes = chunk.getItems().stream().map(hero -> (Hero) hero).toList();
        this.heroRepository.saveAll(heroes);
    }
}
