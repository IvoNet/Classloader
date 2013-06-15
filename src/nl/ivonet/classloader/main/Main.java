package nl.ivonet.classloader.main;

import nl.ivonet.classloader.trial.IvoNetClassLoader;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Ivo Woltring
 */
public class Main {

    public Main() throws ClassNotFoundException, IllegalAccessException, InstantiationException, MalformedURLException {

        IvoNetClassLoader urlcl = new IvoNetClassLoader(new URL[]{
                new URL("file:///Users/ivonet/dev/Classloader/ClassesNotOnClassPath/")});
        Class<?> classS = urlcl.loadClass("ivonet.IvoNet");
        for (Method field : classS.getMethods()) {
            System.out.println(field.getName());
        }

        System.out.println("classS = " + classS);

        Object o = classS.newInstance();

        System.out.println("o.toString() = " + o.toString());

    }

    public static void main(String[] args)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException, MalformedURLException {
        final Main main = new Main();
    }
}
