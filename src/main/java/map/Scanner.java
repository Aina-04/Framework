package main.java.map;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Scanner {
    public enum AnnotationLevel {
        CLASS,
        FIELD,
        METHOD
    }

    public static List<String> scanAnnotatedClasses(String pkg, Class<? extends Annotation> annotation,
            AnnotationLevel level) throws Exception {
        List<String> result = new ArrayList<>();
        for (String className : findClasses(pkg)) {
            if (hasAnnotation(className, annotation, level)) {
                result.add(className);
            }
        }
        Collections.sort(result);
        return result;
    }

    public static Map<String, Mapping> scanModels(String pkg) throws Exception {
        Map<String, Mapping> result = new HashMap<>();

        for (String className : findClasses(pkg)) {
            addClassInfo(result, className);
        }

        return result;
    }

    private static Set<String> findClasses(String pkg) throws IOException {
        Set<String> classNames = new LinkedHashSet<>();
        String path = pkg.replace('.', '/');
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = cl.getResources(path);

        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            String protocol = url.getProtocol();

            if ("file".equals(protocol)) {
                File dir = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
                scanDirectory(dir, pkg, classNames);
            } else if ("jar".equals(protocol)) {
                scanJar(url, path, classNames);
            }
        }

        return classNames;
    }

    private static void scanDirectory(File dir, String pkg, Set<String> classNames) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, pkg + "." + file.getName(), classNames);
                continue;
            }

            String name = file.getName();
            if (name.endsWith(".class") && !name.contains("$")) {
                classNames.add(pkg + "." + name.substring(0, name.length() - 6));
            }
        }
    }

    private static void scanJar(URL url, String path, Set<String> classNames) throws IOException {
        JarURLConnection connection = (JarURLConnection) url.openConnection();
        try (JarFile jarFile = connection.getJarFile()) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith(path) && name.endsWith(".class") && !name.contains("$")) {
                    classNames.add(name.substring(0, name.length() - 6).replace('/', '.'));
                }
            }
        }
    }

    private static boolean hasAnnotation(String className, Class<? extends Annotation> annotation,
            AnnotationLevel level) throws Exception {
        Class<?> clazz = Class.forName(className);

        if (level == AnnotationLevel.CLASS) {
            return clazz.isAnnotationPresent(annotation);
        }

        if (level == AnnotationLevel.FIELD) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotation)) {
                    return true;
                }
            }
            return false;
        }

        if (level == AnnotationLevel.METHOD) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation)) {
                    return true;
                }
            }
            return false;
        }

        return false;
    }

    private static void addClassInfo(Map<String, Mapping> result, String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        Field[] fields = clazz.getDeclaredFields();

        String[] cols = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            cols[i] = fields[i].getName();
        }

        Mapping m = new Mapping();
        m.setTableName(clazz.getSimpleName());
        m.setFields(fields);
        m.setColonnes(cols);

        result.put(clazz.getName(), m);
    }
}
