package nl.ivonet.classloader.main;

import nl.ivonet.classloader.trial.IvoNetClassLoader;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This ClassLoader trial just illustrates the workings of the Classloader.
 * <p/>
 * Howto get it to work:
 * <ul>
 *     <li>First adjust the CLASSLOADER_CLASSES_NOT_ON_CLASS_PATH to point to the correct location</li>
 *     <li>Run this class</li>
 *     <li>you should get a ClassNotFoundException...</li>
 *     <li>Open a console in the ClassesNotOnClassPath folder of this project</li>
 *     <li>perform command: javac ivonet/*.java</li>
 *     <li>Run this class</li>
 * </ul>
 *
 * If all goes well you should see some information on the commandline proving that classes not on the normal
 * classpath can be loaded.<br/>
 * The IvoNet and the ClassInClass will print out by what classloader they have been loaded.
 * In this case it should always be the IvoNetClassLoader.
 *
 *<p/>
 * I've commented out some stuff to give you ideas for further trials.
 */
public class FirstTry {

    private static final String CLASSLOADER_CLASSES_NOT_ON_CLASS_PATH =
            "file:///Users/ivonet/dev/Classloader/ClassesNotOnClassPath/";

    public FirstTry()
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, MalformedURLException {

        final IvoNetClassLoader ivoNetClassLoader = new IvoNetClassLoader(new URL[]{
                new URL(CLASSLOADER_CLASSES_NOT_ON_CLASS_PATH)});

        final Class<?> ivoNet = ivoNetClassLoader.loadClass("ivonet.IvoNet");

//        for (final Method field : ivoNet.getMethods()) {
//            System.out.println(field.getName());
//        }

//        System.out.println("ivoNet = " + ivoNet);
        final Object ivoNetInstance = ivoNet.newInstance();
        System.out.println("ivoNetInstance.toString() = " + ivoNetInstance.toString());

        final Class<?> classInClass = ivoNetClassLoader.loadClass("ivonet.ClassInClass");
//        for (final Method method : classInClass.getMethods()) {
//            System.out.println("method.getName() = " + method.getName());
//        }

//        System.out.println("classInClass = " + classInClass);
        final Object classInClassInstance = classInClass.newInstance();
        System.out.println("classInClassInstance.toString() = " + classInClassInstance.toString());

    }

    public static void main(final String[] args) throws Exception {
        new FirstTry();
    }
}
