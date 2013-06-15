package ivonet;

/**
 *
 * @author Ivo Woltring
 */
public class Third {

    public Third() {
        System.out.println("Third Loaded by       : " + getClass().getClassLoader().getClass().getName());
    }
}
