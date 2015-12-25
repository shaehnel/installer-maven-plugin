package com.trashgroup.maven.plugins.util;


import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

public class VersionSelection {

    private Prompter prompter;

    public VersionSelection(Prompter prompter) {
        this.prompter = prompter;
    }

    public String promptForVersion(String defaultVersion) throws PrompterException {
        return prompter.prompt("Select version: ", defaultVersion);
    }
}
