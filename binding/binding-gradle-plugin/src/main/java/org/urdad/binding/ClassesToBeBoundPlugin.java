package org.urdad.binding;

import org.gradle.api.Action;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.SourceSetContainer;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class ClassesToBeBoundPlugin implements org.gradle.api.Plugin<org.gradle.api.Project> {


    @Override
    public void apply(org.gradle.api.Project project) {

        ClassesToBeBoundPluginExtension classesToBeBoundPluginExtension = project.getExtensions().create("classesToBeBoundPlugin", ClassesToBeBoundPluginExtension.class, project);

        List<String> packagesToInclude = classesToBeBoundPluginExtension.packagesToInclude;

        File outputDirectory = new File(classesToBeBoundPluginExtension.outputDirectory);

        boolean clearOutputDir = classesToBeBoundPluginExtension.clearOutputDir;

        GenerateClassesToBeBound generateClassesToBeBound = project.getTasks().create("generateClassesToBeBound", GenerateClassesToBeBound.class, new Action<GenerateClassesToBeBound>() {
            @Override
            public void execute(GenerateClassesToBeBound generateClassesToBeBound) {
                generateClassesToBeBound.setClearOutputDir(clearOutputDir);
                generateClassesToBeBound.setOutputDirectory(outputDirectory.getAbsolutePath());
                generateClassesToBeBound.getPackagesToInclude().addAll(packagesToInclude);
            }
        });
        project.getPlugins().withType(JavaPlugin.class, new Action<JavaPlugin>() {
            @Override
            public void execute(final JavaPlugin plugin) {
                SourceSetContainer sourceSets = (SourceSetContainer)
                        project.getProperties().get("sourceSets");
                sourceSets.getByName("main").getResources().srcDir(outputDirectory);
                Copy resourcesTask = (Copy) project.getTasks()
                        .getByName(JavaPlugin.COMPILE_JAVA_TASK_NAME);
                resourcesTask.dependsOn(generateClassesToBeBound);
            }
        });


    }
}
