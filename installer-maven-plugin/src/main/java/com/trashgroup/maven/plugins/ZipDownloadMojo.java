package com.trashgroup.maven.plugins;


import com.trashgroup.maven.plugins.internal.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;


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
        final InstallItem installerTest = new InstallItem("Installer Test", "com.trashgroup.maven.plugins", "installer-test-zip", "pom");
        installerTest.addInstallOptionFile("replace.properties");
        installerTest.addInstallOptionFile("options.properties");
        final InstallItem anotherInstallerTest = new InstallItem("Also the Installer Test", "com.trashgroup.maven.plugins", "installer-test-zip", "pom");
        final InstallItem springPlatformBom = new InstallItem("Spring Platform BOM", "io.spring.platform", "platform-bom", "pom");
        artifactChoice.addInstallItem(installerTest);
        artifactChoice.addInstallItem(anotherInstallerTest);
        artifactChoice.addInstallItem(springPlatformBom);

        final VersionSelection versionSelection = new VersionSelection(prompter);
        final ArtifactResolver resolver = new ArtifactResolver(repoSystem, repoSession, remoteRepos);

        try {
            // ask for artifact
            final InstallItem sel = artifactChoice.promptForArtifactSelection();

            // ask for artifact version with latest version as default
            final String latestVersion = resolver.findLatestVersion(sel.toString());
            final String version = versionSelection.promptForVersion(latestVersion);
            sel.setVersion(version);

            // resolve ZIP artifact
            final File zipFile = switchToZipFile(resolver.download(sel.toString()));
            getLog().info("Retrieved " + zipFile.getAbsolutePath());

            // unzip to temp
            final Path tempDir = Files.createTempDirectory("mvninstaller-");
            getLog().info("Created " + tempDir.toString());
            new Unzipper().unzip(zipFile, tempDir.toString());
            final String baseDir = tempDir + "/" +  zipFile;

            // go through property files
            OptionFilePrompter opf = new OptionFilePrompter(tempDir.toString(), prompter, getLog());
            for (String filename : sel.getInstallOptionFiles()) {
                Properties userInput = opf.processFile(filename);
                userInput.store(new FileWriter(tempDir.toString() + "/" + filename), "user input");
            }

        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public File switchToZipFile(File file) throws IOException {
        String fullname = file.getCanonicalPath();
        return new File(fullname.substring(0, fullname.lastIndexOf(".")) + ".zip");
    }

}
