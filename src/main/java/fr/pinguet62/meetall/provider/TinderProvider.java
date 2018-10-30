package fr.pinguet62.meetall.provider;

import org.springframework.stereotype.Component;

@Component
public class TinderProvider implements Provider {

    @Override
    public String getId() {
        return "tinder";
    }

}
