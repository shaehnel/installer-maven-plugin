package com.trashgroup.maven.plugins.internal;


import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Arrays;

public class MavenInvoker {

    private String baseDir;

    public MavenInvoker(String baseDir) {
        this.baseDir = baseDir;
    }

    public void generateSources() throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setBaseDirectory(new File(baseDir));
        request.setPomFile(new File(baseDir + "/pom.xml"));
        request.setShowErrors(true);
        request.setOutputHandler(new SystemOutHandler());
        request.setGoals(Arrays.asList("clean", "generate-sources"));

        Invoker invoker = new DefaultInvoker();
        invoker.execute(request);
    }
}
