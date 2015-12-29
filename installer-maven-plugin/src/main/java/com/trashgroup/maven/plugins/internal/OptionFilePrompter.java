package com.trashgroup.maven.plugins.internal;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OptionFilePrompter {

    private String fileRoot;
    private Prompter prompter;
    private Log logger;

    public OptionFilePrompter(String fileRoot, Prompter prompter, Log logger) {
        this.fileRoot = fileRoot;
        this.prompter = prompter;
        this.logger = logger;
        if (!this.fileRoot.endsWith(File.separator)) {
            this.fileRoot += File.separator;
        }
    }

    public Properties processFile(String name) {
        String fullName = fileRoot + name;
        File file = new File(fullName);
        if (!file.exists()) {
            logger.warn("File does not exist: " + fullName);
            return new Properties();
        }

        LineProcessor lineProcessor = new LineProcessor(prompter);

        try (InputStream in = Files.newInputStream(file.toPath());
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                lineProcessor.addLine(line);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return lineProcessor.getProperties();
    }

    private static class LineProcessor {

        private List<String> buffer;
        private Prompter prompter;

        private Properties properties;

        public LineProcessor(Prompter prompter) {
            this.prompter = prompter;
            this.buffer = new ArrayList<>(5);
            this.properties = new Properties();
        }

        public void addLine(String line) throws PrompterException {
            if (line.startsWith("#")) {
                if (line.length() > 1) {
                    buffer.add(line.substring(1).trim());
                }
            } else
            if (line.contains("=")) {
                String[] split= line.split("=");
                String key = split[0];
                String defaultValue = split[1];
                final String message = String.join("\n", buffer);
                final String value = prompter.prompt(message, defaultValue);
                properties.setProperty(key, value);
                buffer.clear();
            } else {
                buffer.clear();
            }
        }

        public Properties getProperties() {
            return properties;
        }

    }
}
