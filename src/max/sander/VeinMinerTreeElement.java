package max.sander;

import java.util.ArrayList;

public class VeinMinerTreeElement {
    static VeinMinerTreeElement root;
    static ArrayList<VeinMinerTreeElement> allElements = new ArrayList<VeinMinerTreeElement>();
    ArrayList<VeinMinerTreeElement> subElements = new ArrayList<VeinMinerTreeElement>();
    VeinMinerTreeElement parent;
    double x;
    double y; // level of the floor that is shown when the player stand on it
    double z;
    String type; //options: ore, stairs, mined, blocked
    public VeinMinerTreeElement(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.parent = null;
        allElements.add(this);
        root = this;
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
    public VeinMinerTreeElement(BlockPosWithDirection pos, VeinMinerTreeElement parent) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
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
    public void removeUnminedDuplicates() { // will set them to mined instead
        ArrayList<VeinMinerTreeElement> loopList = allElements;
        for (VeinMinerTreeElement element : loopList) {
            if (element == this) continue;
            if (element.getX() == x && element.getY() == y && element.getZ() == z && element.getType().equals("ore")) element.setType("mined");
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
    public double[] positionToDoubleArray() {
        return new double[] {this.getX(), this.getY(), this.getZ()};
    }
    public VeinMinerTreeElement findOreInSubelements() {

        for (VeinMinerTreeElement element : this.getSubElements()) {
            if (element.getType().equals("ore")) return element;
        }
        return null;
    }

    public ArrayList<double[]> getPath(VeinMinerTreeElement destination) throws HowDidThisHappenException {
        //fixme untested but i'm assuming it works
        ArrayList<double[]> steps = new ArrayList<double[]>();
        if (this == destination) {
            return steps;
        }

        ArrayList<VeinMinerTreeElement> startAndParents = this.getThisAndParents();
        ArrayList<VeinMinerTreeElement> destinationAndParents = destination.getThisAndParents();
        VeinMinerTreeElement closestCommonParent = null;
        for (VeinMinerTreeElement element : startAndParents) {
            if (destinationAndParents.contains(element)) {
                closestCommonParent = element;
                break;
            }
            steps.add(element.positionToDoubleArray());
        }
        if (closestCommonParent == null) throw new HowDidThisHappenException("tree building and maintainig musst be done wrong if this happens");
        for (int i = destinationAndParents.indexOf(closestCommonParent); i >= 0; i--) {
            steps.add(destinationAndParents.get(i).positionToDoubleArray());
        }

        return steps;
    }

    public VeinMinerTreeElement getNextOre() throws HowDidThisHappenException {
        VeinMinerTreeElement currentClosestOre = null;
        int distance = 1000000;//just a high number

        for (VeinMinerTreeElement element : allElements) {
            if (element.getType().equals("ore")) {
                if (this.getPath(element).size() <= distance) {
                    currentClosestOre = element;
                }
            }
        }

        return currentClosestOre;
    }

    public ArrayList<VeinMinerTreeElement> getThisAndParents() {
        ArrayList<VeinMinerTreeElement> thisAndParents = new ArrayList<VeinMinerTreeElement>();
        VeinMinerTreeElement currentParent = this;
        thisAndParents.add(currentParent);
        while (currentParent.getParent() != null) {
            currentParent = currentParent.getParent();
            thisAndParents.add(currentParent);
        }
        return thisAndParents;
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
