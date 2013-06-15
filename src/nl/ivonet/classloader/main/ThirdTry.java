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
 *     <li>First adjust the CLASSLOADER_CLASSES_ON_CLASS_PATH to point to the compile path of your IDE</li>
 *     <li>Run this class</li>
 *     <li>You should get a nice output message. See if you can explain why all this happens...</li>
 * </ul>
 */
public class ThirdTry {

    /**
     * Note that this path now points to the same location as the output folder
     * where the classes are compiled in the IDE that is normally on the classpath!
     * I use IntelliJ so the path will be something like the one below.
     */
    private static final String CLASSLOADER_CLASSES_ON_CLASS_PATH =
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
                new URL(CLASSLOADER_CLASSES_ON_CLASS_PATH)}, null);

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
