package fr.pinguet62.meetall.config;

import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.stereotype.Component;

@Component
public class EnumHttpMessageReaderRegistrar implements CodecCustomizer {

    @Override
    public void customize(CodecConfigurer configurer) {
        configurer.customCodecs().register(new EnumHttpMessageReader());
    }

}
