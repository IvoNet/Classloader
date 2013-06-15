package ivonet;

/**
 * @author Ivo Woltring
 */
public class ClassInClass {
    private final IvoNet ivoNet;

    public ClassInClass() {
        ivoNet = new IvoNet();
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClassInClass{");
        sb.append("ivoNet=").append(ivoNet);
        sb.append('}');
        return sb.toString();
    }
}
