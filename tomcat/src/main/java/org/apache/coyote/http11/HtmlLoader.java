package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HtmlLoader {

    private static final String DEFAULT_HTML = "Hello world!";

    public static String generateFile(String fileName) throws IOException {
        URL resource = HtmlLoader.class.getClassLoader().getResource(fileName);
        if (resource == null) {
            return DEFAULT_HTML;
        }
        final Path path = new File(resource.getFile()).toPath();
        final List<String> actual = Files.readAllLines(path);
        return String.join("\n", actual) + "\n";
    }
}
