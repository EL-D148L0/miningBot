package max.sander;

public class BooleanHolder {
    public boolean a;
    public BooleanHolder(boolean in) {
        this.a = in;
    }
    public BooleanHolder() {

    }
    public void set(boolean in) {
        this.a = in;
    }

    public boolean get() {
        return a;
    }

    @Override
    public String toString() {
        return "" + a;
    }
}
