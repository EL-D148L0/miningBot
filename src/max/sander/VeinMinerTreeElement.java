package max.sander;

import java.util.ArrayList;

public class VeinMinerTreeElement {
    static ArrayList<VeinMinerTreeElement> allElements = new ArrayList<VeinMinerTreeElement>();
    ArrayList<VeinMinerTreeElement> subElements = new ArrayList<VeinMinerTreeElement>();
    VeinMinerTreeElement parent;
    double x;
    double y; // level of the floor
    double z;
    String type; //options: ore, stairs, mined, blocked
    public VeinMinerTreeElement(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.parent = null;
        allElements.add(this);
        this.type = "mined";
    }
    public VeinMinerTreeElement(double x, double y, double z, VeinMinerTreeElement parent) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.parent = parent;
        allElements.add(this);
        parent.subElements.add(this);
        this.type = "ore";
        checkBlockedDuplicates();
    }
    static void clearTree() {
        for (VeinMinerTreeElement element : allElements) {
            element.delete();
        }
        allElements.clear();
    }
    static boolean isCleared() {
        boolean clear = true;
        for (VeinMinerTreeElement element : allElements) {
            if (element.type.equals("ore")) {
                clear = false;
                break;
            }
        }
        return clear;
    }
    public void removeUnminedDuplicates() {
        for (VeinMinerTreeElement element : allElements) {
            if (element == this) continue;
            if (element.getX() == x && element.getY() == y && element.getZ() == z && element.getType().equals("ore")) element.delete();
        }

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public void checkBlockedDuplicates(){
        //will check for duplicates of this position that are "blocked"
        //if it finds any it will change itself to "blocked"

        for (VeinMinerTreeElement element : allElements) {
            if (element == this) continue;
            if (element.getX() == x && element.getY() == y && element.getZ() == z && element.getType().equals("blocked")) this.setType("blocked");
        }
    }

    public void delete() {
        allElements.remove(this);
        if (this.parent != null) {
            this.parent.subElements.remove(this);
        }
    }

    public ArrayList<VeinMinerTreeElement> getSubElements() {
        return subElements;
    }

    public VeinMinerTreeElement getParent() {
        return parent;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
