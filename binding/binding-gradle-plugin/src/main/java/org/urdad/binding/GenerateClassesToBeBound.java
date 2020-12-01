package org.urdad.binding;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskAction;
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

public class GenerateClassesToBeBound extends DefaultTask {
    public List<String> getPackagesToInclude() {
        return packagesToInclude;
    }

    public void setPackagesToInclude(List<String> packagesToInclude) {
        this.packagesToInclude = packagesToInclude;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public boolean isClearOutputDir() {
        return clearOutputDir;
    }

    public void setClearOutputDir(boolean clearOutputDir) {
        this.clearOutputDir = clearOutputDir;
    }

    private List<String> packagesToInclude = new ArrayList<>();
    private String outputDirectory;
    private boolean clearOutputDir;

    @TaskAction
    void generateClassesToBeBound() {
        List<URL> fileUrls = new ArrayList<>();
        for (Iterator<Configuration> iter = this.getProject().getConfigurations().iterator(); iter.hasNext(); ) {
            Configuration element = iter.next();
            try {
                Set<File> filesSet = element.resolve();
                for (Iterator<File> filesIterator = filesSet.iterator(); filesIterator.hasNext(); ) {
                    File file = filesIterator.next();
                    System.out.println(file.getName());
                    try {
                        fileUrls.add(new URL("file://" + file.getAbsolutePath()));
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IllegalStateException e){
                System.out.println("Skipping resolution of "+element.getName());
            }
        }

        List<ClassLoader> classLoaders = new ArrayList<>();
        classLoaders.add(ClasspathHelper.staticClassLoader());
        classLoaders.add(ClasspathHelper.contextClassLoader());
        classLoaders.add(URLClassLoader.newInstance(fileUrls.toArray(new URL[0])));

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
            Files.createDirectories(Paths.get(outputDirectory));
            if (clearOutputDir) {
                emptyDirectory(new File(outputDirectory));
            }
            generateClassesToBeBound(filteredList, outputDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        for (String type : classes) {
            System.out.println("Contained Serializable Type: " + type);
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
