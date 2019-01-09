package fr.pinguet62.meetall.photoproxy;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 2 functions must be bijective.
 */
public class PhotoProxyEncoder {

    private static final String ENCODING = UTF_8.name();

    public static String encode(String origin) {
        try {
            return URLEncoder.encode(origin, ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decode(String encoded) {
        try {
            return URLDecoder.decode(encoded, ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
