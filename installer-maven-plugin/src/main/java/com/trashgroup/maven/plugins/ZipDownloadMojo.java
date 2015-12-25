package com.trashgroup.maven.plugins;


import com.trashgroup.maven.plugins.util.Unzipper;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @goal install
 */
public class ZipDownloadMojo extends AbstractMojo {

    /**
     * The entry point to Aether, i.e. the component doing all the work.
     *
     * @component
     */
    private RepositorySystem repoSystem;

    /**
     * The current repository/network configuration of Maven.
     *
     * @parameter default-value="${repositorySystemSession}"
     * @readonly
     */
    private RepositorySystemSession repoSession;

    /**
     * The project's remote repositories to use for the resolution of plugins and their dependencies.
     *
     * @parameter default-value="${project.remotePluginRepositories}"
     * @readonly
     */
    private List<RemoteRepository> remoteRepos;

    protected Unzipper unzipper = new Unzipper();

    public void execute() throws MojoExecutionException, MojoFailureException {

        String artifact = "com.trashgroup.maven.plugins:installer-test-zip:0.1.0-SNAPSHOT";

        try {
            Path tempDir = Files.createTempDirectory("mvninstaller-");
            getLog().info("Created " + tempDir.toString());
            File repoFile = download(artifact);
            unzipper.unzip(repoFile, tempDir.toString());

        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private File download(String artifact) throws ArtifactResolutionException {
        ArtifactRequest request = new ArtifactRequest();
        request.setArtifact(new DefaultArtifact(artifact));
        request.setRepositories(remoteRepos);

        getLog().info("Resolving artifact " + artifact + " from " + remoteRepos);

        ArtifactResult result;
        result = repoSystem.resolveArtifact(repoSession, request);

        getLog().info("Resolved artifact " + artifact + " to " +
                result.getArtifact().getFile() + " from "
                + result.getRepository());

        return result.getArtifact().getFile();
    }

}
