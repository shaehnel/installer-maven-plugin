package com.trashgroup.maven.plugins;


import com.trashgroup.maven.plugins.util.ArtifactChoice;
import com.trashgroup.maven.plugins.util.ArtifactResolver;
import com.trashgroup.maven.plugins.util.Unzipper;
import com.trashgroup.maven.plugins.util.VersionSelection;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;


@Mojo(name = "install")
public class ZipDownloadMojo extends AbstractMojo {

    @Component
    private RepositorySystem repoSystem;

    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession repoSession;

    @Parameter(defaultValue = "${project.remotePluginRepositories}", readonly = true)
    private List<RemoteRepository> remoteRepos;

    @Component
    private Prompter prompter;

    public void execute() throws MojoExecutionException, MojoFailureException {

        final ArtifactChoice artifactChoice = new ArtifactChoice(prompter);
        artifactChoice.addArtifact("Installer Test", "com.trashgroup.maven.plugins", "installer-test-zip", "pom");
        artifactChoice.addArtifact("Also the Installer Test", "com.trashgroup.maven.plugins", "installer-test-zip", "pom");
        artifactChoice.addArtifact("Spring Platform BOM", "io.spring.platform", "platform-bom", "pom");

        final VersionSelection versionSelection = new VersionSelection(prompter);

        final ArtifactResolver resolver = new ArtifactResolver(repoSystem, repoSession, remoteRepos);

        try {
            // ask for artifact
            final ArtifactChoice.Item sel = artifactChoice.promptForArtifactSelection();

            // ask for artifact version with latest version as default
            final String latestVersion = resolver.findLatestVersion(sel.toString());
            final String version = versionSelection.promptForVersion(latestVersion);

            // resolve ZIP artifact
            final File repoFile = switchToZipFile(resolver.download(sel.toString() + ":" + version));
            getLog().info("Retrieved " + repoFile.getAbsolutePath());

            // unzip to temp
            final Path tempDir = Files.createTempDirectory("mvninstaller-");
            getLog().info("Created " + tempDir.toString());
            new Unzipper().unzip(repoFile, tempDir.toString());

        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public File switchToZipFile(File file) throws IOException {
        String fullname = file.getCanonicalPath();
        return new File(fullname.substring(0, fullname.lastIndexOf(".")) + ".zip");
    }

}
