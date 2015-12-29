# Installer Maven Plugin

A plugin that will be used as an interactive "installer".

## Usage

Run: 

    mvn clean install
    mvn com.trashgroup.maven.plugins:installer-maven-plugin:0.1.0-SNAPSHOT:install

## Task tracking 

- (DONE) Initial setup
- (DONE) Retrieve an artifact and unpack to `temp`
- (DONE) Display a list where the a user on the command line can select the artifact to download
- (DONE) Unpack the artifact and interactively go through a list of specified property files on command line
- (TODO) Run `mvn generate-sources` in `temp`