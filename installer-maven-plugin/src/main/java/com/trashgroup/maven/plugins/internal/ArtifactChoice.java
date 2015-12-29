package com.trashgroup.maven.plugins.internal;

import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import java.util.ArrayList;
import java.util.List;

public class ArtifactChoice {

    private Prompter prompter;
    private List<InstallItem> artifacts;

    public ArtifactChoice(Prompter prompter) {
        this.prompter = prompter;
        artifacts = new ArrayList<InstallItem>(3);
    }

    public void addInstallItem(InstallItem installItem) {
        artifacts.add(installItem);
    }

    public InstallItem promptForArtifactSelection() throws PrompterException {
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

}
