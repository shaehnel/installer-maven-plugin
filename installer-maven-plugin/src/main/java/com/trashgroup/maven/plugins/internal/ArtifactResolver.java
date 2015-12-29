package com.trashgroup.maven.plugins.internal;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.version.Version;

import java.io.File;
import java.util.List;

public class ArtifactResolver {

    private RepositorySystem repoSystem;
    private RepositorySystemSession repoSession;
    private List<RemoteRepository> remoteRepos;

    public ArtifactResolver(RepositorySystem repoSystem, RepositorySystemSession repoSession, List<RemoteRepository> remoteRepos) {
        this.repoSystem = repoSystem;
        this.repoSession = repoSession;
        this.remoteRepos = remoteRepos;
    }

    public File download(String coordinates) throws ArtifactResolutionException, VersionRangeResolutionException {
        ArtifactRequest request = new ArtifactRequest();
        request.setArtifact(new DefaultArtifact(coordinates));
        request.setRepositories(remoteRepos);

        ArtifactResult result = repoSystem.resolveArtifact(repoSession, request);

        return result.getArtifact().getFile();
    }

    public String findLatestVersion(String coordinates) throws VersionRangeResolutionException {
        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact(new DefaultArtifact(coordinates + ":[0,)"));
        rangeRequest.setRepositories(remoteRepos);

        VersionRangeResult rangeResult = repoSystem.resolveVersionRange(repoSession, rangeRequest);

        Version newestVersion = rangeResult.getHighestVersion();

        if (newestVersion != null) {
            return newestVersion.toString();
        }
        return null;
    }
}
