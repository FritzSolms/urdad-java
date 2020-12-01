package org.urdad.binding;

import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;

public class ClassesToBeBoundPluginExtension {
    public ClassesToBeBoundPluginExtension(Project project) {
        outputDirectory = project.getBuildDir().getAbsolutePath()+"/generated-sources/java/";
    }

    boolean clearOutputDir = false;
    List<String> packagesToInclude = new ArrayList<>();
    String outputDirectory;
}
