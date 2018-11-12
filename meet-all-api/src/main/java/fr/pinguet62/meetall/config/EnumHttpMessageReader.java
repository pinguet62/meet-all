package fr.pinguet62.meetall.config;

import org.springframework.http.codec.DecoderHttpMessageReader;

/**
 * @see <a href="https://github.com/spring-projects/spring-boot/issues/8441">Multipart Form with Number @RequestPart causes HttpMediaTypeNotSupportedException</a>
 */
public class EnumHttpMessageReader extends DecoderHttpMessageReader<Enum> {

    public EnumHttpMessageReader() {
        super(new EnumDecoder());
    }

}
