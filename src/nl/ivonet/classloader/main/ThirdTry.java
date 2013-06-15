package nl.ivonet.classloader.main;

import ivonet.Third;
import nl.ivonet.classloader.trial.IvoNetClassLoader;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This ClassLoader trial illustrates the workings of two completely separate Classloaders.
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
public class ThirdTry {

    /**
     * Note that this path now points to the same location as the output folder
     * where the classes are compiled in the IDE that is normally on the classpath!
     * I use
     */
    private static final String CLASSLOADER_CLASSES_NOT_ON_CLASS_PATH =
            "file:///Users/ivonet/dev/Classloader/out/production/Classloader/";


    public ThirdTry()
            throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        //The Third class that is on the classpath
        Third third = new Third();
        System.out
                .println("Just instantiated the 'Third' class by using a new statement.\nIt is on the normal "
                         + "classpath and is therefore loaded by:\nsun.misc.Launcher$AppClassLoader.");

        //Note: I've set the parent classloader to null so it won't escalate the search
        final IvoNetClassLoader ivoNetClassLoader = new IvoNetClassLoader(new URL[]{
                new URL(CLASSLOADER_CLASSES_NOT_ON_CLASS_PATH)}, null);

        final Class<?> thirdByAnotherClassloader = ivoNetClassLoader.loadClass("ivonet.Third");
        final Object thirdByAnotherClassloaderInstance = thirdByAnotherClassloader.newInstance();

        try {
            third = (Third) thirdByAnotherClassloaderInstance;
        } catch (ClassCastException e) {
            System.out.println(
                    "I've just tried to cast the Third loaded by IvoNetClassLoader\nto Third on the classpath and "
                    + "gotten a ClassCastException with message:\n"
                    + e.getMessage() + "\nFunny isn't it?!\nEspecially because It is exactly the same class!");
        }
    }

    public static void main(final String[] args) throws Exception {
        System.out.println("Third Try");
        new ThirdTry();
    }
}
