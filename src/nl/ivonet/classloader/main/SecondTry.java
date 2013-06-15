package nl.ivonet.classloader.main;

import nl.ivonet.classloader.trial.IvoNetClassLoader;
import nl.ivonet.classloader.trial.IvoNetClassLoader2;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This ClassLoader trial illustrates the workings of the Classloader with a parent Classloader.
 * <p/>
 * Howto get it to work:
 * <ul>
 *     <li>First adjust the CLASSLOADER_CLASSES_NOT_ON_CLASS_PATH and
 *     CLASSLOADER_CLASSES_NOT_ON_CLASS_PATH2 constants to point to the correct locations</li>
 *     <li>Open a console in the ClassesNotOnClassPath folder of this project</li>
 *     <li>Command: rm -f ivonet/*.class OR del ivonet\*.class</li>
 *     <li>Open a console in the ClassesNotOnClassPath2 folder of this project</li>
 *     <li>Command: rm -f ivonet/*.class OR del ivonet\*.class</li>
 *     <li>Run this class</li>
 *     <li>You should get ClassNotFoundExceptions</li>
 *     <li>Open a console in the ClassesNotOnClassPath folder of this project</li>
 *     <li>perform command: javac ivonet/*.java</li>
 *     <li>Run this class' main method</li>
 *     <li>Both the ClassInClass and the IvoNet class should be loaded by IvoNetClassLoader!</li>
 *     <li>Open a console in the ClassesNotOnClassPath folder of this project</li>
 *     <li>Command: rm -f ivonet/ClassInClass.class OR del ivonet\ClassInClass.class</li>
 *     <li>Open a console in the ClassesNotOnClassPath2 folder of this project</li>
 *     <li>Command: javac ivonet/ClassInClass.class</li>
 *     <li>Run this class</li>
 *     <li>Now the IvoNet class should be loaded by the IvoNetClassLoader,
 *     but the ClassInClass class should be loaded by the IvoNetClassLoader2
 *     even though ivonet/IvoNet.class also exists in the ClassesNotOnClassPath2</li>
 * </ul>
 *
 * Note that all the classes are going through the IvoNetClassLoader2 but first loading is delegated
 * to its parent and the that's why you will see two different classloaders in the last step of this trial.
 */
public class SecondTry {
    private static final String CLASSLOADER_CLASSES_NOT_ON_CLASS_PATH =
            "file:///Users/ivonet/dev/Classloader/ClassesNotOnClassPath/";
    private static final String CLASSLOADER_CLASSES_NOT_ON_CLASS_PATH2 =
            "file:///Users/ivonet/dev/Classloader/ClassesNotOnClassPath2/";


    public SecondTry()
            throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        final IvoNetClassLoader ivoNetClassLoader = new IvoNetClassLoader(new URL[]{
                new URL(CLASSLOADER_CLASSES_NOT_ON_CLASS_PATH)});

        //This Classloader has the IvoNetClassLoader as a parent!
        final IvoNetClassLoader2 ivoNetClassLoader2 = new IvoNetClassLoader2(new URL[]{
                new URL(CLASSLOADER_CLASSES_NOT_ON_CLASS_PATH2)}, ivoNetClassLoader);

        final Class<?> classInClass2 = ivoNetClassLoader2.loadClass("ivonet.ClassInClass");

        final Object classInClassInstance2 = classInClass2.newInstance();
        System.out.println("classInClassInstance2 = " + classInClassInstance2);

        final Class<?> ivoNet = ivoNetClassLoader2.loadClass("ivonet.IvoNet");
        final Object ivoNetInstance = ivoNet.newInstance();
        System.out.println("ivoNetInstance = " + ivoNetInstance);

    }

    public static void main(final String[] args) throws Exception {
        new SecondTry();
    }
}
