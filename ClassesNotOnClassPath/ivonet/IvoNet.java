package ivonet;

/**
 * @author Ivo Woltring
 */
public class IvoNet {

    public IvoNet() {
        System.out.println("IvoNet Loaded by      : " + this.getClass().getClassLoader().getClass().getName());
    }



    @Override
    public String toString() {
        return "I'm an IvoNet class";
    }
}
