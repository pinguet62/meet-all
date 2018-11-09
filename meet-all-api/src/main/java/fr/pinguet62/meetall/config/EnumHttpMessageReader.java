package fr.pinguet62.meetall.config;

import org.springframework.http.codec.DecoderHttpMessageReader;

public class EnumHttpMessageReader extends DecoderHttpMessageReader<Enum> {

    public EnumHttpMessageReader() {
        super(new EnumDecoder());
    }

}
