package fr.pinguet62.meetall.config;

import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @see Enum#valueOf(Class, String)
 */
public class EnumDecoder implements Decoder<Enum> {

    private static final StringDecoder STRING_DECODER = StringDecoder.allMimeTypes();

    @Override
    public boolean canDecode(ResolvableType elementType, MimeType mimeType) {
        return elementType.getRawClass() != null && Enum.class.isAssignableFrom(elementType.getRawClass());
    }

    @Override
    public Flux<Enum> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, MimeType mimeType, Map<String, Object> hints) {
        return STRING_DECODER.decode(inputStream, elementType, mimeType, hints)
                .map(mapToEnum(elementType));
    }

    @Override
    public Mono<Enum> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType, MimeType mimeType, Map<String, Object> hints) {
        return STRING_DECODER.decodeToMono(inputStream, elementType, mimeType, hints)
                .map(mapToEnum(elementType));
    }

    @Override
    public List<MimeType> getDecodableMimeTypes() {
        return STRING_DECODER.getDecodableMimeTypes();
    }

    private Function<String, Enum> mapToEnum(ResolvableType elementType) {
        return str -> Enum.valueOf((Class<? extends Enum>) elementType.getRawClass(), str);
    }

}
