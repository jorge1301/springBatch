package com.batch.heroe.batch.step.processor;

import com.batch.heroe.adapters.api.dto.Heroes;
import com.batch.heroe.core.domain.Hero;
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
