package com.huchonglin.util;

import com.huchonglin.annotation.Controller;
import com.huchonglin.annotation.Repository;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * @author: hcl
 * @date: 2020/7/2 19:42
 */
public class ClassUtil {
    private final static String SEPARATOR = File.separator;
    private final static String DOT_JAVA = ".java";
    /**
     * 保存Class的路径
     */
    private static Set<String> CLASS_LOCATION_PATH;

    private static Set<Class<?>> CLASS_SET_PO = new HashSet<>();

    private static Set<Class<?>> CLASS_SET_ALL;

    private static Set<Class<?>> CLASS_SET_CONTROLLER = new HashSet<>();
    /**
     * 加载 src\main\java\basePackage...的类
     *
     * @param basePackage 包的路径
     * @return 类集合
     */
    public static Set<Class<?>> loadClassFromPackage(String basePackage) {
        CLASS_LOCATION_PATH = new HashSet<>();
        CLASS_SET_ALL = new HashSet<>();

        String beginPath = "";
        String projectPath = getMavenProjectPath();

        projectPath = projectPath.replace("/", SEPARATOR);
        String[] split = basePackage.split("\\.");
        for (int i = 0; i < split.length; i++) {
            if (i == 0) {
                beginPath = split[i];
            }
            projectPath += SEPARATOR + split[i];

        }
        if(projectPath.startsWith(SEPARATOR)){
            projectPath = projectPath.substring(1);
        }
        //     F:\workspace_idea\workspace_testAndStudy\javaee\simple-spring-Test\src\test\java\org\test\controller
        File file = new File(projectPath);
        loadClassFromPackage(file, projectPath);
        for (String s : CLASS_LOCATION_PATH) {
            String a = "";
            String java = s.substring(s.lastIndexOf(beginPath));
            String[] split1 = java.split("\\\\");
            for (int i = 0; i < split1.length; i++) {
                if (i == 0) {
                    a += split1[i];
                } else {
                    a += "." + split1[i];
                }
            }
            a = a.replace(".java", "");
            try {
                Class<?> cls = Class.forName(a);
                CLASS_SET_ALL.add(cls);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return CLASS_SET_ALL;
    }

    /**
     * 获取maven下项目的路径
     * @return /src/main/java/projectName
     */
    private static String getMavenProjectPath() {
        String projectPath = WebUtil.getProjectPath();
        projectPath += SEPARATOR + "src" + SEPARATOR +
                "main" + SEPARATOR +
                "java";
        return projectPath;
    }


    public static void loadClassFromPackage(File file, String path) {
        if (file.isFile() && file.getName().endsWith(DOT_JAVA)) {
            try {
                CLASS_LOCATION_PATH.add(file.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;

        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                String canonicalPath = path;
                canonicalPath += File.separator;
                canonicalPath += file1.getName();
                loadClassFromPackage(file1, canonicalPath);
            }
        }
    }


    public static Set<Class<?>> getClassSetOfController() {
        if (!CLASS_SET_CONTROLLER.isEmpty()) {
            return CLASS_SET_CONTROLLER;
        } else {
            for (Class<?> clazz : CLASS_SET_ALL) {
                if (null != clazz.getAnnotation(Controller.class)) {
                    CLASS_SET_CONTROLLER.add(clazz);
                }
            }
            return CLASS_SET_CONTROLLER;
        }
    }

    public static Set<Class<?>> getClassSetOfRepository() {
        if (!CLASS_SET_PO.isEmpty()) {
            return CLASS_SET_PO;
        } else {
            for (Class<?> clazz : CLASS_SET_ALL) {
                if (null != clazz.getAnnotation(Repository.class)) {
                    CLASS_SET_PO.add(clazz);
                }
            }
            return CLASS_SET_PO;
        }
    }

    public static Set<Class<?>> getClassSetAll() {
        return CLASS_SET_ALL;
    }
}
