package com.huchonglin.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.huchonglin.anno.Aspect;
import com.huchonglin.anno.Bean;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: hcl
 * @date: 2020/7/2 19:42
 */
@Slf4j
public class ClassUtil {
    private final static String SEPARATOR = File.separator;
    private final static String DOT_JAVA = ".java";
    /**
     * 保存Class的路径
     */
    private static Set<String> CLASS_LOCATION_PATH;

    private static Set<Class<?>> CLASS_SET_ASPECT = new HashSet<>();

    private static Set<Class<?>> CLASS_SET_ALL;

    private static Set<Class<?>> CLASS_SET_BEAN = new HashSet<>();
    private static Set<Class<?>> CLASS_SET_CONTROLLER = new HashSet<>();

    /**
     * 加载 src\main\java\basePackage...的类
     *
     * @param basePackage 包的路径
     * @return 类集合
     */
    public static void getPackageClass(String basePackage) {
        CLASS_LOCATION_PATH = new HashSet<>();
        CLASS_SET_ALL = new HashSet<>();

        String canonicalPath = WebUtil.getProjectPath();
        String beginPath = "";
        canonicalPath += SEPARATOR + "src" + SEPARATOR +
                "test" + SEPARATOR +
                "java";
        canonicalPath.replaceAll("/",SEPARATOR);
        String[] split = basePackage.split("\\.");
        for (int i = 0; i < split.length; i++) {
            if (i == 0) {
                beginPath = split[i];
            }
            canonicalPath += SEPARATOR + split[i];

        }
        File file = new File(canonicalPath);
        getPackageClass(file, canonicalPath);
        for (String s : CLASS_LOCATION_PATH) {
            String a = "";
            String java = s.substring(s.lastIndexOf(beginPath), s.length());
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
                Class<?> aClass = Class.forName(a);
                CLASS_SET_ALL.add(aClass);
                if (null != aClass.getAnnotation(Aspect.class)) {
                    CLASS_SET_ASPECT.add(aClass);
                }
                if (null != aClass.getAnnotation(Bean.class)) {
                    CLASS_SET_BEAN.add(aClass);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    public static void getPackageClass(File file, String path) {
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
                getPackageClass(file1, canonicalPath);
            }
        }
    }

    public static Set<Class<?>> getClassSetOfAspect() {
        if (!CLASS_SET_ASPECT.isEmpty()) {
            return CLASS_SET_ASPECT;
        } else {
            for (Class<?> clazz : CLASS_SET_ALL) {
                if (clazz.isInterface()) {
                    continue;
                }
                if (null != clazz.getAnnotation(Aspect.class)) {
                    CLASS_SET_ASPECT.add(clazz);
                }
            }
            return CLASS_SET_ASPECT;
        }
    }

    public static Set<Class<?>> getClassSetOfBean() {
        if (!CLASS_SET_BEAN.isEmpty()) {
            return CLASS_SET_BEAN;
        } else {
            for (Class<?> clazz : CLASS_SET_ALL) {
                if (null != clazz.getAnnotation(Bean.class)) {
                    CLASS_SET_BEAN.add(clazz);
                }
            }
            return CLASS_SET_BEAN;
        }
    }

    public static Set<Class<?>> getClassSetOfController() {
        if (!CLASS_SET_CONTROLLER.isEmpty()) {
            return CLASS_SET_CONTROLLER;
        } else {
            for (Class<?> clazz : CLASS_SET_ALL) {
                if (null != clazz.getAnnotation(Bean.class)) {
                    CLASS_SET_CONTROLLER.add(clazz);
                }
            }
            return CLASS_SET_CONTROLLER;
        }
    }

    public static Set<Class<?>> getClassSetAll() {
        return CLASS_SET_ALL;
    }
}
