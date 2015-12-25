package com.trashgroup.maven.plugins.util;

import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import java.util.ArrayList;
import java.util.List;

public class ArtifactChoice {

    private Prompter prompter;
    private List<Item> artifacts;

    public ArtifactChoice(Prompter prompter) {
        this.prompter = prompter;
        artifacts = new ArrayList<Item>(3);
    }

    public void addArtifact(String name, String groupId, String artifactId, String packaging) {
        artifacts.add(new Item(name, groupId, artifactId, packaging));
    }

    public Item promptForArtifactSelection() throws PrompterException {
        StringBuilder message = new StringBuilder();
        message.append("Select artifact:");
        List<String> options = new ArrayList<String>(artifacts.size());
        for (int i = 0; i < artifacts.size(); i++) {
            message.append("\n  (").append(i+1).append(") ").append(artifacts.get(i).getName());
            options.add(String.valueOf(i+1));
        }
        message.append("\n");
        String selection = prompter.prompt(message.toString(), options);
        int i = Integer.parseInt(selection);
        return artifacts.get(i - 1);
    }

    public static class Item {
        private String name;
        private String groupId;
        private String artifactId;
        private String packaging;

        public Item(String name, String groupId, String artifactId, String packaging) {
            this.name = name;
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.packaging = packaging;
        }

        public String getName() {
            return name;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public String getPackaging() {
            return packaging;
        }

        @Override
        public String toString() {
            return groupId + ':' + artifactId + ':' + packaging;
        }
    }

}
