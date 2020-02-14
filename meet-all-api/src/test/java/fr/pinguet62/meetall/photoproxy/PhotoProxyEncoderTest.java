package fr.pinguet62.meetall.photoproxy;

import org.junit.Test;

import java.util.List;

import static fr.pinguet62.meetall.photoproxy.PhotoProxyEncoder.decode;
import static fr.pinguet62.meetall.photoproxy.PhotoProxyEncoder.encode;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PhotoProxyEncoderTest {

    @Test
    public void areBijective() {
        for (String original : List.of("any", "http://google.fr", "http://google.fr/foo/bar?param=value")) {
            assertThat(decode(encode(original)), is(original));
        }
    }

}
