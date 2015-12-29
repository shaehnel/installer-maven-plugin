package com.trashgroup.maven.plugins.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InstallItem {
    private String name;
    private String groupId;
    private String artifactId;
    private String packaging;

    private List<String> installOptionFiles;

    private String version;

    public InstallItem(String name, String groupId, String artifactId, String packaging) {
        this.name = name;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.packaging = packaging;
        this.installOptionFiles = new ArrayList<String>(3);
    }

    public void addInstallOptionFile(String pathInZip) {
        this.installOptionFiles.add(pathInZip);
    }

    public void setVersion(String version) {
        this.version = version;
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

    public String getVersion() {
        return version;
    }

    public List<String> getInstallOptionFiles() {
        return Collections.unmodifiableList(installOptionFiles);
    }

    @Override
    public String toString() {
        return groupId + ':' + artifactId + ':' + packaging + (version != null ? ":" + version : "");
    }
}
