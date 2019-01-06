package fr.pinguet62.meetall;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;

public class TestUtils {

    public static String readResource(String resourcePath) {
        Path path;
        try {
            path = Paths.get(TestUtils.class.getResource(resourcePath).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        try (Stream<String> lines = Files.lines(path)) {
            return lines.collect(joining(lineSeparator()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
