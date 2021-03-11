package max.sander;

public class Pair<FirstType, SecondType> {
    FirstType first;
    SecondType second;
    public Pair(FirstType firstElement, SecondType secondElement) {
        this.first = firstElement;
        this.second = secondElement;
    }

    public FirstType getFirst() {
        return first;
    }

    public void setFirst(FirstType first) {
        this.first = first;
    }

    public SecondType getSecond() {
        return second;
    }

    public void setSecond(SecondType second) {
        this.second = second;
    }
}
