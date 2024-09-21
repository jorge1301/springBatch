package com.batch.job;

import com.batch.dto.Heroes;
import com.batch.model.Hero;
import org.springframework.batch.item.ItemProcessor;

public class HeroProcessor implements ItemProcessor<Heroes, Hero> {
    @Override
    public Hero process(Heroes heroes) {
        return Hero.builder()
                .id(heroes.id())
                .alias(heroes.alias())
                .name(heroes.name())
                .lastName(heroes.lastName())
                .age(heroes.age())
                .power(heroes.power())
                .status(heroes.status()).build();
    }
}
