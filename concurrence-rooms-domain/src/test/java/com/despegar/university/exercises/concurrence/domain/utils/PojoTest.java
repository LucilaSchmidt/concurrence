package com.despegar.university.exercises.concurrence.domain.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


@SuppressWarnings({"rawtypes", "unchecked"})
public class PojoTest {

    // ant like pattern matching. @see PathMatchingResourcePatternResolver
    // NOTE: classes to find will be those that aren't enums, interfaces or abstracts
    private static final String[] packagesWhereToFindModelClasses = new String[] {"com/despegar/university/**/model/**"};

    private static final Method OBJECT_TOSTRING_METHOD = getObjectClassToStringMethod();

    private static Method getObjectClassToStringMethod() {
        try {
            return Object.class.getDeclaredMethod("toString", new Class[] {});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }


    private Object[][] classesForTesting;

    @BeforeClass
    public void findClasses() throws IOException, ClassNotFoundException {
        List<Class> classes = new LinkedList<Class>();
        for (String packageWhereToFindModelClasses : packagesWhereToFindModelClasses) {
            classes.addAll(this.findClassesForPackage(packageWhereToFindModelClasses));
        }

        orderClassesByClassName(classes);

        this.classesForTesting = new Object[classes.size()][];
        int i = 0;
        for (Class clazz : classes) {
            this.classesForTesting[i++] = new Object[] {clazz};
        }
    }

    @DataProvider
    public Object[][] getClasses() {
        return this.classesForTesting;
    }

    @Test(dataProvider = "getClasses", enabled = false)
    public void class_class_hasToStringImplemented(Class clazz) {
        Assert.assertNotEquals(getToStringMethodSafely(clazz), OBJECT_TOSTRING_METHOD,
            String.format("%s must have toString implemented", clazz.getName()));
    }

    @Test(dataProvider = "getClasses")
    public void class_class_hasDeclaredToString(Class clazz) {
        Assert.assertTrue(hasDeclaredToString(clazz), String.format("%s must have toString declared", clazz.getName()));
    }


    @Test(dataProvider = "getClasses")
    public void class_newClassInstanceUsingObjenesis_toStringReturnsString(Class clazz) {
        Assert.assertNotNull(safeToString(clazz),
            String.format("%s toString must work still with properties null set", clazz.getName()));
    }

    @Test(dataProvider = "getClasses")
    public void class_newClassInstanceUsingObjenesis_equalsToItselfReturnsTrue(Class clazz) throws SecurityException,
        NoSuchMethodException {
        Assert.assertTrue(equalsToItself(clazz),
            String.format("%s.equals() implementation must work even when no properties are set", clazz.getName()));
    }

    @Test(dataProvider = "getClasses")
    public void class_newClassInstanceUsingObjenesis_hashCodeReturnsDefaultValue(Class clazz) throws SecurityException,
        NoSuchMethodException {
        Assert.assertNotNull(hashCodeReturnsValue(clazz),
            String.format("%s.hashCode() implementation must work even when no properties are set", clazz.getName()));
    }

    @Test(dataProvider = "getClasses")
    public void class_class_hasBothMethodsEqualsAndHashCodeImpementedOrNone(Class clazz) {

        Assert.assertEquals(hasDeclaredEquals(clazz), hasDeclaredHashCode(clazz),
            String.format("%s must implement both methods (equals and hashCode)  or none", clazz.getName()));

    }

    private static Method getToStringMethodSafely(Class clazz) {
        try {
            return clazz.getMethod("toString", new Class[] {});
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean hasDeclaredToString(Class clazz) {
        try {
            clazz.getDeclaredMethod("toString", new Class[] {});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean hasDeclaredEquals(Class clazz) {
        try {
            clazz.getDeclaredMethod("equals", new Class[] {Object.class});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean hasDeclaredHashCode(Class clazz) {
        try {
            clazz.getDeclaredMethod("hashCode", new Class[] {});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean equalsToItself(Class clazz) throws SecurityException, NoSuchMethodException {
        Objenesis objenesis = new ObjenesisStd(); // this needs objenesis which is given transitively by mockito
        ObjectInstantiator clazzIstantiator = objenesis.getInstantiatorOf(clazz);

        Method equalsMethod = clazz.getMethod("equals", Object.class);
        Method objectEqualsMethod = Object.class.getMethod("equals", Object.class);

        if (!(equalsMethod.equals(objectEqualsMethod))) {
            Object newInstance = clazzIstantiator.newInstance();
            Object anotherInstance = clazzIstantiator.newInstance();

            try {
                return newInstance.equals(anotherInstance);
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    private static Integer hashCodeReturnsValue(Class clazz) {
        Objenesis objenesis = new ObjenesisStd(); // this needs objenesis which is given transitively by mockito
        ObjectInstantiator clazzIstantiator = objenesis.getInstantiatorOf(clazz);

        try {
            return clazzIstantiator.newInstance().hashCode();
        } catch (Exception e) {
            return null;
        }
    }

    private static String safeToString(Class clazz) {
        Objenesis objenesis = new ObjenesisStd(); // this needs objenesis which is given transitively by mockito
        ObjectInstantiator clazzIstantiator = objenesis.getInstantiatorOf(clazz);

        try {
            return clazzIstantiator.newInstance().toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static void orderClassesByClassName(List<Class> classes) {
        Collections.sort(classes, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Class) o1).getName().compareTo(((Class) o2).getName());
            }
        });
    }

    // @see PathMatchingResourcePatternResolver
    // @see http://stackoverflow.com/questions/1456930/how-do-i-read-all-classes-from-a-java-package-in-the-classpath
    private List<Class> findClassesForPackage(String basePackage) throws IOException, ClassNotFoundException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        List<Class> candidates = new ArrayList<Class>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + this.resolveBasePackage(basePackage)
            + "/*.class";
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (this.isCandidate(metadataReader)) {
                    candidates.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                }
            }
        }
        return candidates;
    }

    private String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
    }

    private boolean isCandidate(MetadataReader metadataReader) throws ClassNotFoundException {
        try {
            Class clazz = Class.forName(metadataReader.getClassMetadata().getClassName());
            if (!clazz.isEnum() && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())
                && !clazz.getName().contains("$")) {
                return true;
            }
        } catch (Throwable e) {
        }
        return false;
    }
}
