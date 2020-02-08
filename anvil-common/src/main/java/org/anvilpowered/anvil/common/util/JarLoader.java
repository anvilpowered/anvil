package org.anvilpowered.anvil.common.util;

import org.anvilpowered.anvil.api.plugin.Plugin;
import org.anvilpowered.anvil.api.plugin.PluginData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarLoader {

    public ArrayList<File> getJarsInPath(File file) throws IOException, NoSuchMethodException {
        ArrayList<File> out = new ArrayList<File>();
        Files.list(file.toPath()).forEach(path -> {
            out.add(path.toFile());
        });
        return out;
    }

    public Plugin<?> loadPluginJar(File file) throws IOException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        URLClassLoader child = new URLClassLoader(
                new URL[]{file.toURI().toURL()},
                this.getClass().getClassLoader()
        );

        List<String> classNames = new ArrayList<String>();
        ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                // This ZipEntry represents a class. Now, what class does it represent?
                String className = entry.getName().replace('/', '.'); // including ".class"
                classNames.add(className.substring(0, className.length() - ".class".length()));
            }
        }

        for(String name : classNames) {
            Class classToLoad = Class.forName(name, true, child);
            if(classToLoad.isAnnotationPresent(PluginData.class)) {
                return (Plugin<?>) classToLoad.newInstance();
            }
        }

        return null;
    }

}
