package org.urdad.binding;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.reflections.ReflectionUtils;
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

@Mojo(name = "generateClassesToBeBound", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class ClassesToBeBoundMojo extends AbstractMojo {
    @Parameter(property = "packagePrefix")
    private List<String> packagesToInclude;

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/java/", property = "outputDir", required = true)
    private File outputDirectory;

    @Parameter(defaultValue = "false")
    protected boolean clearOutputDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        try {

            List<String> compileClasspathElements = project.getCompileClasspathElements();
            List<URL> fileUrls = new ArrayList<>();
            for (String compileClasspathElement : compileClasspathElements) {
                try {
//                    getLog().info("Found classpath element: "+compileClasspathElement);
                    fileUrls.add(new URL("file://" + compileClasspathElement));
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
            List<ClassLoader> classLoaders = new ArrayList<>();
            classLoaders.add(ClasspathHelper.staticClassLoader());
            classLoaders.add(ClasspathHelper.contextClassLoader());
            classLoaders.add(URLClassLoader.newInstance(fileUrls.toArray(new URL[0])));
            List<URL> jarUrls = new ArrayList<>();
            for (Artifact artifact : project.getArtifacts()) {
                try {
//                    getLog().info("Found classpath element: "+artifact.getFile().getAbsolutePath());
                    URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{artifact.getFile().toURI().toURL()});
//                        getLog().info("Class loader urls: "+Arrays.toString(urlClassLoader.getURLs()));
                    classLoaders.add(urlClassLoader);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }

            ClassLoader[] loaders = classLoaders.toArray(new ClassLoader[0]);
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forClassLoader(loaders))
                    .setScanners(new SubTypesScanner(true))
                    .filterInputsBy(new FilterBuilder().includePackage(packagesToInclude.toArray(new String[0])))
            );

            Iterable<String> classes = reflections.getStore().getAll(SubTypesScanner.class.getSimpleName(), Arrays.asList(Serializable.class.getName()));
            List<String> filteredList = new LinkedList<>();
            for (String aClass : classes) {
                for (String packagePrefix : packagesToInclude) {
                    if (aClass.startsWith(packagePrefix)) {
                        filteredList.add(aClass);
                        break;
                    }
                }
            }
            filteredList.sort(Comparator.naturalOrder());
            try {
                Files.createDirectories(outputDirectory.toPath());
                if (clearOutputDir) {
                    emptyDirectory(outputDirectory);
                }
                generateClassesToBeBound(filteredList, outputDirectory.getAbsolutePath());
                project.addCompileSourceRoot(outputDirectory.getPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            for (String type : classes) {
                getLog().info("Contained Serializable Type: " + type);
            }

        } catch (DependencyResolutionRequiredException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateClassesToBeBound(Iterable<String> classes, String outputDirectory) {
        Path targetFile = Paths.get(outputDirectory, "org/urdad/binding/ClassesToBeBoundList.java");

        List<String> contents = new LinkedList<>();
        contents.add("package org.urdad.binding;");
        contents.add("");

        contents.add("public class ClassesToBeBoundList{");
        contents.add("\tpublic static Class[] getClassesToBeBound(){");
        contents.add("\t\treturn new Class[] {");

        Iterator<String> iterator = classes.iterator();
        while (iterator.hasNext()) {
            contents.add("\t\t\t" + iterator.next().replace("$", ".") + ".class" + (iterator.hasNext() ? "," : ""));
        }
        contents.add("\t\t};");
        contents.add("\t}");
        contents.add("}");

        try {
            Files.deleteIfExists(targetFile);
            Files.createDirectories(targetFile.getParent());
            Files.write(targetFile, contents, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void emptyDirectory(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                emptyDirectory(file);
            }
            file.delete();
        }
    }
}
