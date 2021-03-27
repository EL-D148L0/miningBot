package max.sander;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;

public class Main {
    static int fontPixelSize = 3;
    static boolean secondScreen = false;


    static boolean holdingLMB = false;
    static boolean holdingRMB = false;
    static boolean holdingShift = false;
    static Robot robot;
    static BufferedImage map;
    static int mapOffsetX = 0;
    static int mapOffsetZ = 0;
    static int yawDiff; // values are made for facing toward negative Z     yawDiff is added to yaw values
    static int currentTunnelingDirection = 0; //  0: positive X 1: negative X 2: positive Z 3: negative Z
    static boolean debuggingMode = true;


    //COLOR RGB INTS
    static int green = -16711936;
    static int gray = -8355712;
    static int red = -65536;
    static int orange = -14336;
    static int black = -16777216;
    static int magenta = -65281;
    static int white = -1;
    static int blue = -16776961;
    static int cyan = -16711681;
    static int pink = -20561;
    static int yellow = -256;


    static int rgb221 = -2236963;
    static int rgb252 = -197380;

    static int positionOutOfMap = -23425;// gets returned if a map color outside of the map gets checked. is an ugly skin-color-ish color.


    //hotbar slots:

    static char slotPickaxe = '1';
    static char slotShovel = '2';
    static char slotTorches = '3';
    static char slotFood = '4';
    static char slotBlocks = '5';
    static char slotLava = '6';
    static char slotWater = '7';
    static char slotSlabs = '8';

    //block collections
    static String[] ores =  new String[]{"minecraft:emerald_ore", "minecraft:gold_ore", "minecraft:iron_ore",
            "minecraft:coal_ore", "minecraft:diamond_ore", "minecraft:redstone_ore", "minecraft:lapis_ore"};
    static String[] stones =  new String[]{"minecraft:cobblestone", "minecraft:stone", "minecraft:diorite",
            "minecraft:granite", "minecraft:andesite", "minecraft:dirt", "minecraft:sand", "minecraft:gravel"};

    /*


    FIXME
     -
     -
     - add the function that respond to mobs with the procedure described in a comment below.
     - vein mining functions
     - redo the scan functions using the new pointAtPos function
     - idea: make a faster pointat function that calculates if it should be able to see the target at the current heading so it doesn't spend much time swiveling  --> done i guess

    */


    public static void main(String[] args) {
	// write your code here
        String debug;
        double[] pos;
        try {

            if (false) throw new DebugTextIncompleteException("whatever"); // this is here to keep the catch even if i'm not using anything that generates this exception
            robot = new Robot();

            initMap();

            debug = getDebug(getGameScreen());
            if (debug.contains("positive X")) {
                yawDiff = 90;
                currentTunnelingDirection = 0;
            } else if (debug.contains("negative X")) {
                yawDiff = -90;
                currentTunnelingDirection = 1;
            } else if (debug.contains("positive Z")) {
                yawDiff = 180;
                currentTunnelingDirection = 2;
            } else if (debug.contains("negative Z")) {
                yawDiff = 0;
                currentTunnelingDirection = 3;
            }


            debug = getDebug(getGameScreen());
            //System.out.println(debug);

//            mineVeinTunnelLevel();
//            BlockPosWithDirection testBPWD = new BlockPosWithDirection(35, 56, 31, 0);
//            System.out.println(moveToBlockUp(testBPWD, Timeout.newTimeout(10000)));


            ArrayList<BlockPos> seenBlocks = new ArrayList<>();
            ArrayList<BlockPos> oreBlocks = new ArrayList<>();
            ArrayList<BlockPos> minedBlocks = new ArrayList<>();
            ArrayList<BlockPos> hazardBlockingBlocks = new ArrayList<>();
            seenBlocks.addAll(generateBlockPosArrayList(46, 56, 87, 50, 57, 87));
            seenBlocks.addAll(generateBlockPosArrayList(46, 56, 85, 50, 57, 85));
            minedBlocks.addAll(generateBlockPosArrayList(46, 56, 86, 50, 57, 86));
            seenBlocks.addAll(generateBlockPosArrayList(46, 55, 86, 50, 55, 86));
            seenBlocks.addAll(generateBlockPosArrayList(46, 58, 86, 50, 58, 86));
            seenBlocks.addAll(generateBlockPosArrayList(51, 56, 86, 51, 57, 86));
            oreBlocks.addAll(generateBlockPosArrayList(50, 56, 87, 50, 57, 87));


            Vein vein = new Vein(seenBlocks, oreBlocks, minedBlocks, hazardBlockingBlocks);
//            vein.mine();

            






        } catch (DebugTextIncompleteException e) {
            if (!debuggingMode) {
                try {
                    logOff();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception");
            e.printStackTrace();
        } finally {
            System.out.println("Main finally exit");
            endProgram();
        }
    }
    static ArrayList<BlockPos> generateBlockPosArrayList(int x1, int y1, int z1, int x2, int y2, int z2) {
        ArrayList<BlockPos> out = new ArrayList<>();

        int smallX;
        int bigX;
        int smallZ;
        int bigZ;
        int smallY;
        int bigY;
        if (x1 > x2) {
            bigX = x1;
            smallX = x2;
        } else {
            bigX = x2;
            smallX = x1;
        }
        if (y1 > y2) {
            bigY = y1;
            smallY = y2;
        } else {
            bigY = y2;
            smallY = y1;
        }
        if (z1 > z2) {
            bigZ = z1;
            smallZ = z2;
        } else {
            bigZ = z2;
            smallZ = z1;
        }
        for (int x = smallX; x <= bigX; x++) {
            for (int y = smallY; y <= bigY; y++) {
                for (int z = smallZ; z <= bigZ; z++) {
                    out.add(new BlockPos(x, y, z));
                }
            }
        }
        return out;
    }
    static BlockPosWithHeight getCurrentBPWH(ArrayList<BlockPosWithHeight> floorPlan, String debug) throws HowDidThisHappenException {
        BlockPosWithHeight currentBPWH = null;
        double[] playerPosFloored = getPlayerPosFloored(debug);
        playerPosFloored[1] -= 1;
        for (BlockPosWithHeight i : floorPlan) {
            if (i.equalsDoubleArray(playerPosFloored)) {
                currentBPWH = i;
            }
        }
        if (currentBPWH == null) throw new HowDidThisHappenException("player is outside of floorplan");
        return currentBPWH;
    }

    static String mineVeinTunnelLevel() throws InterruptedException, DebugTextIncompleteException, HowDidThisHappenException, UnexpectedGameBehaviourException {
        /* call this facing the wall that contains ore
        * this should mine all the ore of the vein that is on the same level as the tunnel and return to the location where it was called returning "done".
        * if it runs into an unexpected situation (cave or liquid etc) it will depending on the gravity of the situation return to the location it was
        * called, block off the dug out space and return "unexpected".
        * returns:
        * done: done
        * unexpected: vein has been abandoned and blocked off
        * */

        String debug = getDebug(getGameScreen());
        BlockPosWithDirection playerPos = new BlockPosWithDirection(debug);

        VeinMinerTreeElement root = new VeinMinerTreeElement(playerPos.getX(), playerPos.getY(), playerPos.getZ());
        VeinMinerTreeElement currentLocation = root;
        VeinMinerTreeElement currentElement = new VeinMinerTreeElement(playerPos.forward().getX(), playerPos.forward().getY(), playerPos.forward().getZ(), root);
        BlockPosList seenBlocks = new BlockPosList();

        while (!VeinMinerTreeElement.isCleared()) {
            if (currentElement.getType().equals("ore")) {
                String result = mineVeinMinerTreeElement(currentElement, seenBlocks, true);
                if (result.equals("flee")) {
                    writeChatDebug("flight triggered. run away!");
                    //TODO put flee function here once it exists
                    return "unexpected";
                } else if (result.equals("aborted")) {
                    currentElement.removeUnminedDuplicates();
                } else if (result.equals("done")) {
                    currentElement.removeUnminedDuplicates();
                } else {
                    throw new HowDidThisHappenException("how? if this comes up during testing something must have gone extremely wrong");
                }
            } else {
                currentElement = currentElement.getNextOre();
                if (currentElement == null) {
                    throw new HowDidThisHappenException("unless isCleared doesn't work this should never happen");
                }
                ArrayList<double[]> path = currentLocation.getPath(currentElement.getParent());
                if (followFlatPath(path)) {
                    currentLocation = currentElement.getParent();
                } else throw new UnexpectedGameBehaviourException("movement was apparently blocked on a known route");
            }
        }

        ArrayList<double[]> path = currentLocation.getPath(root);
        if (followFlatPath(path)) {
            return "done";
        } else throw new UnexpectedGameBehaviourException("movement was apparently blocked on a known route or the pathfollowing function still doesn't work.");


        //TODO fleeing mechanics must contain map recolouring of cutoff tunnel part

    }

    static boolean moveToBlockUp(BlockPosWithDirection targetBPWD, int timeoutID) throws InterruptedException, HowDidThisHappenException {
        // targetBPWD/target must be one block down from the floor (floor not floored) of the current position, with optimally the step being immediately in front of it.

        // this will not be checked.

        String debug;
        double[] playerPosFloored;

        pointAtPos(targetBPWD);

        if (!moveToBlockFlat(targetBPWD, timeoutID, 1)) return false;
        TimeUnit.MILLISECONDS.sleep(150);
        debug = getDebug(getGameScreen());
        playerPosFloored = getPlayerPosFloored(debug);
        while (playerPosFloored[1] != targetBPWD.getY() + 1) {
            if (Timeout.hasExpired(timeoutID)) {
                return false;
            }
            pointAtPos(targetBPWD);
            robot.keyPress(KeyEvent.VK_SPACE);
            TimeUnit.MILLISECONDS.sleep(30);
            robot.keyRelease(KeyEvent.VK_SPACE);
            robot.keyPress(KeyEvent.VK_W);
            TimeUnit.MILLISECONDS.sleep(180);
            robot.keyRelease(KeyEvent.VK_W);
            TimeUnit.MILLISECONDS.sleep(200);
            debug = getDebug(getGameScreen());
            playerPosFloored = getPlayerPosFloored(debug);
        }

        robot.keyPress(KeyEvent.VK_SHIFT);
        moveToBlockFlat(targetBPWD, timeoutID, 0.2);
        robot.keyRelease(KeyEvent.VK_SHIFT);

        TimeUnit.MILLISECONDS.sleep(50);


        return true;
    }

    static ArrayList<BlockPosWithHeight> get3dPath(ArrayList<BlockPosWithHeight> floorPlan, BlockPosWithHeight start, BlockPosWithHeight target) throws HowDidThisHappenException {
        //if(!floorPlan.contains(start) || !floorPlan.contains(target)) throw new HowDidThisHappenException("start and target were not both on the pathfinding floorplan");
        double[] startDoubleArray = start.toDoubleArray();
        double[] targetDoubleArray = target.toDoubleArray();
        boolean startInPlan = false;
        boolean targetInPlan = false;
        for (BlockPosWithHeight i : floorPlan) {
            if (i.equalsDoubleArray(startDoubleArray)) {
                start = i;
                startInPlan = true;
            }
            if (i.equalsDoubleArray(targetDoubleArray)) {
                target = i;
                targetInPlan = true;
            }

        }
        if (!(startInPlan && targetInPlan)) throw new HowDidThisHappenException("start and target were not both on the pathfinding floorplan");
        //if the floorplan is flawed weird things will probably happen


        ArrayList<ArrayList<BlockPosWithHeight>> pathList = new ArrayList<>();
        ArrayList<BlockPosWithHeight> firstPath = new ArrayList<BlockPosWithHeight>();
        firstPath.add(start);
        pathList.add(firstPath);
        System.out.println("before while loop");

        int runs = 5;
        while (true) {/*
            if (runs >= 0) {
                System.out.println("beginning while loop");
                System.out.println(pathList);
                runs--;
            }*/

            boolean didSomething = false;
            for (int i = 0; i < pathList.size(); i++) {
                ArrayList<BlockPosWithHeight> currentPath = pathList.get(i);
                BlockPosWithHeight lastElement = currentPath.get(currentPath.size() - 1);
                if (lastElement.equals(target)) continue;
                //if (i > 4) break; // after 4 paths stop
                double[] lastElementDoubleArray = lastElement.toDoubleArray();
                double[] dir1 = lastElementDoubleArray.clone();
                dir1[0] += 1;
                double[] dir2 = lastElementDoubleArray.clone();
                dir2[0] -= 1;
                double[] dir3 = lastElementDoubleArray.clone();
                dir3[2] += 1;
                double[] dir4 = lastElementDoubleArray.clone();
                dir4[2] -= 1;
                for (BlockPosWithHeight floorPlanElement : floorPlan) {
                    if (currentPath.contains(floorPlanElement)) continue;

                    // there's probably a fancy way to do this but i'm not looking it up
                    if (floorPlanElement.getX() == dir1[0] && floorPlanElement.getZ() == dir1[2]) {
                        if (floorPlanElement.getY() == dir1[1]) {
                            ArrayList<BlockPosWithHeight> newPath = (ArrayList<BlockPosWithHeight>) currentPath.clone();
                            newPath.add(floorPlanElement);
                            pathList.add(newPath);
                            didSomething = true;
                        }
                        if (floorPlanElement.getY() == dir1[1] + 1) {
                            if (lastElement.getHeight() >= 3) {
                                ArrayList<BlockPosWithHeight> newPath = (ArrayList<BlockPosWithHeight>) currentPath.clone();
                                newPath.add(floorPlanElement);
                                pathList.add(newPath);
                                didSomething = true;
                            }
                        }
                        if (floorPlanElement.getY() == dir1[1] - 1) {
                            if (floorPlanElement.getHeight() >= 3) {
                                ArrayList<BlockPosWithHeight> newPath = (ArrayList<BlockPosWithHeight>) currentPath.clone();
                                newPath.add(floorPlanElement);
                                pathList.add(newPath);
                                didSomething = true;
                            }
                        }
                    }
                    if (floorPlanElement.getX() == dir2[0] && floorPlanElement.getZ() == dir2[2]) {
                        if (floorPlanElement.getY() == dir2[1]) {
                            ArrayList<BlockPosWithHeight> newPath = (ArrayList<BlockPosWithHeight>) currentPath.clone();
                            newPath.add(floorPlanElement);
                            pathList.add(newPath);
                            didSomething = true;
                        }
                        if (floorPlanElement.getY() == dir2[1] + 1) {
                            if (lastElement.getHeight() >= 3) {
                                ArrayList<BlockPosWithHeight> newPath = (ArrayList<BlockPosWithHeight>) currentPath.clone();
                                newPath.add(floorPlanElement);
                                pathList.add(newPath);
                                didSomething = true;
                            }
                        }
                        if (floorPlanElement.getY() == dir2[1] - 1) {
                            if (floorPlanElement.getHeight() >= 3) {
                                ArrayList<BlockPosWithHeight> newPath = (ArrayList<BlockPosWithHeight>) currentPath.clone();
                                newPath.add(floorPlanElement);
                                pathList.add(newPath);
                                didSomething = true;
                            }
                        }
                    }
                    if (floorPlanElement.getX() == dir3[0] && floorPlanElement.getZ() == dir3[2]) {
                        if (floorPlanElement.getY() == dir3[1]) {
                            ArrayList<BlockPosWithHeight> newPath = (ArrayList<BlockPosWithHeight>) currentPath.clone();
                            newPath.add(floorPlanElement);
                            pathList.add(newPath);
                            didSomething = true;
                        }
                        if (floorPlanElement.getY() == dir3[1] + 1) {
                            if (lastElement.getHeight() >= 3) {
                                ArrayList<BlockPosWithHeight> newPath = (ArrayList<BlockPosWithHeight>) currentPath.clone();
                                newPath.add(floorPlanElement);
                                pathList.add(newPath);
                                didSomething = true;
                            }
                        }
                        if (floorPlanElement.getY() == dir3[1] - 1) {
                            if (floorPlanElement.getHeight() >= 3) {
                                ArrayList<BlockPosWithHeight> newPath = (ArrayList<BlockPosWithHeight>) currentPath.clone();
                                newPath.add(floorPlanElement);
                                pathList.add(newPath);
                                didSomething = true;
                            }
                        }
                    }
                    if (floorPlanElement.getX() == dir4[0] && floorPlanElement.getZ() == dir4[2]) {
                        if (floorPlanElement.getY() == dir4[1]) {
                            ArrayList<BlockPosWithHeight> newPath = (ArrayList<BlockPosWithHeight>) currentPath.clone();
                            newPath.add(floorPlanElement);
                            pathList.add(newPath);
                            didSomething = true;
                        }
                        if (floorPlanElement.getY() == dir4[1] + 1) {
                            if (lastElement.getHeight() >= 3) {
                                ArrayList<BlockPosWithHeight> newPath = (ArrayList<BlockPosWithHeight>) currentPath.clone();
                                newPath.add(floorPlanElement);
                                pathList.add(newPath);
                                didSomething = true;
                            }
                        }
                        if (floorPlanElement.getY() == dir4[1] - 1) {
                            if (floorPlanElement.getHeight() >= 3) {
                                ArrayList<BlockPosWithHeight> newPath = (ArrayList<BlockPosWithHeight>) currentPath.clone();
                                newPath.add(floorPlanElement);
                                pathList.add(newPath);
                                didSomething = true;
                            }
                        }
                    }
                }
                pathList.remove(i);// removes path that is not leading to target and wasn't continued
                didSomething = true;
                break;


            }

            if (!didSomething) break;
        }

        ArrayList<ArrayList<BlockPosWithHeight>> newPathListOne = new ArrayList<>();

        for (ArrayList<BlockPosWithHeight> currentPath : pathList) {
            if (currentPath.contains(target)) {
                newPathListOne.add(currentPath);
            }
        }


        // now newPathListOne should have only paths that lead to the target

        ArrayList<ArrayList<BlockPosWithHeight>> newPathList = new ArrayList<>();

        for (ArrayList<BlockPosWithHeight> currentPath : newPathListOne) {
            newPathList.add(simplify3dPathDiagonals(currentPath, floorPlan));
        }
        // now only simplified paths that lead to the target.

        ArrayList<ArrayList<BlockPosWithHeight>> shortestPathList = new ArrayList<>();
        int smallestSize = 2147483647;
        for (ArrayList<BlockPosWithHeight> currentPath : newPathList) {
            if (currentPath.size() < smallestSize) {
                smallestSize = currentPath.size();
            }
        }
        for (ArrayList<BlockPosWithHeight> currentPath : newPathList) {
            if (currentPath.size() == smallestSize) {
                shortestPathList.add(currentPath);
            }
        }
        ArrayList<BlockPosWithHeight> currentShortestPath = null;
        double shortestLength = 12000;//idk what to put here i could do a fancy thing and initialise it with the first value but this is easier
        for (ArrayList<BlockPosWithHeight> currentPath : shortestPathList) {
            if (pathLength(currentPath) < shortestLength) {
                shortestLength = pathLength(currentPath);
                currentShortestPath = currentPath;
            }
        }




        return currentShortestPath;
    }
    static ArrayList<BlockPosWithDirection> TranslateDoubleArrayArrayListToBlockPosWithDirectionArrayList(ArrayList<double[]> in) {
        ArrayList<BlockPosWithDirection> out = new ArrayList<>();
        for (double[] element : in) {
            out.add(new BlockPosWithDirection(element, 0));
        }
        return out;
    }

    static double pathLength(ArrayList<BlockPosWithHeight> path) {
        int steps = path.size();
        double length = 0;
        for (int i = 0; i < steps - 1; i++) {
            BlockPosWithHeight start = path.get(i);
            BlockPosWithHeight end = path.get(i + 1);
            double startX = start.getX();
            double startZ = start.getZ();
            double endX = end.getX();
            double endZ = end.getZ();
            Vector2D direction = new Vector2D(new double[] {startX, startZ}, new double[] {endX, endZ});
            length += direction.length();

        }
        return length;
    }
    static ArrayList<BlockPosWithHeight> simplify3dPathDiagonals(ArrayList<BlockPosWithHeight> path, ArrayList<BlockPosWithHeight> floorPlan) {
        // untested but i guess it'll work
        ArrayList<BlockPosWithHeight> newPath = simplify3dPath(path);
        int steps = newPath.size();
        if (steps <= 2) return newPath;
        // player is .6 blocks wide
        double width = 0.6; //the passage width

        while (true) {
            boolean foundSomething = false;
            for (int i = 0; i < steps - 2; i++) {
                if (newPath.get(i).getY() == newPath.get(i + 1).getY() && newPath.get(i).getY() == newPath.get(i + 2).getY()) {
                    BlockPosWithHeight start = newPath.get(i);
                    BlockPosWithHeight end = newPath.get(i + 2);
                    double startX = start.getX();
                    double startZ = start.getZ();
                    double endX = end.getX();
                    double endZ = end.getZ();
                    Vector2D direction = new Vector2D(new double[] {startX, startZ}, new double[] {endX, endZ});
                    Vector2D shiftDirection = new Vector2D(direction.getY(), -direction.getX()).unitVector();
                    Vector2D startPoint = new Vector2D(startX + 0.5, startZ + 0.5);
                    Vector2D endPoint = new Vector2D(endX + 0.5, endZ + 0.5);
                    Vector2D startPointShift1 = startPoint.plus(shiftDirection.times(width/2));
                    Vector2D endPointShift1 = endPoint.plus(shiftDirection.times(width/2));
                    Vector2D startPointShift2 = startPoint.plus(shiftDirection.times(-width/2));
                    Vector2D endPointShift2 = endPoint.plus(shiftDirection.times(-width/2));


                    Line2D.Double firstLine = new Line2D.Double(startPointShift1.getX(), startPointShift1.getY(), endPointShift1.getX(), endPointShift1.getY());
                    Line2D.Double secondLine = new Line2D.Double(startPointShift2.getX(), startPointShift2.getY(), endPointShift2.getX(), endPointShift2.getY());
                    double smallX;
                    double bigX;
                    double smallZ;
                    double bigZ;
                    if (startX > endX) {
                        bigX = startX;
                        smallX = endX;
                    } else {
                        bigX = endX;
                        smallX = startX;
                    }
                    if (startZ > endZ) {
                        bigZ = startZ;
                        smallZ = endZ;
                    } else {
                        bigZ = endZ;
                        smallZ = startZ;
                    }

                    boolean pathExists = true;
                    for (double x = smallX; x < bigX; x++) {
                        for (double z = smallZ; z < bigZ; z++) {
                            if (firstLine.intersects(x, z, 1, 1)) {
                                boolean positionOnFloorPlan = false;
                                for (BlockPosWithHeight pos : floorPlan) {
                                    if (pos.getX() == x && pos.getZ() == z) {
                                        positionOnFloorPlan = true;
                                    }
                                }
                                if (!positionOnFloorPlan) pathExists = false;
                            }

                            if (secondLine.intersects(x, z, 1, 1)) {
                                boolean positionOnFloorPlan = false;
                                for (BlockPosWithHeight pos : floorPlan) {
                                    if (pos.getX() == x && pos.getZ() == z) {
                                        positionOnFloorPlan = true;
                                    }
                                }
                                if (!positionOnFloorPlan) pathExists = false;
                            }
                        }
                    }
                    if (pathExists) {
                        newPath.remove(i+1);
                        foundSomething = true;
                        break;
                    }
                }
            }
            steps = newPath.size();
            if (!foundSomething) break;
        }

        return newPath;
    }

    static ArrayList<BlockPosWithHeight> simplify3dPath(ArrayList<BlockPosWithHeight> path) {
        int steps = path.size();
        if (steps <= 2) return path;

        while (true) {
            boolean foundSomething = false;
            for (int i = 0; i < steps - 2; i++) {
                if (path.get(i).getX() == path.get(i + 1).getX() && path.get(i).getX() == path.get(i + 2).getX() && path.get(i).getY() == path.get(i + 1).getY() && path.get(i).getY() == path.get(i + 2).getY()) {
                    path.remove(i+1);
                    foundSomething = true;
                    break;
                }
                if (path.get(i).getZ() == path.get(i + 1).getZ() && path.get(i).getZ() == path.get(i + 2).getZ() && path.get(i).getY() == path.get(i + 1).getY() && path.get(i).getY() == path.get(i + 2).getY()) {
                    path.remove(i+1);
                    foundSomething = true;
                    break;
                }
            }
            steps = path.size();
            if (!foundSomething) break;
        }
        return path;
    }
    

    static boolean moveToBlockDown(BlockPosWithDirection targetBPWD, int timeoutID) throws InterruptedException, HowDidThisHappenException {
        // targetBPWD/target must be one block down from the floor (floor not floored) of the current position, with optimally the step being immediately in front of it.

        // this will not be checked.
        String debug;
        double[] playerPosFloored;

        pointAtPos(targetBPWD);

        if (!moveToBlockFlat(targetBPWD, timeoutID, 1)) return false;
        TimeUnit.MILLISECONDS.sleep(150);
        debug = getDebug(getGameScreen());
        playerPosFloored = getPlayerPosFloored(debug);
        while (playerPosFloored[1] != targetBPWD.getY() + 1) {
            if (Timeout.hasExpired(timeoutID)) {
                return false;
            }
            pointAtPos(targetBPWD);
            robot.keyPress(KeyEvent.VK_W);
            TimeUnit.MILLISECONDS.sleep(30);
            robot.keyRelease(KeyEvent.VK_W);
            TimeUnit.MILLISECONDS.sleep(150);
            debug = getDebug(getGameScreen());
            playerPosFloored = getPlayerPosFloored(debug);
        }

        robot.keyPress(KeyEvent.VK_SHIFT);
        moveToBlockFlat(targetBPWD, timeoutID, 0.2);
        robot.keyRelease(KeyEvent.VK_SHIFT);

        TimeUnit.MILLISECONDS.sleep(50);

        return true;
    }


    static boolean followFlatPath(ArrayList<double[]> path) throws InterruptedException {
        //well now it works but it looks like a confused chicken
        simplifyFlatPath(path);
        int steps = path.size();
        if (steps == 0) return true;
        // ignores first step
        for (int i = 1; i < steps - 1; i++) {
            if (!moveToBlockFlatCombined(new BlockPosWithDirection(path.get(i), 0), Timeout.newTimeout(5000))) return false;
        }
        if (!moveToBlockFlatCombined(new BlockPosWithDirection(path.get(steps - 1), 0), Timeout.newTimeout(5000))) return false;
        return true;
    }
    static boolean follow3dPath(ArrayList<BlockPosWithHeight> path) throws InterruptedException, HowDidThisHappenException {
        //well now it works but it looks like a confused chicken
        int steps = path.size();
        if (steps == 0) return true;
        // ignores first step
        for (int i = 1; i < steps; i++) {
            if (path.get(i).getY() == path.get(i-1).getY()) {
                if (!moveToBlockFlatCombined(new BlockPosWithDirection(path.get(i), 0), Timeout.newTimeout(10000))) return false;
            } else if (path.get(i).getY() - 1 == path.get(i-1).getY()) {
                if (!moveToBlockUp(new BlockPosWithDirection(path.get(i), 0), Timeout.newTimeout(10000))) return false;
            } else if (path.get(i).getY() == path.get(i-1).getY() - 1) {
                if (!moveToBlockDown(new BlockPosWithDirection(path.get(i), 0), Timeout.newTimeout(10000))) return false;
            }
        }
        return true;
    }
    static boolean moveToBlockFlatCombined(BlockPosWithDirection targetBPWD, int timeoutID) throws InterruptedException {

        moveToBlockFlat(targetBPWD, timeoutID, 0.8);
        robot.keyPress(KeyEvent.VK_SHIFT);
        moveToBlockFlat(targetBPWD, timeoutID, 0.2);
        robot.keyRelease(KeyEvent.VK_SHIFT);

        TimeUnit.MILLISECONDS.sleep(50);
        return true;
    }

    static boolean moveToBlockFlat(BlockPosWithDirection targetBPWD, int timeoutID, double accuracy) throws InterruptedException {



        String debug = getDebug(getGameScreen());

        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        int mouseX = mouseLocation.x;
        int mouseY = mouseLocation.y;
        double[] facing;
        double yawDiff;
        double pitchDiff;

        double targetYaw = getPointAtYaw(targetBPWD, debug);
        double targetPitch = getPointAtPitch(targetBPWD, debug);


        targetYaw = round(targetYaw, 1);
        targetPitch = round(targetPitch, 1);
        robot.keyPress(KeyEvent.VK_W);

        double x = Math.floor(targetBPWD.getX()) + 0.5;
        double z = Math.floor(targetBPWD.getZ()) + 0.5;
        double[] playerPos;
        double diffX;
        double diffZ;
        while (true) {
            debug = getDebug(getGameScreen());
            facing = getFacing(debug);
            targetYaw = getPointAtYaw(targetBPWD, debug);
            targetPitch = getPointAtPitch(targetBPWD, debug);
            yawDiff = targetYaw - facing[0];
            pitchDiff = targetPitch - facing[1];
            if (yawDiff < -180) {
                yawDiff +=360;
            }
            if (yawDiff > 180) {
                yawDiff -=360;
            }

            playerPos = getPlayerPos(debug);
            diffX = playerPos[0] - x;
            diffZ = playerPos[2] - z;
            if (!(Math.abs(diffX) >= accuracy || Math.abs(diffZ) >= accuracy)) break;
            robot.mouseMove((int) (mouseX + Math.round(yawDiff*10)), (int) (mouseY + Math.round(pitchDiff*10)));
            //System.out.println("Moved! x:" + Math.round(yawDiff*10) + " y:" + Math.round(pitchDiff*10));
            try {
                if (Timeout.hasExpired(timeoutID)) {
                    robot.keyRelease(KeyEvent.VK_W);
                    return false;
                }
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        robot.keyRelease(KeyEvent.VK_W);
        return true;
    }
    static boolean moveToBlockFlat(BlockPosWithDirection targetBPWD, int timeoutID) throws InterruptedException {
        return moveToBlockFlat(targetBPWD, timeoutID, 0.2);
    }
    static ArrayList<double[]> simplifyFlatPath(ArrayList<double[]> path) {
        int steps = path.size();
        if (steps <= 2) return path;

        while (true) {
            boolean foundSomething = false;
            for (int i = 0; i < steps - 2; i++) {
                if (path.get(i)[0] == path.get(i + 1)[0] && path.get(i)[0] == path.get(i + 2)[0]) {
                    path.remove(i+1);
                    foundSomething = true;
                    break;
                }
                if (path.get(i)[2] == path.get(i + 1)[2] && path.get(i)[2] == path.get(i + 2)[2]) {
                    path.remove(i+1);
                    foundSomething = true;
                    break;
                }
            }
            steps = path.size();
            if (!foundSomething) break;
        }
        return path;
    }
    static void printPath(ArrayList<double[]> path) {
        int steps = path.size();
        String out = "{";
        for (int i = 0; i < steps; i++) {
            out = out + Arrays.toString(path.get(i));
            if (!(i == steps - 1)) {
                out = out + ", ";
            }
        }
        System.out.println(out + "}");
    }

    static String mineVeinMinerTreeElement(VeinMinerTreeElement currentElement, BlockPosList seenBlocks, boolean levelWithTunnel) throws InterruptedException, DebugTextIncompleteException, HowDidThisHappenException {
        /*mines the given VeinMinerTreeElement. does not flee or move on. generates subelements. changes element to "mined" or "blocked". does not remove duplicates.
        * is called from a position where the VeinMinerTreeElement is in view
        * returns:
        * done: done, success
        * aborted: mining was aborted due to locally fixable problem like water. this vein will continue to be mined.
        * flee: bigger problem like monster. this vein should be abandoned and blocked off.
        * */

        //TODO nearly none of this is tested yet




        String debug = getDebug(getGameScreen());
        BlockPosWithDirection playerPos = new BlockPosWithDirection(debug);
        //debug = getDebug(getGameScreen());//necessary to make directions right fixme calculate this instead of wasting time done (not tested)
        int minedBlockPosDir;
        double xDiff = currentElement.getX() - playerPos.getX();
        double zDiff = currentElement.getZ() - playerPos.getZ();
        if (Math.abs(xDiff) > Math.abs(zDiff)) {
            if (xDiff > 0) {
                minedBlockPosDir = 0;
            } else {
                minedBlockPosDir = 1;
            }
        } else {
            if (zDiff > 0) {
                minedBlockPosDir = 2;
            } else {
                minedBlockPosDir = 3;
            }
        }

        BlockPosWithDirection minedBlockPos = new BlockPosWithDirection(currentElement.getX(), currentElement.getY(), currentElement.getZ(), minedBlockPosDir).up();
        pointAtSide(minedBlockPos.backward(0.5));
        mineBlockWithTool();
        seenBlocks.removeBlockPos(minedBlockPos);
        TimeUnit.MILLISECONDS.sleep(100);



        BooleanHolder oreLeft = new BooleanHolder(false);
        BooleanHolder oreRight = new BooleanHolder(false);
        BooleanHolder oreForward = new BooleanHolder(false);
        BooleanHolder fallingsand = new BooleanHolder(false);
        BlockPosWithDirection examinedBlockPos;

        // upper forward block
        examinedBlockPos = minedBlockPos.forward();
//        System.out.println("calling reactToScan from  mineVeinMinerTreeElement ufb");
        String x = reactToScan(minedBlockPos, examinedBlockPos, seenBlocks, levelWithTunnel, oreForward, fallingsand);
        if (x != null) {
            currentElement.setType("blocked");
            return x;
        }

        
        // upper left block
        examinedBlockPos = minedBlockPos.left();
//        System.out.println("calling reactToScan from  mineVeinMinerTreeElement ulb");
        x = reactToScan(minedBlockPos, examinedBlockPos, seenBlocks, levelWithTunnel, oreLeft, fallingsand);
        if (x != null) {
            currentElement.setType("blocked");
            return x;
        }
        
        // upper right block
        examinedBlockPos = minedBlockPos.right();
//        System.out.println("calling reactToScan from  mineVeinMinerTreeElement urb");
        x = reactToScan(minedBlockPos, examinedBlockPos, seenBlocks, levelWithTunnel, oreRight, fallingsand);
        if (x != null) {
            currentElement.setType("blocked");
            return x;
        }

        // top block

        if (fallingsand.get()) {
            fillSandRoofHole();
        } else {
            examinedBlockPos = minedBlockPos.up();
            x = reactToScanRoof(minedBlockPos, examinedBlockPos, seenBlocks, levelWithTunnel);
            if (x != null) {
                currentElement.setType("blocked");
                return x;
            }
        }


        minedBlockPos = minedBlockPos.down();
        pointAtSide(minedBlockPos.up(0.5));
        mineBlockWithTool();
        seenBlocks.removeBlockPos(minedBlockPos);
        TimeUnit.MILLISECONDS.sleep(100);
        
        // lower forward block
        examinedBlockPos = minedBlockPos.forward();
//        System.out.println("calling reactToScan from  mineVeinMinerTreeElement lfb");
        x = reactToScan(minedBlockPos, examinedBlockPos, seenBlocks, levelWithTunnel, oreForward, fallingsand);
        if (x != null) {
            currentElement.setType("blocked");
            return x;
        }

        // lower left block
        examinedBlockPos = minedBlockPos.left();
//        System.out.println("calling reactToScan from  mineVeinMinerTreeElement llb");
        x = reactToScan(minedBlockPos, examinedBlockPos, seenBlocks, levelWithTunnel, oreLeft, fallingsand);
        if (x != null) {
            currentElement.setType("blocked");
            return x;
        }

        // lower right block
        examinedBlockPos = minedBlockPos.right();
//        System.out.println("calling reactToScan from  mineVeinMinerTreeElement lrb");
        x = reactToScan(minedBlockPos, examinedBlockPos, seenBlocks, levelWithTunnel, oreRight, fallingsand);
        if (x != null) {
            currentElement.setType("blocked");
            return x;
        }

        // bottom block
        examinedBlockPos = minedBlockPos.down();
        x = reactToScanFloor(minedBlockPos, examinedBlockPos, seenBlocks, levelWithTunnel);
        if (x != null) {
            currentElement.setType("blocked");
            return x;
        }


        if (oreForward.get()) {
            new VeinMinerTreeElement(minedBlockPos.forward(), currentElement).checkBlockedDuplicates();
        }
        if (oreRight.get()) {
            new VeinMinerTreeElement(minedBlockPos.right(), currentElement).checkBlockedDuplicates();
        }
        if (oreLeft.get()) {
            new VeinMinerTreeElement(minedBlockPos.left(), currentElement).checkBlockedDuplicates();
        }


        currentElement.setType("mined");
        if (levelWithTunnel && mapGetColor(minedBlockPos.toDoubleArray()) != magenta && mapGetColor(minedBlockPos.toDoubleArray()) != yellow && mapGetColor(minedBlockPos.toDoubleArray()) != pink) mapColorPos(minedBlockPos.toDoubleArray(), Color.BLACK);
        return "done";
    }
    static boolean fillSandRoofHole() throws InterruptedException, DebugTextIncompleteException {
        String debug = getDebug(getGameScreen());
        faceDirection(getDirection(debug));
        //centerPosition();
        robot.keyPress(KeyEvent.VK_W);
        TimeUnit.MILLISECONDS.sleep(100);
        robot.keyRelease(KeyEvent.VK_W);
        faceDirection(getDirection(debug));
        BlockPosWithDirection block3up = new BlockPosWithDirection(debug).forward(2).up(4);
        pointAtPosSneaking(block3up.backward(0.5).down(0.3));
        robot.keyPress(KeyEvent.VK_SHIFT);
        TimeUnit.MILLISECONDS.sleep(150);




        double[] lookingAtBlockPos = getLookingAtBlockCoords(getDebug(getGameScreen()));
        if (block3up.equalsDoubleArray(lookingAtBlockPos)) {
            placeBlock();
            TimeUnit.MILLISECONDS.sleep(20);
            placeBlock();
            TimeUnit.MILLISECONDS.sleep(20);
            placeBlock();
            robot.keyRelease(KeyEvent.VK_SHIFT);
            return true;
        } else if (block3up.backward().equalsDoubleArray(lookingAtBlockPos)) {
            placeBlock();
            TimeUnit.MILLISECONDS.sleep(20);
            placeBlock();
            robot.keyRelease(KeyEvent.VK_SHIFT);
            return true;
        } else if (block3up.backward().down().equalsDoubleArray(lookingAtBlockPos)) {
            placeBlock();
            robot.keyRelease(KeyEvent.VK_SHIFT);
            return true;
        } else if (block3up.backward().down().down().equalsDoubleArray(lookingAtBlockPos)) {
            robot.keyRelease(KeyEvent.VK_SHIFT);
            writeChatDebug("no sandhole to block");
            return true;
        } else {
            pointAtPosSneaking(block3up.backward(0.58).down(0.42).left(0.5));
            lookingAtBlockPos = getLookingAtBlockCoords(getDebug(getGameScreen()));
            if (block3up.left().backward().equalsDoubleArray(lookingAtBlockPos)) {
                placeBlock();
                TimeUnit.MILLISECONDS.sleep(20);
                placeBlock();
                TimeUnit.MILLISECONDS.sleep(20);
                placeBlock();
                robot.keyRelease(KeyEvent.VK_SHIFT);
                return true;
            } else {
                pointAtPosSneaking(block3up.backward(0.58).down(0.42).right(0.5));
                lookingAtBlockPos = getLookingAtBlockCoords(getDebug(getGameScreen()));
                if (block3up.right().backward().equalsDoubleArray(lookingAtBlockPos)) {
                    placeBlock();
                    TimeUnit.MILLISECONDS.sleep(20);
                    placeBlock();
                    TimeUnit.MILLISECONDS.sleep(20);
                    placeBlock();
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    return true;
                } else {
                    block3up = block3up.down();
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    pointAtPos(block3up.backward(0.5).down(0.42));
                    lookingAtBlockPos = getLookingAtBlockCoords(getDebug(getGameScreen()));
                    if (block3up.equalsDoubleArray(lookingAtBlockPos)) {
                        placeBlock();
                        TimeUnit.MILLISECONDS.sleep(20);
                        placeBlock();
                        return true;
                    } else {
                        pointAtPos(block3up.backward(0.58).down(0.42).right(0.5));
                        lookingAtBlockPos = getLookingAtBlockCoords(getDebug(getGameScreen()));
                        if (block3up.right().backward().equalsDoubleArray(lookingAtBlockPos)) {
                            placeBlock();
                            TimeUnit.MILLISECONDS.sleep(20);
                            placeBlock();
                            return true;
                        } else {
                            pointAtPos(block3up.backward(0.58).down(0.42).left(0.5));
                            lookingAtBlockPos = getLookingAtBlockCoords(getDebug(getGameScreen()));
                            if (block3up.left().backward().equalsDoubleArray(lookingAtBlockPos)) {
                                placeBlock();
                                TimeUnit.MILLISECONDS.sleep(20);
                                placeBlock();
                                return true;
                            } else {
                                block3up = block3up.down();
                                pointAtPos(block3up.backward(0.5).down(0.42));
                                lookingAtBlockPos = getLookingAtBlockCoords(getDebug(getGameScreen()));
                                if (block3up.equalsDoubleArray(lookingAtBlockPos)) {
                                    placeBlock();
                                    return true;
                                } else {
                                    pointAtPos(block3up.backward(0.58).down(0.42).right(0.5));
                                    lookingAtBlockPos = getLookingAtBlockCoords(getDebug(getGameScreen()));
                                    if (block3up.right().backward().equalsDoubleArray(lookingAtBlockPos)) {
                                        placeBlock();
                                        return true;
                                    } else {
                                        pointAtPos(block3up.backward(0.58).down(0.42).left(0.5));
                                        lookingAtBlockPos = getLookingAtBlockCoords(getDebug(getGameScreen()));
                                        if (block3up.left().backward().equalsDoubleArray(lookingAtBlockPos)) {
                                            placeBlock();
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private static String reactToScan(BlockPosWithDirection minedBlockPos, BlockPosWithDirection examinedBlockPos, BlockPosList seenBlocks, boolean levelWithTunnel, BooleanHolder ore, BooleanHolder fallingsand) throws InterruptedException, DebugTextIncompleteException, HowDidThisHappenException {
        String result;
        String debug;
        //System.out.println("calling scanBlock from reactToScan");
        result = scanBlock(examinedBlockPos, minedBlockPos, seenBlocks);
        if (result.contains("sand")) fallingsand.set(true);
        if (result.contains("blocked")) return "aborted";
        if (result.contains("flee")) return "flee";
        if (result.contains("ore")) {
            ore.set(true);
            if (levelWithTunnel) mapColorPos(examinedBlockPos.toDoubleArray(), Color.CYAN);
        }
        //tunnel -> do nothing
        if (result.contains("stone") && levelWithTunnel) mapColorPos(examinedBlockPos.toDoubleArray(), Color.GRAY);
        if (result.contains("infested")) {
            if (levelWithTunnel) mapColorPos(examinedBlockPos.toDoubleArray(), Color.ORANGE);
            //TODO once i've done the adding of subelements come back here and make it make a subelement that's blocked.
        }
        if (result.contains("other")) {
            if (levelWithTunnel) mapColorPos(examinedBlockPos.toDoubleArray(), Color.BLUE);
            //TODO once i've done the adding of subelements come back here and make it make a subelement that's blocked.
        }
        if (result.contains("air")) {
            if (levelWithTunnel) {
                if (mapGetColor(examinedBlockPos.toDoubleArray()) == black) {
                    debug = getDebug(getGameScreen());
                    double[] lookingAtBlockPos = getLookingAtBlockCoordsNotNull(debug);
                    seenBlocks.addBlockPos(lookingAtBlockPos);// if it is a mapped tunnel add it to the list
                } else {
                    if (blockFluidBreakin(minedBlockPos)) return "aborted";
                    return "flee";
                }
            } else {
                if (blockFluidBreakin(minedBlockPos)) return "aborted";
                return "flee";
            }
        }
        return null;
    }
    private static String reactToScanRoof(BlockPosWithDirection minedBlockPos, BlockPosWithDirection examinedBlockPos, BlockPosList seenBlocks, boolean levelWithTunnel) throws InterruptedException, DebugTextIncompleteException, HowDidThisHappenException {
        String result;
        String debug;
        result = scanBlock(examinedBlockPos, minedBlockPos, seenBlocks);
        if (result.contains("blocked")) return "aborted";
        if (result.contains("flee")) return "flee";
        if (result.contains("ore")) {
            if (levelWithTunnel) mapColorPos(examinedBlockPos.toDoubleArray(), Color.MAGENTA);
        }
        //tunnel -> do nothing
        if (result.contains("other")) {
            if (levelWithTunnel) mapColorPos(examinedBlockPos.toDoubleArray(), Color.BLUE);
        }
        if (result.contains("air")) {
            // FIXME figure out if blocksandhole is better here than blockfluidbreakin
                if (blockFluidBreakin(minedBlockPos)) return "aborted";
                return "flee";
        }
        return null;
    }
    private static String reactToScanFloor(BlockPosWithDirection minedBlockPos, BlockPosWithDirection examinedBlockPos, BlockPosList seenBlocks, boolean levelWithTunnel) throws InterruptedException, DebugTextIncompleteException, HowDidThisHappenException {
        String result;
        String debug;
        result = scanBlock(examinedBlockPos, minedBlockPos, seenBlocks);
        if (result.contains("blocked")) return "aborted";
        if (result.contains("flee")) return "flee";
        if (result.contains("ore")) {
            if (levelWithTunnel) {
                if (mapGetColor(examinedBlockPos.toDoubleArray()) == magenta) {
                    mapColorPos(examinedBlockPos.toDoubleArray(), Color.YELLOW);
                } else {
                    mapColorPos(examinedBlockPos.toDoubleArray(), Color.PINK);
                }
            }
        }
        //tunnel -> do nothing
        //FIXME return here when i figured out roof ore mining logic to maybe add floorbuilding in case of tunnel
        if (result.contains("other")) {
            if (levelWithTunnel) mapColorPos(examinedBlockPos.toDoubleArray(), Color.BLUE);
        }
        if (result.contains("air")) {
            if (blockFluidBreakin(minedBlockPos)) return "aborted";
            return "flee";
        }
        return null;
    }


    static String scanBlock(BlockPosWithDirection examinedBlockPos, BlockPosWithDirection minedBlockPos, BlockPosList seenBlocks) throws InterruptedException, DebugTextIncompleteException, HowDidThisHappenException {
        /*will look at given block
        * will not block off if blockable events happen (water breakin etc.)
        * will remove fallingsand
        * will not colour map positions
        * will add to seenBlocks
        * returns:
        * any return listed below plus "sand" (example "ore sand") if fallingsand was encountered.
        * blocked: minedBlockPos was be Blocked off. reasons: water, lava
        * air: a block that is not at the examined position and not on the seenBlocks list. should be checked on map.
        * flee: fleeing is advised.
        * tunnel: a block that is not at the examined position but on the seenBlocks list.
        * infested: a infested stone block
        * stone: a block that is on the stone list.
        * ore: a block that is on the ore list.
        * other: a block that is not on one of the lists
        */
        BlockPosWithDirection examinedBlockPointAtPos = new BlockPosWithDirection((examinedBlockPos.getX() + minedBlockPos.getX())/2, (examinedBlockPos.getY() + minedBlockPos.getY())/2, (examinedBlockPos.getZ() + minedBlockPos.getZ())/2, minedBlockPos.getDirection());
        pointAtSide(examinedBlockPointAtPos);
        boolean fallingsand = false;

        TimeUnit.MILLISECONDS.sleep(100);

        String debug = getDebug(getGameScreen());
        String block = getLookingAtBlock(debug);
        double[] lookingAtBlockPos = getLookingAtBlockCoordsNotNull(debug);

        if (debug.contains("Targeted Entity")) {
            return "flee";
        }
        if (!getLookingAtFluid(debug).equals("minecraft:empty")) {
            if (blockFluidBreakin(minedBlockPos)) {
                return "blocked";
            } else return "flee";
        }
        if (!examinedBlockPos.equalsDoubleArray(lookingAtBlockPos)) {
            if (minedBlockPos.equalsDoubleArray(lookingAtBlockPos)) {
                fallingsand = true;
                pointAtSide(minedBlockPos.down(0.5));
                breakFallingStackSlab();
                TimeUnit.SECONDS.sleep(2);
                mineBlockWithTool();
                pointAtSide(examinedBlockPointAtPos);
                debug = getDebug(getGameScreen());
                block = getLookingAtBlock(debug);
                lookingAtBlockPos = getLookingAtBlockCoordsNotNull(debug);
                if (debug.contains("Targeted Entity")) {
                    return "flee";
                }
                if (!getLookingAtFluid(debug).equals("minecraft:empty")) {
                    if (blockFluidBreakin(minedBlockPos)) {
                        return "blocked sand";
                    } return "flee sand";
                }
            } // end falling sand handling
            if (!examinedBlockPos.equalsDoubleArray(lookingAtBlockPos)) {
                writeChatDebug("air 1" + examinedBlockPos.toString());
                if (seenBlocks.contains(lookingAtBlockPos)) {
                    if (fallingsand) return "tunnel sand";
                    return "tunnel";
                } else {
                    if (fallingsand) return "air sand";
                    return "air";
                }
            } else {
                seenBlocks.addBlockPos(lookingAtBlockPos);
            }
        } else {
            seenBlocks.addBlockPos(lookingAtBlockPos);
        }
        if (block.equals("minecraft:infested_stone")) {
            if (fallingsand) return "infested sand";
            return "infested";
        } else if (arrayContainsString(ores, block)) {
            if (fallingsand) return "ore sand";
            return "ore";
        } else if (arrayContainsString(stones, block)) {
            if (fallingsand) return "stone sand";
            return "stone";
        } else {
            writeChat(block + " at position " + Arrays.toString(lookingAtBlockPos) + " this is probably a structure.");
            System.out.println(block + " at position " + Arrays.toString(lookingAtBlockPos) + " this is probably a structure.");
            if (fallingsand) return "other sand";
            return "other";
        }
    }
    static boolean blockFluidBreakin(BlockPosWithDirection blockPos) throws InterruptedException, DebugTextIncompleteException, HowDidThisHappenException {
        pointAtSide(blockPos.down(0.5));
        String debug = getDebug(getGameScreen());
        double[] lookingAtBlockPos = getLookingAtBlockCoordsNotNull(debug);
        if (blockPos.down().equalsDoubleArray(lookingAtBlockPos)) {
            placeBlock();
            return true;
        } else {
            pointAtSide(blockPos.left(0.5));
            debug = getDebug(getGameScreen());
            lookingAtBlockPos = getLookingAtBlockCoordsNotNull(debug);
            if (blockPos.left().equalsDoubleArray(lookingAtBlockPos)) {
                placeBlock();
                return true;
            } else {
                pointAtSide(blockPos.right(0.5));
                debug = getDebug(getGameScreen());
                lookingAtBlockPos = getLookingAtBlockCoordsNotNull(debug);
                if (blockPos.right().equalsDoubleArray(lookingAtBlockPos)) {
                    placeBlock();
                    return true;
                } else {
                    pointAtSide(blockPos.forward(0.5));
                    debug = getDebug(getGameScreen());
                    lookingAtBlockPos = getLookingAtBlockCoordsNotNull(debug);
                    if (blockPos.forward().equalsDoubleArray(lookingAtBlockPos)) {
                        placeBlock();
                        return true;
                    } else return false;

                }

            }

        }

    }
    static boolean arrayContainsString(String[] arrayIn, String in) {
        boolean answer = false;
        for (String i : arrayIn) {
            if (in.equals(i)) {
                answer = true;
                break;
            }
        }
        return answer;
    }
    static void writeChatDebug(String in) throws InterruptedException {
        if (debuggingMode) {
            System.out.println(in);
            writeChat(in);
        }
    }
    static  boolean moveToBlock(double x, double z) throws InterruptedException {
        return moveToBlock(x, z, 3500);
    }
    static boolean moveToBlock(double x, double z, int timeout) throws InterruptedException {
        //will try directly moving to block with a 0.2 block error acceptance. (.3-.7 is fine)
        //timeout in millis
        x = Math.floor(x) + 0.5;
        z = Math.floor(z) + 0.5;

        String debug = getDebug(getGameScreen());
        double[] playerPos = getPlayerPos(debug);
        double diffX = playerPos[0] - x;
        double diffZ = playerPos[2] - z;

        if (!(Math.abs(diffX) >= 0.2 || Math.abs(diffZ) >= 0.2)) return true;
        int keyXPlus;
        int keyXMinus;
        int keyZPlus;
        int keyZMinus;
        if (debug.contains("positive X")) {
            faceDirection(0);
            keyXPlus = KeyEvent.VK_W;
            keyXMinus = KeyEvent.VK_S;
            keyZPlus = KeyEvent.VK_D;
            keyZMinus = KeyEvent.VK_A;
        } else if (debug.contains("negative X")) {
            faceDirection(1);
            keyXPlus = KeyEvent.VK_S;
            keyXMinus = KeyEvent.VK_W;
            keyZPlus = KeyEvent.VK_A;
            keyZMinus = KeyEvent.VK_D;
        } else if (debug.contains("positive Z")) {
            faceDirection(2);
            keyXPlus = KeyEvent.VK_A;
            keyXMinus = KeyEvent.VK_D;
            keyZPlus = KeyEvent.VK_W;
            keyZMinus = KeyEvent.VK_S;
        } else if (debug.contains("negative Z")) {
            faceDirection(3);
            keyXPlus = KeyEvent.VK_D;
            keyXMinus = KeyEvent.VK_A;
            keyZPlus = KeyEvent.VK_S;
            keyZMinus = KeyEvent.VK_W;
        } else return false;

        boolean keyXPlusPressed = false;
        boolean keyXMinusPressed = false;
        boolean keyZPlusPressed = false;
        boolean keyZMinusPressed = false;


        long end = System.currentTimeMillis() + timeout;

        //if (!moveToBlockRough(x, z, timeout)) return false;


        while (Math.abs(diffX) >= 0.5 || Math.abs(diffZ) >= 0.5) {

            if (diffX <= -0.5) {
                robot.keyPress(keyXPlus);
                keyXPlusPressed = true;
            } else if (keyXPlusPressed) {
                robot.keyRelease(keyXPlus);
            }
            if (diffX >= 0.5) {
                robot.keyPress(keyXMinus);
                keyXMinusPressed = true;
            } else if (keyXMinusPressed) {
                robot.keyRelease(keyXMinus);
            }
            if (diffZ <= -0.5) {
                robot.keyPress(keyZPlus);
                keyZPlusPressed = true;
            } else if (keyZPlusPressed) {
                robot.keyRelease(keyZPlus);
            }
            if (diffZ >= 0.5) {
                robot.keyPress(keyZMinus);
                keyZMinusPressed = true;
            } else if (keyZMinusPressed) {
                robot.keyRelease(keyZMinus);
            }

            debug = getDebug(getGameScreen());
            playerPos = getPlayerPos(debug);
            diffX = playerPos[0] - x;
            diffZ = playerPos[2] - z;

            if (System.currentTimeMillis() > end) break;
        }

        if (keyXPlusPressed) {
            robot.keyRelease(keyXPlus);
        }
        if (keyXMinusPressed) {
            robot.keyRelease(keyXMinus);
        }
        if (keyZPlusPressed) {
            robot.keyRelease(keyZPlus);
        }
        if (keyZMinusPressed) {
            robot.keyRelease(keyZMinus);
        }
        robot.keyPress(KeyEvent.VK_SHIFT);

        TimeUnit.MILLISECONDS.sleep(30);

        debug = getDebug(getGameScreen());
        playerPos = getPlayerPos(debug);
        diffX = playerPos[0] - x;
        diffZ = playerPos[2] - z;

        while (Math.abs(diffX) > 0.2 || Math.abs(diffZ) > 0.2) {

            if (diffX < -0.2) {
                robot.keyPress(keyXPlus);
                keyXPlusPressed = true;
            }
            if (diffX > 0.2) {
                robot.keyPress(keyXMinus);
                keyXMinusPressed = true;
            }
            if (diffZ < -0.2) {
                robot.keyPress(keyZPlus);
                keyZPlusPressed = true;
            }
            if (diffZ > 0.2) {
                robot.keyPress(keyZMinus);
                keyZMinusPressed = true;
            }
            TimeUnit.MILLISECONDS.sleep(20);
            if (keyXPlusPressed) {
                robot.keyRelease(keyXPlus);
            }
            if (keyXMinusPressed) {
                robot.keyRelease(keyXMinus);
            }
            if (keyZPlusPressed) {
                robot.keyRelease(keyZPlus);
            }
            if (keyZMinusPressed) {
                robot.keyRelease(keyZMinus);
            }

            TimeUnit.MILLISECONDS.sleep(30);

            debug = getDebug(getGameScreen());
            playerPos = getPlayerPos(debug);
            diffX = playerPos[0] - x;
            diffZ = playerPos[2] - z;

            if (System.currentTimeMillis() > end) break;
        }

        if (keyXPlusPressed) {
            robot.keyRelease(keyXPlus);
        }
        if (keyXMinusPressed) {
            robot.keyRelease(keyXMinus);
        }
        if (keyZPlusPressed) {
            robot.keyRelease(keyZPlus);
        }
        if (keyZMinusPressed) {
            robot.keyRelease(keyZMinus);
        }
        robot.keyRelease(KeyEvent.VK_SHIFT);


        return !(Math.abs(diffX) > 0.2 || Math.abs(diffZ) > 0.2);
    }
    static boolean moveToBlockRough(double x, double z, int timeout) throws InterruptedException {
        //will try directly moving to block with a 0.2 block error acceptance. (.3-.7 is fine)
        //timeout in millis
        x = Math.floor(x) + 0.5;
        z = Math.floor(z) + 0.5;

        String debug = getDebug(getGameScreen());
        double[] playerPos = getPlayerPos(debug);
        double diffX = playerPos[0] - x;
        double diffZ = playerPos[2] - z;
        int keyXPlus;
        int keyXMinus;
        int keyZPlus;
        int keyZMinus;
        if (debug.contains("positive X")) {
            faceDirection(0);
            keyXPlus = KeyEvent.VK_W;
            keyXMinus = KeyEvent.VK_S;
            keyZPlus = KeyEvent.VK_D;
            keyZMinus = KeyEvent.VK_A;
        } else if (debug.contains("negative X")) {
            faceDirection(1);
            keyXPlus = KeyEvent.VK_S;
            keyXMinus = KeyEvent.VK_W;
            keyZPlus = KeyEvent.VK_A;
            keyZMinus = KeyEvent.VK_D;
        } else if (debug.contains("positive Z")) {
            faceDirection(2);
            keyXPlus = KeyEvent.VK_A;
            keyXMinus = KeyEvent.VK_D;
            keyZPlus = KeyEvent.VK_W;
            keyZMinus = KeyEvent.VK_S;
        } else if (debug.contains("negative Z")) {
            faceDirection(3);
            keyXPlus = KeyEvent.VK_D;
            keyXMinus = KeyEvent.VK_A;
            keyZPlus = KeyEvent.VK_S;
            keyZMinus = KeyEvent.VK_W;
        } else return false;

        boolean keyXPlusPressed = false;
        boolean keyXMinusPressed = false;
        boolean keyZPlusPressed = false;
        boolean keyZMinusPressed = false;


        long end = System.currentTimeMillis() + timeout;


        while (Math.abs(diffX) >= 0.2 || Math.abs(diffZ) >= 0.2) {

            if (diffX <= -0.2) {
                robot.keyPress(keyXPlus);
                keyXPlusPressed = true;
            } else if (keyXPlusPressed) {
                robot.keyRelease(keyXPlus);
            }
            if (diffX >= 0.2) {
                robot.keyPress(keyXMinus);
                keyXMinusPressed = true;
            } else if (keyXMinusPressed) {
                robot.keyRelease(keyXMinus);
            }
            if (diffZ <= -0.2) {
                robot.keyPress(keyZPlus);
                keyZPlusPressed = true;
            } else if (keyZPlusPressed) {
                robot.keyRelease(keyZPlus);
            }
            if (diffZ >= 0.2) {
                robot.keyPress(keyZMinus);
                keyZMinusPressed = true;
            } else if (keyZMinusPressed) {
                robot.keyRelease(keyZMinus);
            }

            debug = getDebug(getGameScreen());
            playerPos = getPlayerPos(debug);
            diffX = playerPos[0] - x;
            diffZ = playerPos[2] - z;

            if (System.currentTimeMillis() > end) break;
        }
        if (keyXPlusPressed) {
            robot.keyRelease(keyXPlus);
        }
        if (keyXMinusPressed) {
            robot.keyRelease(keyXMinus);
        }
        if (keyZPlusPressed) {
            robot.keyRelease(keyZPlus);
        }
        if (keyZMinusPressed) {
            robot.keyRelease(keyZMinus);
        }


        return !(Math.abs(diffX) > 0.2 || Math.abs(diffZ) > 0.2);
    }
    static void retreatAndBlock(int dir) throws InterruptedException, HowDidThisHappenException {
        // goes back one block in tunneling direction and blocks tunnel in front of it
        String debug = getDebug(getGameScreen());
        faceDirection(dir);
        BlockPosWithDirection barrierPos = new BlockPosWithDirection(debug);
        robot.keyPress(KeyEvent.VK_S);
        TimeUnit.MILLISECONDS.sleep(600);
        robot.keyRelease(KeyEvent.VK_S);
        pointAtSide(barrierPos.down(0.5));
        placeBlock();
        pointAtSide(barrierPos.up(0.5));
        placeBlock();
    }
    static void placeBlock() throws InterruptedException {
        placeBlock(slotBlocks);
    }
    static int getDirection(String debug) throws DebugTextIncompleteException {
        int direction;
        if (debug.contains("positive X")) {
            direction = 0;
        } else if (debug.contains("negative X")) {
            direction = 1;
        } else if (debug.contains("positive Z")) {
            direction = 2;
        } else if (debug.contains("negative Z")) {
            direction = 3;
        } else throw new DebugTextIncompleteException("debug is missing direction");

        return direction;
    }
    static void placeBlock(char itemSlot) throws InterruptedException {
        BufferedImage screen = getGameScreen();
        char slot = getCurrentSlot(screen);
        pressButton(itemSlot);
        rightClick();
        pressButton(slot);

    }
    static void placeBlock(BlockPosWithDirection dir, char itemSlot) throws InterruptedException {
        pointAtPos(dir);
        placeBlock(itemSlot);
    }
    static void placeBlock(BlockPosWithDirection dir) throws InterruptedException {
        pointAtPos(dir);
        placeBlock();
    }
    static void faceDirection(int direction) throws InterruptedException {
        //  0: positive X 1: negative X 2: positive Z 3: negative Z
        double[] facing = getFacingFromRobot();
        double targetYaw = facing[0];
        double targetPitch = facing[1];
        if (direction == 0) {
            targetYaw = -90;
        } else if (direction == 1) {
            targetYaw = 90;
        } else if (direction == 2) {
            targetYaw = 0;
        } else if (direction == 3) {
            targetYaw = 180;
        }

        pointAt(targetYaw, targetPitch);

    }
    static void pointAtPosSneaking(BlockPosWithDirection targetPos) throws InterruptedException {
        pointAtPos(targetPos.up(0.35));
    }
    static void pointAtPos(BlockPosWithDirection targetPos) throws InterruptedException {
        String debug = getDebug(getGameScreen());
        double eyeHeight = 1.62;
        double[] playerPos = getPlayerPos(debug);
        double xDiff = (playerPos[0] - (targetPos.getX()+ 0.5));
        double yDiff = ((playerPos[1] + eyeHeight) - (targetPos.getY()+ 0.5));
        double zDiff = (playerPos[2] - (targetPos.getZ()+ 0.5));
        double[] facing = getFacing(debug);
        double targetYaw = facing[0];
        double targetPitch = facing[1];
        if (xDiff == 0) {
            if (zDiff < 0) {
                targetYaw = 0;
            } else if (zDiff > 0) {
                targetYaw = 180;
            }
        } else if (xDiff > 0) {
            targetYaw = Math.toDegrees(Math.atan(zDiff / xDiff)) + 90;
        } else if (xDiff < 0) {
            targetYaw = Math.toDegrees(Math.atan(zDiff / xDiff)) - 90;
        }

        double distance = distance3D(playerPos, targetPos.down(eyeHeight).addX(0.5).addY(0.5).addZ(0.5).toDoubleArray());
//        writeChat("yDiff=" + yDiff + " distance ="+distance);
        if (distance != 0) targetPitch = Math.toDegrees(Math.asin(yDiff/distance));

        pointAt(targetYaw, targetPitch);

    }

    static double getPointAtYaw(BlockPosWithDirection targetPos, String debug) {
        double eyeHeight = 1.62;
        double[] playerPos = getPlayerPos(debug);
        double xDiff = (playerPos[0] - (targetPos.getX()+ 0.5));
        double zDiff = (playerPos[2] - (targetPos.getZ()+ 0.5));
        double[] facing = getFacing(debug);
        double targetYaw = facing[0];
        if (xDiff == 0) {
            if (zDiff < 0) {
                targetYaw = 0;
            } else if (zDiff > 0) {
                targetYaw = 180;
            }
        } else if (xDiff > 0) {
            targetYaw = Math.toDegrees(Math.atan(zDiff / xDiff)) + 90;
        } else if (xDiff < 0) {
            targetYaw = Math.toDegrees(Math.atan(zDiff / xDiff)) - 90;
        }

        return targetYaw;
    }
    static double getPointAtPitch(BlockPosWithDirection targetPos, String debug) {
        double eyeHeight = 1.62;
        double[] playerPos = getPlayerPos(debug);
        double yDiff = ((playerPos[1] + eyeHeight) - (targetPos.getY()+ 0.5));
        double[] facing = getFacing(debug);
        double targetPitch = facing[1];

        double distance = distance3D(playerPos, targetPos.down(eyeHeight).addX(0.5).addY(0.5).addZ(0.5).toDoubleArray());
        if (distance != 0) targetPitch = Math.toDegrees(Math.asin(yDiff/distance));
        return targetPitch;
    }
    static void pointAtSide(BlockPosWithDirection targetPos) throws InterruptedException, HowDidThisHappenException {
        //only works with BlockPosWithDirections that are 0.5 off from full blocks.
        //will point at the blockside whichs center the BlockPosWithDirection is marking.
        String debug = getDebug(getGameScreen());
        double eyeHeight = 1.62;
        double[] playerPos = getPlayerPos(debug);
        double xDiff = (playerPos[0] - (targetPos.getX()+ 0.5));
        double yDiff = ((playerPos[1] + eyeHeight) - (targetPos.getY()+ 0.5));
        double zDiff = (playerPos[2] - (targetPos.getZ()+ 0.5));
        double[] facing = getFacing(debug);
        double targetYaw = facing[0];
        double targetPitch = facing[1];
        if (xDiff == 0) {
            if (zDiff < 0) {
                targetYaw = 0;
            } else if (zDiff > 0) {
                targetYaw = 180;
            }
        } else if (xDiff > 0) {
            targetYaw = Math.toDegrees(Math.atan(zDiff / xDiff)) + 90;
        } else if (xDiff < 0) {
            targetYaw = Math.toDegrees(Math.atan(zDiff / xDiff)) - 90;
        }

        double distance = distance3D(playerPos, targetPos.down(eyeHeight).addX(0.5).addY(0.5).addZ(0.5).toDoubleArray());
//        writeChat("yDiff=" + yDiff + " distance ="+distance);
        if (distance != 0) targetPitch = Math.toDegrees(Math.asin(yDiff/distance));

        //

        //pointAt modified version

        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        int mouseX = mouseLocation.x;
        int mouseY = mouseLocation.y;
//        double[] facing;
        double yawDiff;
        double pitchDiff;
        targetYaw = round(targetYaw, 1);
        targetPitch = round(targetPitch, 1);


        //setup for break logic
        boolean facingSide = false;
        int plane;// 0 same x , 1 same y, 2 same z
        double[] smallCorner;
        double[] bigCorner;
        Vector3D planeNormal;
        if (Math.abs(Math.floor(targetPos.getX()) - targetPos.getX()) > 0.4 && Math.abs(Math.ceil(targetPos.getX()) - targetPos.getX()) > 0.4) {
            plane = 0;
            planeNormal = new Vector3D(1, 0, 0);
        } else if (Math.abs(Math.floor(targetPos.getY()) - targetPos.getY()) > 0.4 && Math.abs(Math.ceil(targetPos.getY()) - targetPos.getY()) > 0.4){
            plane = 1;
            planeNormal = new Vector3D(0, 1, 0);
        } else if (Math.abs(Math.floor(targetPos.getZ()) - targetPos.getZ()) > 0.4 && Math.abs(Math.ceil(targetPos.getZ()) - targetPos.getZ()) > 0.4) {
            plane = 2;
            planeNormal = new Vector3D(0, 0, 1);
        } else throw new HowDidThisHappenException("a blockposwithdirection that was not on a block border was passed");
        Vector3D planePoint = new Vector3D(targetPos.toDoubleArray()).plus(new Vector3D(0.5, 0.5, 0.5));

        // a block reaches from its coordinates to its coordinates + 1
        Vector3D eyePos = new Vector3D(playerPos[0], playerPos[1] + eyeHeight, playerPos[2]);
        while (true) {
            debug = getDebug(getGameScreen());
            facing = getFacing(debug);
            yawDiff = targetYaw - facing[0];
            pitchDiff = targetPitch - facing[1];
            if (yawDiff < -180) {
                yawDiff +=360;
            }
            if (yawDiff > 180) {
                yawDiff -=360;
            }
            // break logic start
            double rayVectorY = (-1) * Math.sin(Math.toRadians(facing[1]));
            double helper1 = Math.cos(Math.toRadians(facing[1]));
            double rayVectorX = (-1) * Math.sin(Math.toRadians(facing[0])) * helper1;
            double rayVectorZ = Math.cos(Math.toRadians(facing[0])) * helper1;
            Vector3D rayVector = new Vector3D(rayVectorX, rayVectorY, rayVectorZ);

            Vector3D intersectVector = Vector3D.intersectPointRay(rayVector, eyePos, planeNormal, planePoint);
            //System.out.println(intersectVector);

            if (intersectVector != null) {
                double[] intersect = intersectVector.toDoubleArray();
                double[] targetPosDA = targetPos.toDoubleArray();
                facingSide = true;
                for (int i = 0; i < 3; i++) {
                    if (i != plane) {
                        if (intersect[i] <= targetPosDA[i] || intersect[i] >= targetPosDA[i] + 1) {
                            facingSide = false;
                        }
                    }
                }
            }
            if (facingSide) break;


            if (yawDiff == 0 && pitchDiff == 0) break;
            // break logic end
            robot.mouseMove((int) (mouseX + Math.round(yawDiff*10)), (int) (mouseY + Math.round(pitchDiff*10)));
            //System.out.println("Moved! x:" + Math.round(yawDiff*10) + " y:" + Math.round(pitchDiff*10));
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    static void initMap() throws IOException, InterruptedException {
        File mapFile = new File("map.png");
        if (mapFile.exists()) {
            map = ImageIO.read(mapFile);
            Scanner scanner = new Scanner(new File("data.txt"));
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                System.out.println(data);
                if (data.contains("mapOffsetX")) {
                    mapOffsetX = Integer.parseInt(data.substring(data.indexOf(':') + 1));
                    System.out.println(mapOffsetX);
                }
                if (data.contains("mapOffsetZ")) {
                    mapOffsetZ = Integer.parseInt(data.substring(data.indexOf(':') + 1));
                    System.out.println(Main.mapOffsetZ);
                }
            }
            scanner.close();
        } else {
            String debug = getDebug(getGameScreen());
            double[] pos = getPlayerPosFloored(debug);
            int playerX = (int) pos[0];
            int playerZ = (int) pos[2];
            map = new BufferedImage(21, 21, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = map.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, map.getWidth(), map.getHeight());

            mapOffsetX = playerX - 10;
            mapOffsetZ = playerZ - 10;

            FileWriter myWriter = new FileWriter("data.txt");
            myWriter.write("mapOffsetX:" + mapOffsetX + "\n");
            myWriter.write("mapOffsetZ:" + mapOffsetZ + "\n");
            myWriter.close();
            mapColorPos(getPlayerPosFloored(debug), Color.GREEN);
        }
    }
    static void endProgram(){
        System.out.println("\nending program");
        System.out.println("saving map");
        try {
            ImageIO.write(map, "png", new File("map.png"));
            FileWriter myWriter = new FileWriter("data.txt");
            myWriter.write("mapOffsetX:" + mapOffsetX + "\n");
            myWriter.write("mapOffsetZ:" + mapOffsetZ + "\n");
            myWriter.close();
        } catch (IOException e) {
            System.err.println("error while saving");
        }
        System.exit(0);
    }
    static void placeTorch() throws InterruptedException {
        BufferedImage screen = getGameScreen();
        double[] facing = getFacing(getDebug(screen));
        char slot = getCurrentSlot(screen);
        pointAt(180 + yawDiff, 90);
        pressButton(slotTorches);
        rightClick();
        pressButton(slot);
        pointAt(facing[0], facing[1]);
    }
    static void logOff() throws InterruptedException {
        robot.keyPress(KeyEvent.VK_ESCAPE);
        robot.keyRelease(KeyEvent.VK_ESCAPE);
        TimeUnit.MILLISECONDS.sleep(50);
        robot.mouseMove(960, 624);
        leftClick();

    }
    static int[] getSlotAmounts() throws InterruptedException {
        // 0 is shield then hotbar then inventory from top to bottom

        int[] slotNumbers = new int[37];
        pressButton('e');
        TimeUnit.MILLISECONDS.sleep(50);
        forceMouseMoveConfirmed(1500, 500);
        TimeUnit.MILLISECONDS.sleep(100);
        BufferedImage img = getGameScreenOld();
        String number;
        int index = 0;
        number = getOCR(939, 471, 39, 27, img).trim();
        if (number.equals("")) {
            slotNumbers[index] = 1;
        } else {
            slotNumbers[index] = Integer.parseInt(number);
        }
        index++;
        for (int i = 0; i < 9; i++) {
            number = getOCR(732 + 54 * i, 711, 39, 27, img).trim();
            if (number.equals("")) {
                slotNumbers[index] = 1;
            } else {
                slotNumbers[index] = Integer.parseInt(number);
            }
            index++;
        }
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 9; i++) {
                number = getOCR(732 + 54 * i, 537 + 54 * j, 39, 27, img).trim();
                if (number.equals("")) {
                    slotNumbers[index] = 1;
                } else {
                    slotNumbers[index] = Integer.parseInt(number);
                }
                index++;
            }

        }
        pressButton('e');
        TimeUnit.MILLISECONDS.sleep(50);


        return slotNumbers;
    }
    static String[] getSlotContent() throws InterruptedException {
        String[] slotNames = new String[37];
        int index = 0;
        pressButton('e');
        TimeUnit.MILLISECONDS.sleep(50);
        forceMouseMoveConfirmed(939, 471);
        TimeUnit.MILLISECONDS.sleep(100);
        slotNames[index] = readTooltip(getGameScreenOld()).trim();
        index++;

        for (int i = 0; i < 9; i++) {
            forceMouseMoveConfirmed(732 + 54 * i, 711);
            TimeUnit.MILLISECONDS.sleep(100);
            slotNames[index] = readTooltip(getGameScreenOld()).trim();
            index++;
        }
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 9; i++) {
                forceMouseMoveConfirmed(732 + 54 * i, 537 + 54 * j);
                TimeUnit.MILLISECONDS.sleep(100);
                slotNames[index] = readTooltip(getGameScreenOld()).trim();
                index++;
            }

        }
        for (int i = 0; i < slotNames.length; i++) {
            if (slotNames[i].equals("")) {
                slotNames[i] = "empty";
            }
        }
        pressButton('e');
        TimeUnit.MILLISECONDS.sleep(50);

        return slotNames;
    }
    static void forceMouseMoveConfirmed(int x, int y) throws InterruptedException {
        //use with care
        //this function has no timeout so it wait thats bad i'm making a timeout
        // this function has a timeout of 1 second
        int[] mousePos;
        long end = System.currentTimeMillis() + 1000;
        while (true) {
            robot.mouseMove(x, y);
            TimeUnit.MILLISECONDS.sleep(20);
            mousePos = getMousePos();
            if (mousePos[0] == x && mousePos[1] == y) break;
            if (System.currentTimeMillis() > end) break;
        }

    }
    static int[] getMousePos() {
        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        int x = (int) b.getX();
        int y = (int) b.getY();
        return new int[] {x, y};
    }
    static String getOCR(int textboxX, int textboxY, int textboxWidth, int textboxHeight, BufferedImage img, Color[] c) {
        //line has to start in top row of box idc enough to fix it
        Color color;
        BufferedImage result = new BufferedImage(textboxWidth / fontPixelSize, (textboxHeight / fontPixelSize) + 9, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < result.getWidth(); x++) {
            for (int y = result.getHeight() - 9; y < result.getHeight(); y++) {
                result.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
        for (int x = textboxX; x < textboxX + textboxWidth; x++) {
            for (int y = textboxY; y < textboxY + textboxHeight; y++) {
                if ((x - 2) % fontPixelSize == 0 && (y - 1) % fontPixelSize == 0) {
                    color = new Color(img.getRGB(x, y));
                    result.setRGB((x - 2 - textboxX) / fontPixelSize, (y - 1 - textboxY) / fontPixelSize, Color.WHITE.getRGB());
                    for (Color textColor: c) {
                        if (color.getRGB() == textColor.getRGB()) {
                            result.setRGB((x - 2 - textboxX) / fontPixelSize, (y - 1 - textboxY) / fontPixelSize, Color.BLACK.getRGB());
                        }
                    }
                }
            }
        }
        return FontTranslator.translate(result);
    }
    static String getOCR(int x, int y, int width, int height, BufferedImage img) {
        return getOCR(x, y, width, height, img, new Color[] {new Color(252, 252, 252)});
    }

    static int getHealth(BufferedImage in) {
        int x0 = 694;
        int color;
        int health = 0;
        for (int i = 0; i < 20; i++) {
            color = in.getRGB(x0 + i * 12, 916);
            if (color != -14145496) {
                health += 1;
            }
        }
        return health;
    }
    static char getCurrentSlot(BufferedImage in) {
        Color color = new Color(in.getRGB(720, 949));
        if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) return '1';
        color = new Color(in.getRGB(780, 949));
        if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) return '2';
        color = new Color(in.getRGB(840, 949));
        if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) return '3';
        color = new Color(in.getRGB(900, 949));
        if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) return '4';
        color = new Color(in.getRGB(960, 949));
        if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) return '5';
        color = new Color(in.getRGB(1020, 949));
        if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) return '6';
        color = new Color(in.getRGB(1080, 949));
        if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) return '7';
        color = new Color(in.getRGB(1140, 949));
        if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) return '8';
        color = new Color(in.getRGB(1200, 949));
        if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) return '9';

        return '0';
    }
    static void breakFallingStack() throws InterruptedException {
        BufferedImage screen = getGameScreen();
        char slot = getCurrentSlot(screen);
        pressButton(slotShovel);
        mineBlock();
        pressButton(slotTorches);
        rightClick();

        pressButton(slot);
    }
    static void breakFallingStackSlab() throws InterruptedException {
        BufferedImage screen = getGameScreen();
        char slot = getCurrentSlot(screen);
        pressButton(slotShovel);
        mineBlock();
        pressButton(slotSlabs);
        rightClick();

        pressButton(slot);
    }
    static String readTooltip(BufferedImage img) {
        int color;
        Color color2;
        boolean found = false;
        int textboxX = 0;
        int textboxY = 0;
        int textboxWidth = 0;
        int textboxHeight = 0;
        for (int y = 264; y <= 749; y++) {
            for (int x = 702; x <= 1217; x++) {
                color2 = new Color(img.getRGB(x, y));
                if (color2.getRed() <= 30 && color2.getGreen() <= 15 && color2.getBlue() <= 30 ) {
                    //img.setRGB(x, y, Color.ORANGE.getRGB());
                    boolean isTextbox = true;
                    for (int i = 0; i < 150; i++) {
                        for (int j = 0; j < 6; j++) {
                            color2 = new Color(img.getRGB(x + i, y + j));
                            if (color2.getRed() > 30 || color2.getGreen() > 15 || color2.getBlue() > 30) {
                                isTextbox = false;
                            }
                        }
                    }
                    if (isTextbox) {
                        for (int i = 0; i < img.getWidth() - x; i++) {
                            color2 = new Color(img.getRGB(x + i, y));
                            if (color2.getRed() <= 30 && color2.getGreen() <= 15 && color2.getBlue() <= 30 ) {
                                textboxWidth += 1;
                                //img.setRGB(x + i, y, Color.ORANGE.getRGB());
                            } else break;
                        }
                        textboxHeight += 1;
                        for (int j = 1; j < img.getHeight() - y; j++) {
                            color2 = new Color(img.getRGB(x, y + j));
                            if (color2.getRed() <= 30 && color2.getGreen() <= 15 && color2.getBlue() <= 30 ) {
                                textboxHeight += 1;
                                //img.setRGB(x, y + j, Color.ORANGE.getRGB());
                            } else break;
                        }
                        textboxX = x;
                        textboxY = y;

                        found = true;
                    }
                }
                if (found) break;
                //img.setRGB(x, y, Color.ORANGE.getRGB());
            }
            if (found) break;
        }
        if (!found) return "";
        BufferedImage result = new BufferedImage(textboxWidth / fontPixelSize, (textboxHeight / fontPixelSize), BufferedImage.TYPE_INT_RGB);



        for (int x = textboxX; x < textboxX + textboxWidth; x++) {
            for (int y = textboxY; y < textboxY + textboxHeight; y++) {
                if ((x - 2) % fontPixelSize == 0 && (y - 1) % fontPixelSize == 0) {
                    color = img.getRGB(x, y);
                    if (color == new Color(252, 252, 252).getRGB()) {
                        result.setRGB((x - 2 - textboxX) / fontPixelSize, (y - 1 - textboxY) / fontPixelSize, Color.BLACK.getRGB());
                    } else if (color == new Color(84, 84, 84).getRGB()) {
                        result.setRGB((x - 2 - textboxX) / fontPixelSize, (y - 1 - textboxY) / fontPixelSize, Color.BLACK.getRGB());
                    } else if (color == new Color(168, 168, 168).getRGB()) {
                        result.setRGB((x - 2 - textboxX) / fontPixelSize, (y - 1 - textboxY) / fontPixelSize, Color.BLACK.getRGB());
                    } else if (color == new Color(84, 252, 252).getRGB()) {
                        result.setRGB((x - 2 - textboxX) / fontPixelSize, (y - 1 - textboxY) / fontPixelSize, Color.BLACK.getRGB());
                    } else if (color == new Color(0, 168, 0).getRGB()) {
                        result.setRGB((x - 2 - textboxX) / fontPixelSize, (y - 1 - textboxY) / fontPixelSize, Color.BLACK.getRGB());
                    } else if (color == new Color(252, 252, 84).getRGB()) {
                        result.setRGB((x - 2 - textboxX) / fontPixelSize, (y - 1 - textboxY) / fontPixelSize, Color.BLACK.getRGB());
                    } else if (color == new Color(252, 84, 84).getRGB()) {
                        result.setRGB((x - 2 - textboxX) / fontPixelSize, (y - 1 - textboxY) / fontPixelSize, Color.BLACK.getRGB());
                    } else if (color == new Color(168, 0, 168).getRGB()) {
                        result.setRGB((x - 2 - textboxX) / fontPixelSize, (y - 1 - textboxY) / fontPixelSize, Color.BLACK.getRGB());
                    } else if (color == new Color(84, 84, 252).getRGB()) {
                        result.setRGB((x - 2 - textboxX) / fontPixelSize, (y - 1 - textboxY) / fontPixelSize, Color.BLACK.getRGB());
                    } else {
                        result.setRGB((x - 2 - textboxX) / fontPixelSize, (y - 1 - textboxY) / fontPixelSize, Color.WHITE.getRGB());

                    }
                }
            }
        }
        String resultString = FontTranslator.translateVariableSpacing(result).trim();


        return resultString;
    }
    static String getDebug(BufferedImage img) {

        Color color;
        BufferedImage resultDebug = new BufferedImage(img.getWidth() / fontPixelSize, img.getHeight() / fontPixelSize + 1, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                if ((x - 2) % fontPixelSize == 0 && (y - 1) % fontPixelSize == 0) {
                    color = new Color(img.getRGB(x, y));
                    if (color.getRed() == 221 && color.getGreen() == 221 && color.getBlue() == 221) {
                        resultDebug.setRGB((x - 2) / fontPixelSize, (y - 1) / fontPixelSize, Color.BLACK.getRGB());
                    } else {
                        resultDebug.setRGB((x - 2) / fontPixelSize, (y - 1) / fontPixelSize, Color.WHITE.getRGB());

                    }
                }
            }
        }
        return FontTranslator.translate(resultDebug);
    }

    static void mapColorPos(double[] pos, Color color) {
        int mapX = (int) Math.floor(pos[0]) - mapOffsetX;
        int mapZ = (int) Math.floor(pos[2]) - mapOffsetZ;
        //System.out.println("x: "+ mapX+" z: "+mapZ + " game: x: " + (int) Math.floor(pos[0]) + " z: " + (int) Math.floor(pos[2]));
        if (mapX < 0) {
            BufferedImage newMap = new BufferedImage(map.getWidth() + 10, map.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = newMap.createGraphics();
            g2d.drawImage(map, 10, 0, null);
            g2d.fillRect(0, 0, 10, newMap.getHeight());
            mapOffsetX += -10;
            map = newMap;
            try { writeChat("map resized"); } catch (InterruptedException e) { e.printStackTrace(); }
        } else if (mapX >= map.getWidth()) {
            BufferedImage newMap = new BufferedImage(map.getWidth() + 10, map.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = newMap.createGraphics();
            g2d.drawImage(map, 0, 0, null);
            g2d.fillRect(map.getWidth(), 0, 10, newMap.getHeight());
            map = newMap;
            try { writeChat("map resized"); } catch (InterruptedException e) { e.printStackTrace(); }
        } else if (mapZ < 0) {
            BufferedImage newMap = new BufferedImage(map.getWidth(), map.getHeight() + 10, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = newMap.createGraphics();
            g2d.drawImage(map, 0, 10, null);
            g2d.fillRect(0, 0, newMap.getWidth(), 10);
            mapOffsetZ += -10;
            map = newMap;
            try { writeChat("map resized"); } catch (InterruptedException e) { e.printStackTrace(); }
        } else if (mapZ >= map.getWidth()) {
            BufferedImage newMap = new BufferedImage(map.getWidth(), map.getHeight() + 10, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = newMap.createGraphics();
            g2d.drawImage(map, 0, 0, null);
            g2d.fillRect(0, map.getHeight(), newMap.getWidth(), 10);
            map = newMap;
            try { writeChat("map resized"); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        mapX = (int) Math.floor(pos[0]) - mapOffsetX;
        mapZ = (int) Math.floor(pos[2]) - mapOffsetZ;
        //System.out.println("x: "+ mapX+" z: "+mapZ + " game: x: " + (int) Math.floor(pos[0]) + " z: " + (int) Math.floor(pos[2]));
        try {
            map.setRGB(mapX, mapZ, color.getRGB());

        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("location outside of mapped area");
            System.err.println("how did i fuck this up?");
            System.err.println("this should never happen!");
        }
    }
    static int mapGetColor(double[] pos) {
        int mapX = (int) Math.floor(pos[0]) - mapOffsetX;
        int mapZ = (int) Math.floor(pos[2]) - mapOffsetZ;
        int rgb;
        try {
            rgb = map.getRGB(mapX, mapZ);
            return rgb;

        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("location outside of mapped area");
            return positionOutOfMap;
        }

    }
    static String getChat(BufferedImage img) {

        Color color;
        BufferedImage resultChat = new BufferedImage(img.getWidth() / fontPixelSize, img.getHeight() / fontPixelSize + 1, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                if ((x - 2) % fontPixelSize == 0 && (y - 1) % fontPixelSize == 0) {
                    color = new Color(img.getRGB(x, y));
                    if (color.getRed() == 251 && color.getGreen() == 251 && color.getBlue() == 251) {
                        resultChat.setRGB((x - 2) / fontPixelSize, (y - 1) / fontPixelSize, Color.BLACK.getRGB());
                    } else {
                        resultChat.setRGB((x - 2) / fontPixelSize, (y - 1) / fontPixelSize, Color.WHITE.getRGB());

                    }
                }
            }
        }
        return FontTranslator.translate(resultChat);
    }
    static BufferedImage getGameScreen() throws InterruptedException {
        BufferedImage img;
        boolean isNotMinecraft = true;
        boolean firstRun = true;
        boolean statistics = false;
        while (isNotMinecraft) {
            img = robot.createScreenCapture(new Rectangle(0, 23, 1920, 1040 - 23));
            isNotMinecraft = false;

            for (int x = 6; x < 9; x++) {
                for (int y = 6; y < 27; y++) {
                    if (img.getRGB(x, y) != rgb221) {
                        isNotMinecraft = true;
                    }
                }
            }
            if (!isNotMinecraft) {
                Timeout.resumeTimeouts();
                return img;
            } else {
                if (firstRun) {
                    Timeout.pauseTimeouts();
                    System.out.print("Minecraft not running");
                    firstRun = false;
                } else {
                    System.out.print(".");
                    statistics = true;
                    for (int x = 912; x < 915; x++) {
                        for (int y = 60; y < 78; y++) {
                            if (img.getRGB(x, y) != rgb252) {
                                statistics = false;
                            }
                        }
                    }
                    if (statistics) {
                        System.out.println("statistics screen");
                        endProgram();
                    }
                }
                TimeUnit.MILLISECONDS.sleep(100);
            }
        }
        Timeout.resumeTimeouts();
        return robot.createScreenCapture(new Rectangle(0, 23, 1920, 1040 - 23));
    }
    static BufferedImage getGameScreenOld() {
        BufferedImage img;
        if (secondScreen) {
            img = robot.createScreenCapture(new Rectangle(1920, 25 - 137, 1920, 1040 - 25));
        } else {
            img = robot.createScreenCapture(new Rectangle(0, 23, 1920, 1040 - 23));
        }
        return img;
    }
    static double[] getLookingAtBlockCoords(String in) {
        String coords = returnRegExMatch(in, "(?<=Targeted Block: ).+(?=\\n)");
        if (coords != null){
            double x = Integer.parseInt(coords.substring(0, coords.indexOf(",")));
            double y = Integer.parseInt(coords.substring(coords.indexOf(",") + 2, coords.lastIndexOf(",")));
            double z = Integer.parseInt(coords.substring(coords.lastIndexOf(",") + 2));
            return new double[]{x, y ,z};
        } else return null;
    }
    static double[] getLookingAtBlockCoordsNotNull(String in) throws DebugTextIncompleteException {
        double[] out = getLookingAtBlockCoords(in);
        if (out == null) {
            throw new DebugTextIncompleteException("getLookingAtBlockCoordsNotNull tried to return null");
        } else return out;
    }
    static double[] getLookingAtBlockCoordsFromRobot() throws InterruptedException {
        return getLookingAtBlockCoords(getDebug(getGameScreen()));
    }
    static double[] getPlayerPos(String in) {
        String pos = returnRegExMatch(in, "(?<=XYZ: ).+?(?= {2,})");
        double x = Double.parseDouble(pos.substring(0, pos.indexOf(" ")));
        double y = Double.parseDouble(pos.substring(pos.indexOf(" ") + 3, pos.lastIndexOf(" ") - 2));
        double z = Double.parseDouble(pos.substring(pos.lastIndexOf(" ")));
        return new double[]{x, y, z};
    }
    static double[] getPlayerPosFloored(String in) {
        String pos = returnRegExMatch(in, "(?<=XYZ: ).+?(?= {2,})");
        double x = Math.floor(Double.parseDouble(pos.substring(0, pos.indexOf(" "))));
        double y = Math.floor(Double.parseDouble(pos.substring(pos.indexOf(" ") + 3, pos.lastIndexOf(" ") - 2)));
        double z = Math.floor(Double.parseDouble(pos.substring(pos.lastIndexOf(" "))));
        return new double[]{x, y, z};
    }
    static String getLookingAtBlock(String in) {
        if (!in.contains("Targeted Block")) {
            return "minecraft:air";
        } else {
            return returnRegExMatch(in, "minecraft[^ ]+(?=\\n)");
        }
    }
    static String getLookingAtFluid(String in) {
        if (!in.contains("Targeted Fluid")) {
            return "minecraft:empty";
        } else {
            String[] splitted = in.split("Targeted Fluid");
            return returnRegExMatch(splitted[1], "minecraft[^ ]+(?=\\n)");
        }
    }
    static double[] getFacing(String in) {
        String facing = in.substring(in.indexOf("(", in.indexOf("(", in.indexOf("Facing")) + 1) + 1, in.indexOf(")", in.indexOf(")", in.indexOf("Facing")) + 1));
        double yaw = Double.parseDouble(facing.substring(0, facing.indexOf(" ")));
        double pitch = Double.parseDouble(facing.substring(facing.indexOf(" ") + 2));
        return new double[]{yaw, pitch};
    }
    static double[] getFacingFromRobot() throws InterruptedException {
        String in = getDebug(getGameScreen());
        double yaw = 0;
        double pitch = 0;
        try {
            String facing = in.substring(in.indexOf("(", in.indexOf("(", in.indexOf("Facing")) + 1) + 1, in.indexOf(")", in.indexOf(")", in.indexOf("Facing")) + 1));
            yaw = Double.parseDouble(facing.substring(0, facing.indexOf(" ")));
            pitch = Double.parseDouble(facing.substring(facing.indexOf(" ") + 2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new double[]{yaw, pitch};
    }
    static void pointAt(double targetYaw, double targetPitch) throws InterruptedException {
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        int mouseX = mouseLocation.x;
        int mouseY = mouseLocation.y;
        double[] facing;
        double yawDiff;
        double pitchDiff;
        targetYaw = round(targetYaw, 1);
        targetPitch = round(targetPitch, 1);
        while (true) {
            facing = getFacingFromRobot();
            yawDiff = targetYaw - facing[0];
            pitchDiff = targetPitch - facing[1];
            if (yawDiff < -180) {
                yawDiff +=360;
            }
            if (yawDiff > 180) {
                yawDiff -=360;
            }
            if (yawDiff == 0 && pitchDiff == 0) break;
            robot.mouseMove((int) (mouseX + Math.round(yawDiff*10)), (int) (mouseY + Math.round(pitchDiff*10)));
            //System.out.println("Moved! x:" + Math.round(yawDiff*10) + " y:" + Math.round(pitchDiff*10));
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
    static void rightClick() throws InterruptedException {
        if (holdingRMB) {
            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            TimeUnit.MILLISECONDS.sleep(50);
        }
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
        TimeUnit.MILLISECONDS.sleep(50);
        if (holdingRMB) {
            robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
            TimeUnit.MILLISECONDS.sleep(50);
        }
    }
    static void leftClick() throws InterruptedException {
        if (holdingLMB) {
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            TimeUnit.MILLISECONDS.sleep(50);
        }
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        TimeUnit.MILLISECONDS.sleep(50);
        if (holdingLMB) {
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            TimeUnit.MILLISECONDS.sleep(50);
        }
    }
    static void holdLMB(){
        if (!holdingLMB) {
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            holdingLMB = true;
        } else System.out.println("already holding LMB");
    }
    static void releaseLMB(){
        if (holdingLMB) {
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            holdingLMB = false;
        } else System.out.println("already not holding LMB");
    }
    static boolean checkMenu(BufferedImage img) {
        if (secondScreen) {
            Color color = new Color(img.getRGB(862, 161));
            return color.getRed() == 252 && color.getGreen() == 252 && color.getBlue() == 252;
        } else {
            Color color = new Color(img.getRGB(883, 124));
            return color.getRed() == 252 && color.getGreen() == 252 && color.getBlue() == 252;}
    }
    static boolean checkChat(BufferedImage img) {
        if (secondScreen) {

            Color color = new Color(img.getRGB(3,857));
            return color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0;
        } else {
            Color color = new Color(img.getRGB(3,894));
            return color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0;
        }
    }
    static boolean checkBed(BufferedImage img) {
        if (secondScreen) {
            Color color = new Color(img.getRGB(858,885));
            return color.getRed() == 252 && color.getGreen() == 252 && color.getBlue() == 252;
        } else {
            Color color = new Color(img.getRGB(760,905));
            return color.getRed() == 115 && color.getGreen() == 115 && color.getBlue() == 115;
        }
    }
    static void writeChat(String msg) throws InterruptedException {
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        TimeUnit.MILLISECONDS.sleep(100);
        String send = msg;

        if (msg.length() > 256) {
            send = msg.substring(0, 256);
        }
        for (char c: send.toCharArray()) {
            switch (c) {
                case 'A':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_A);
                    robot.keyRelease(KeyEvent.VK_A);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'B':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_B);
                    robot.keyRelease(KeyEvent.VK_B);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'C':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_C);
                    robot.keyRelease(KeyEvent.VK_C);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'D':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_D);
                    robot.keyRelease(KeyEvent.VK_D);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'E':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_E);
                    robot.keyRelease(KeyEvent.VK_E);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'F':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_F);
                    robot.keyRelease(KeyEvent.VK_F);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'G':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_G);
                    robot.keyRelease(KeyEvent.VK_G);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'H':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_H);
                    robot.keyRelease(KeyEvent.VK_H);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'I':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_I);
                    robot.keyRelease(KeyEvent.VK_I);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'J':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_J);
                    robot.keyRelease(KeyEvent.VK_J);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'K':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_K);
                    robot.keyRelease(KeyEvent.VK_K);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'L':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_L);
                    robot.keyRelease(KeyEvent.VK_L);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'M':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_M);
                    robot.keyRelease(KeyEvent.VK_M);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'N':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_N);
                    robot.keyRelease(KeyEvent.VK_N);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'O':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_O);
                    robot.keyRelease(KeyEvent.VK_O);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'P':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_P);
                    robot.keyRelease(KeyEvent.VK_P);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'Q':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_Q);
                    robot.keyRelease(KeyEvent.VK_Q);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'R':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_R);
                    robot.keyRelease(KeyEvent.VK_R);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'S':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_S);
                    robot.keyRelease(KeyEvent.VK_S);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'T':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_T);
                    robot.keyRelease(KeyEvent.VK_T);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'U':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_U);
                    robot.keyRelease(KeyEvent.VK_U);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'V':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_V);
                    robot.keyRelease(KeyEvent.VK_V);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'W':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_W);
                    robot.keyRelease(KeyEvent.VK_W);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'X':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_X);
                    robot.keyRelease(KeyEvent.VK_X);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'Y':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_Y);
                    robot.keyRelease(KeyEvent.VK_Y);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'Z':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_Z);
                    robot.keyRelease(KeyEvent.VK_Z);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case 'a':   robot.keyPress(KeyEvent.VK_A);
                    robot.keyRelease(KeyEvent.VK_A);
                    break;
                case 'b':   robot.keyPress(KeyEvent.VK_B);
                    robot.keyRelease(KeyEvent.VK_B);
                    break;
                case 'c':   robot.keyPress(KeyEvent.VK_C);
                    robot.keyRelease(KeyEvent.VK_C);
                    break;
                case 'd':   robot.keyPress(KeyEvent.VK_D);
                    robot.keyRelease(KeyEvent.VK_D);
                    break;
                case 'e':   robot.keyPress(KeyEvent.VK_E);
                    robot.keyRelease(KeyEvent.VK_E);
                    break;
                case 'f':   robot.keyPress(KeyEvent.VK_F);
                    robot.keyRelease(KeyEvent.VK_F);
                    break;
                case 'g':   robot.keyPress(KeyEvent.VK_G);
                    robot.keyRelease(KeyEvent.VK_G);
                    break;
                case 'h':   robot.keyPress(KeyEvent.VK_H);
                    robot.keyRelease(KeyEvent.VK_H);
                    break;
                case 'i':   robot.keyPress(KeyEvent.VK_I);
                    robot.keyRelease(KeyEvent.VK_I);
                    break;
                case 'j':   robot.keyPress(KeyEvent.VK_J);
                    robot.keyRelease(KeyEvent.VK_J);
                    break;
                case 'k':   robot.keyPress(KeyEvent.VK_K);
                    robot.keyRelease(KeyEvent.VK_K);
                    break;
                case 'l':   robot.keyPress(KeyEvent.VK_L);
                    robot.keyRelease(KeyEvent.VK_L);
                    break;
                case 'm':   robot.keyPress(KeyEvent.VK_M);
                    robot.keyRelease(KeyEvent.VK_M);
                    break;
                case 'n':   robot.keyPress(KeyEvent.VK_N);
                    robot.keyRelease(KeyEvent.VK_N);
                    break;
                case 'o':   robot.keyPress(KeyEvent.VK_O);
                    robot.keyRelease(KeyEvent.VK_O);
                    break;
                case 'p':   robot.keyPress(KeyEvent.VK_P);
                    robot.keyRelease(KeyEvent.VK_P);
                    break;
                case 'q':   robot.keyPress(KeyEvent.VK_Q);
                    robot.keyRelease(KeyEvent.VK_Q);
                    break;
                case 'r':   robot.keyPress(KeyEvent.VK_R);
                    robot.keyRelease(KeyEvent.VK_R);
                    break;
                case 's':   robot.keyPress(KeyEvent.VK_S);
                    robot.keyRelease(KeyEvent.VK_S);
                    break;
                case 't':   robot.keyPress(KeyEvent.VK_T);
                    robot.keyRelease(KeyEvent.VK_T);
                    break;
                case 'u':   robot.keyPress(KeyEvent.VK_U);
                    robot.keyRelease(KeyEvent.VK_U);
                    break;
                case 'v':   robot.keyPress(KeyEvent.VK_V);
                    robot.keyRelease(KeyEvent.VK_V);
                    break;
                case 'w':   robot.keyPress(KeyEvent.VK_W);
                    robot.keyRelease(KeyEvent.VK_W);
                    break;
                case 'x':   robot.keyPress(KeyEvent.VK_X);
                    robot.keyRelease(KeyEvent.VK_X);
                    break;
                case 'y':   robot.keyPress(KeyEvent.VK_Y);
                    robot.keyRelease(KeyEvent.VK_Y);
                    break;
                case 'z':   robot.keyPress(KeyEvent.VK_Z);
                    robot.keyRelease(KeyEvent.VK_Z);
                    break;
                case ' ':   robot.keyPress(KeyEvent.VK_SPACE);
                    robot.keyRelease(KeyEvent.VK_SPACE);
                    break;
                case '.':   robot.keyPress(KeyEvent.VK_PERIOD);
                    robot.keyRelease(KeyEvent.VK_PERIOD);
                    break;
                case ',':   robot.keyPress(KeyEvent.VK_COMMA);
                    robot.keyRelease(KeyEvent.VK_COMMA);
                    break;
                case ':':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_PERIOD);
                    robot.keyRelease(KeyEvent.VK_PERIOD);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case '/':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_7);
                    robot.keyRelease(KeyEvent.VK_7);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case '=':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_0);
                    robot.keyRelease(KeyEvent.VK_0);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case '(':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_8);
                    robot.keyRelease(KeyEvent.VK_8);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case ')':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_9);
                    robot.keyRelease(KeyEvent.VK_9);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case '{':
                    robot.keyPress(KeyEvent.VK_ALT_GRAPH);
                    robot.keyPress(KeyEvent.VK_7);
                    robot.keyRelease(KeyEvent.VK_7);
                    robot.keyRelease(KeyEvent.VK_ALT_GRAPH);
                    break;
                case '}':
                    robot.keyPress(KeyEvent.VK_ALT_GRAPH);
                    robot.keyPress(KeyEvent.VK_0);
                    robot.keyRelease(KeyEvent.VK_0);
                    robot.keyRelease(KeyEvent.VK_ALT_GRAPH);
                    break;
                case '[':
                    robot.keyPress(KeyEvent.VK_ALT_GRAPH);
                    robot.keyPress(KeyEvent.VK_8);
                    robot.keyRelease(KeyEvent.VK_8);
                    robot.keyRelease(KeyEvent.VK_ALT_GRAPH);
                    break;
                case ']':
                    robot.keyPress(KeyEvent.VK_ALT_GRAPH);
                    robot.keyPress(KeyEvent.VK_9);
                    robot.keyRelease(KeyEvent.VK_9);
                    robot.keyRelease(KeyEvent.VK_ALT_GRAPH);
                    break;
                case '-':
                    robot.keyPress(KeyEvent.VK_MINUS);
                    robot.keyRelease(KeyEvent.VK_MINUS);
                    break;
                case '+':
                    robot.keyPress(KeyEvent.VK_PLUS);
                    robot.keyRelease(KeyEvent.VK_PLUS);
                    break;
                case '*':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_PLUS);
                    robot.keyRelease(KeyEvent.VK_PLUS);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case '_':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_MINUS);
                    robot.keyRelease(KeyEvent.VK_MINUS);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case '%':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_5);
                    robot.keyRelease(KeyEvent.VK_5);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case '~':
                    robot.keyPress(KeyEvent.VK_ALT_GRAPH);
                    robot.keyPress(KeyEvent.VK_PLUS);
                    robot.keyRelease(KeyEvent.VK_PLUS);
                    robot.keyRelease(KeyEvent.VK_ALT_GRAPH);
                    break;
                case '1':   robot.keyPress(KeyEvent.VK_1);
                    robot.keyRelease(KeyEvent.VK_1);
                    break;
                case '2':   robot.keyPress(KeyEvent.VK_2);
                    robot.keyRelease(KeyEvent.VK_2);
                    break;
                case '3':   robot.keyPress(KeyEvent.VK_3);
                    robot.keyRelease(KeyEvent.VK_3);
                    break;
                case '4':   robot.keyPress(KeyEvent.VK_4);
                    robot.keyRelease(KeyEvent.VK_4);
                    break;
                case '5':   robot.keyPress(KeyEvent.VK_5);
                    robot.keyRelease(KeyEvent.VK_5);
                    break;
                case '6':   robot.keyPress(KeyEvent.VK_6);
                    robot.keyRelease(KeyEvent.VK_6);
                    break;
                case '7':   robot.keyPress(KeyEvent.VK_7);
                    robot.keyRelease(KeyEvent.VK_7);
                    break;
                case '8':   robot.keyPress(KeyEvent.VK_8);
                    robot.keyRelease(KeyEvent.VK_8);
                    break;
                case '9':   robot.keyPress(KeyEvent.VK_9);
                    robot.keyRelease(KeyEvent.VK_9);
                    break;
                case '0':   robot.keyPress(KeyEvent.VK_0);
                    robot.keyRelease(KeyEvent.VK_0);
                    break;
                case '#':   robot.keyPress(KeyEvent.VK_NUMBER_SIGN);
                    robot.keyRelease(KeyEvent.VK_NUMBER_SIGN);
                    break;
                case '\'':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_NUMBER_SIGN);
                    robot.keyRelease(KeyEvent.VK_NUMBER_SIGN);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case '"':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_2);
                    robot.keyRelease(KeyEvent.VK_2);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case '!':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_1);
                    robot.keyRelease(KeyEvent.VK_1);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                default:    System.out.println("invalid character: " + c);
                    break;
            }
        }
        TimeUnit.MILLISECONDS.sleep(50);

        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        TimeUnit.MILLISECONDS.sleep(50);
        if (msg.length() > 256) {
            writeChat(msg.substring(256));
        }

    }
    static String returnRegExMatch(String in, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(in);
        if (matcher.find()) {
            return matcher.group(); // you can get it from desired index as well
        } else {
            return null;
        }

    }
    static boolean mineBlock() throws InterruptedException {
        String blackList = "minecraft:air minecraft:bedrock";
        String debugInfo = getDebug(getGameScreen());
        String targetBlock = getLookingAtBlock(debugInfo);
        if (blackList.contains(targetBlock)){
            return false;
        }
        double[] targetCoords = getLookingAtBlockCoords(debugInfo);
        //min distance 4
        double distance = distance3D(targetCoords, getPlayerPos(debugInfo));
        if (distance > 4) return false;
        holdLMB();
        while (true) {
            TimeUnit.MILLISECONDS.sleep(100);
            debugInfo = getDebug(getGameScreen());
            if (!Arrays.equals(targetCoords, getLookingAtBlockCoords(debugInfo))) break;
            if (!targetBlock.equals(getLookingAtBlock(debugInfo))) break;
        }
        releaseLMB();


        return true;
    }
    static boolean mineBlockWithTool() throws InterruptedException {
        //pickaxe or shovel
        String blackList = "minecraft:air minecraft:bedrock";
        String debugInfo = getDebug(getGameScreen());
        String targetBlock = getLookingAtBlock(debugInfo);
        if (blackList.contains(targetBlock)){
            return false;
        }
        double[] targetCoords = getLookingAtBlockCoords(debugInfo);
        //min distance 4
        double distance = distance3D(targetCoords, getPlayerPos(debugInfo));
        if (distance > 4) return false;
        switch (targetBlock) {
            case "minecraft:dirt":
            case "minecraft:sand":
            case "minecraft:gravel":
                pressButton(slotShovel);
                break;
            default:
                pressButton(slotPickaxe);
                break;
        }
        holdLMB();
        while (true) {
            TimeUnit.MILLISECONDS.sleep(100);
            debugInfo = getDebug(getGameScreen());
            if (!Arrays.equals(targetCoords, getLookingAtBlockCoords(debugInfo))) break;
            if (!targetBlock.equals(getLookingAtBlock(debugInfo))) break;
        }
        releaseLMB();


        return true;
    }

    static double distance3D(double[] pos1, double[] pos2) {
        return Math.sqrt((pos1[0]-pos2[0])*(pos1[0]-pos2[0])+(pos1[1]-pos2[1])*(pos1[1]-pos2[1])+(pos1[2]-pos2[2])*(pos1[2]-pos2[2]));
    }
    static void pressButton(char c) {
        switch (c) {
            case 'A':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_A);
                robot.keyRelease(KeyEvent.VK_A);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'B':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_B);
                robot.keyRelease(KeyEvent.VK_B);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'C':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_C);
                robot.keyRelease(KeyEvent.VK_C);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'D':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_D);
                robot.keyRelease(KeyEvent.VK_D);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'E':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_E);
                robot.keyRelease(KeyEvent.VK_E);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'F':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_F);
                robot.keyRelease(KeyEvent.VK_F);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'G':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_G);
                robot.keyRelease(KeyEvent.VK_G);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'H':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_H);
                robot.keyRelease(KeyEvent.VK_H);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'I':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_I);
                robot.keyRelease(KeyEvent.VK_I);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'J':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_J);
                robot.keyRelease(KeyEvent.VK_J);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'K':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_K);
                robot.keyRelease(KeyEvent.VK_K);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'L':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_L);
                robot.keyRelease(KeyEvent.VK_L);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'M':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_M);
                robot.keyRelease(KeyEvent.VK_M);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'N':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_N);
                robot.keyRelease(KeyEvent.VK_N);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'O':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_O);
                robot.keyRelease(KeyEvent.VK_O);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'P':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_P);
                robot.keyRelease(KeyEvent.VK_P);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'Q':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_Q);
                robot.keyRelease(KeyEvent.VK_Q);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'R':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_R);
                robot.keyRelease(KeyEvent.VK_R);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'S':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_S);
                robot.keyRelease(KeyEvent.VK_S);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'T':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_T);
                robot.keyRelease(KeyEvent.VK_T);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'U':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_U);
                robot.keyRelease(KeyEvent.VK_U);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'V':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'W':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_W);
                robot.keyRelease(KeyEvent.VK_W);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'X':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_X);
                robot.keyRelease(KeyEvent.VK_X);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'Y':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_Y);
                robot.keyRelease(KeyEvent.VK_Y);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'Z':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_Z);
                robot.keyRelease(KeyEvent.VK_Z);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case 'a':   robot.keyPress(KeyEvent.VK_A);
                robot.keyRelease(KeyEvent.VK_A);
                break;
            case 'b':   robot.keyPress(KeyEvent.VK_B);
                robot.keyRelease(KeyEvent.VK_B);
                break;
            case 'c':   robot.keyPress(KeyEvent.VK_C);
                robot.keyRelease(KeyEvent.VK_C);
                break;
            case 'd':   robot.keyPress(KeyEvent.VK_D);
                robot.keyRelease(KeyEvent.VK_D);
                break;
            case 'e':   robot.keyPress(KeyEvent.VK_E);
                robot.keyRelease(KeyEvent.VK_E);
                break;
            case 'f':   robot.keyPress(KeyEvent.VK_F);
                robot.keyRelease(KeyEvent.VK_F);
                break;
            case 'g':   robot.keyPress(KeyEvent.VK_G);
                robot.keyRelease(KeyEvent.VK_G);
                break;
            case 'h':   robot.keyPress(KeyEvent.VK_H);
                robot.keyRelease(KeyEvent.VK_H);
                break;
            case 'i':   robot.keyPress(KeyEvent.VK_I);
                robot.keyRelease(KeyEvent.VK_I);
                break;
            case 'j':   robot.keyPress(KeyEvent.VK_J);
                robot.keyRelease(KeyEvent.VK_J);
                break;
            case 'k':   robot.keyPress(KeyEvent.VK_K);
                robot.keyRelease(KeyEvent.VK_K);
                break;
            case 'l':   robot.keyPress(KeyEvent.VK_L);
                robot.keyRelease(KeyEvent.VK_L);
                break;
            case 'm':   robot.keyPress(KeyEvent.VK_M);
                robot.keyRelease(KeyEvent.VK_M);
                break;
            case 'n':   robot.keyPress(KeyEvent.VK_N);
                robot.keyRelease(KeyEvent.VK_N);
                break;
            case 'o':   robot.keyPress(KeyEvent.VK_O);
                robot.keyRelease(KeyEvent.VK_O);
                break;
            case 'p':   robot.keyPress(KeyEvent.VK_P);
                robot.keyRelease(KeyEvent.VK_P);
                break;
            case 'q':   robot.keyPress(KeyEvent.VK_Q);
                robot.keyRelease(KeyEvent.VK_Q);
                break;
            case 'r':   robot.keyPress(KeyEvent.VK_R);
                robot.keyRelease(KeyEvent.VK_R);
                break;
            case 's':   robot.keyPress(KeyEvent.VK_S);
                robot.keyRelease(KeyEvent.VK_S);
                break;
            case 't':   robot.keyPress(KeyEvent.VK_T);
                robot.keyRelease(KeyEvent.VK_T);
                break;
            case 'u':   robot.keyPress(KeyEvent.VK_U);
                robot.keyRelease(KeyEvent.VK_U);
                break;
            case 'v':   robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_V);
                break;
            case 'w':   robot.keyPress(KeyEvent.VK_W);
                robot.keyRelease(KeyEvent.VK_W);
                break;
            case 'x':   robot.keyPress(KeyEvent.VK_X);
                robot.keyRelease(KeyEvent.VK_X);
                break;
            case 'y':   robot.keyPress(KeyEvent.VK_Y);
                robot.keyRelease(KeyEvent.VK_Y);
                break;
            case 'z':   robot.keyPress(KeyEvent.VK_Z);
                robot.keyRelease(KeyEvent.VK_Z);
                break;
            case ' ':   robot.keyPress(KeyEvent.VK_SPACE);
                robot.keyRelease(KeyEvent.VK_SPACE);
                break;
            case '.':   robot.keyPress(KeyEvent.VK_PERIOD);
                robot.keyRelease(KeyEvent.VK_PERIOD);
                break;
            case ',':   robot.keyPress(KeyEvent.VK_COMMA);
                robot.keyRelease(KeyEvent.VK_COMMA);
                break;
            case ':':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_PERIOD);
                robot.keyRelease(KeyEvent.VK_PERIOD);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case '/':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_7);
                robot.keyRelease(KeyEvent.VK_7);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case '=':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_0);
                robot.keyRelease(KeyEvent.VK_0);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case '(':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_8);
                robot.keyRelease(KeyEvent.VK_8);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case ')':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_9);
                robot.keyRelease(KeyEvent.VK_9);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case '{':
                robot.keyPress(KeyEvent.VK_ALT_GRAPH);
                robot.keyPress(KeyEvent.VK_7);
                robot.keyRelease(KeyEvent.VK_7);
                robot.keyRelease(KeyEvent.VK_ALT_GRAPH);
                break;
            case '}':
                robot.keyPress(KeyEvent.VK_ALT_GRAPH);
                robot.keyPress(KeyEvent.VK_0);
                robot.keyRelease(KeyEvent.VK_0);
                robot.keyRelease(KeyEvent.VK_ALT_GRAPH);
                break;
            case '[':
                robot.keyPress(KeyEvent.VK_ALT_GRAPH);
                robot.keyPress(KeyEvent.VK_8);
                robot.keyRelease(KeyEvent.VK_8);
                robot.keyRelease(KeyEvent.VK_ALT_GRAPH);
                break;
            case ']':
                robot.keyPress(KeyEvent.VK_ALT_GRAPH);
                robot.keyPress(KeyEvent.VK_9);
                robot.keyRelease(KeyEvent.VK_9);
                robot.keyRelease(KeyEvent.VK_ALT_GRAPH);
                break;
            case '-':
                robot.keyPress(KeyEvent.VK_MINUS);
                robot.keyRelease(KeyEvent.VK_MINUS);
                break;
            case '+':
                robot.keyPress(KeyEvent.VK_PLUS);
                robot.keyRelease(KeyEvent.VK_PLUS);
                break;
            case '*':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_PLUS);
                robot.keyRelease(KeyEvent.VK_PLUS);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case '~':
                robot.keyPress(KeyEvent.VK_ALT_GRAPH);
                robot.keyPress(KeyEvent.VK_PLUS);
                robot.keyRelease(KeyEvent.VK_PLUS);
                robot.keyRelease(KeyEvent.VK_ALT_GRAPH);
                break;
            case '1':   robot.keyPress(KeyEvent.VK_1);
                robot.keyRelease(KeyEvent.VK_1);
                break;
            case '2':   robot.keyPress(KeyEvent.VK_2);
                robot.keyRelease(KeyEvent.VK_2);
                break;
            case '3':   robot.keyPress(KeyEvent.VK_3);
                robot.keyRelease(KeyEvent.VK_3);
                break;
            case '4':   robot.keyPress(KeyEvent.VK_4);
                robot.keyRelease(KeyEvent.VK_4);
                break;
            case '5':   robot.keyPress(KeyEvent.VK_5);
                robot.keyRelease(KeyEvent.VK_5);
                break;
            case '6':   robot.keyPress(KeyEvent.VK_6);
                robot.keyRelease(KeyEvent.VK_6);
                break;
            case '7':   robot.keyPress(KeyEvent.VK_7);
                robot.keyRelease(KeyEvent.VK_7);
                break;
            case '8':   robot.keyPress(KeyEvent.VK_8);
                robot.keyRelease(KeyEvent.VK_8);
                break;
            case '9':   robot.keyPress(KeyEvent.VK_9);
                robot.keyRelease(KeyEvent.VK_9);
                break;
            case '0':   robot.keyPress(KeyEvent.VK_0);
                robot.keyRelease(KeyEvent.VK_0);
                break;
            case '#':   robot.keyPress(KeyEvent.VK_NUMBER_SIGN);
                robot.keyRelease(KeyEvent.VK_NUMBER_SIGN);
                break;
            case '\'':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_NUMBER_SIGN);
                robot.keyRelease(KeyEvent.VK_NUMBER_SIGN);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case '"':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_2);
                robot.keyRelease(KeyEvent.VK_2);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case '!':
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_1);
                robot.keyRelease(KeyEvent.VK_1);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            default:    System.out.println("invalid character: " + c);
                break;
        }
    }
    //TODO here is the map comment
    /*
    * MAP:
    * GREEN     marks the start of the mining operation and the lower end of the staircase
    * GRAY      marks stone wall with no ores
    * RED       marks locations with air or fluids
    * ORANGE    marks locations with monster eggs (infested stone)
    * BLACK     marks dug-out tunnels
    * WHITE     marks unexplored areas
    * BLUE      marks suspected structures
    * CYAN      marks ore on tunnel level
    * MAGENTA   roof ore
    * PINK      floor ore
    * YELLOW    roof and floor ore
    *
    * the scanning functions (scanUpperHalf and scanLowerHalf) shall take care of all map drawing except for
    * coloring tunnels that have been blocked off due to monsters red.
    * */

    /*IN CASE OF BABY ZOMBIE
    * detection: unexpected loss of health.
    * what to do:
    * place down waterbucket on floor
    * move to center of block with water source. this will keep the baby zombie away
    * block the tunnel in the direction you were moving in
    * place upper slab on roof of tunnel in the direction you came from
    * place block underneath
    * place lava through slab on the other side of the slab
    * wait a few seconds
    * baby zombie is hopefully dead
    * remove lava
    * remove slab and the block beneath it
    * remove water
    * retreat
    * */
    static void centerPosition() throws InterruptedException, NullPointerException {
        //goes within 0.04 blocks of the current position blocks center
        String debug = getDebug(getGameScreen());
        double[] pos = getPlayerPos(debug);
        double targetX = Math.floor(pos[0]) + 0.5;
        double targetZ = Math.floor(pos[2]) + 0.5;
        double offsetX = targetX - pos[0];
        double offsetZ = targetZ - pos[2];
        robot.keyPress(KeyEvent.VK_SHIFT);
        while (true) {

            if (debug.contains("positive X")) {
                if (offsetX < -0.04) {
                    robot.keyPress(KeyEvent.VK_S);
                    TimeUnit.MILLISECONDS.sleep(20);
                    robot.keyRelease(KeyEvent.VK_S);
                } else if (offsetX > 0.04) {
                    robot.keyPress(KeyEvent.VK_W);
                    TimeUnit.MILLISECONDS.sleep(20);
                    robot.keyRelease(KeyEvent.VK_W);
                } else if (offsetZ < -0.04) {
                    robot.keyPress(KeyEvent.VK_A);
                    TimeUnit.MILLISECONDS.sleep(20);
                    robot.keyRelease(KeyEvent.VK_A);
                } else if (offsetZ > 0.04) {
                    robot.keyPress(KeyEvent.VK_D);
                    TimeUnit.MILLISECONDS.sleep(20);
                    robot.keyRelease(KeyEvent.VK_D);
                } else break;
            }
            if (debug.contains("negative X")) {
                if (offsetX < -0.04) {
                    robot.keyPress(KeyEvent.VK_W);
                    TimeUnit.MILLISECONDS.sleep(20);
                    robot.keyRelease(KeyEvent.VK_W);
                } else if (offsetX > 0.04) {
                    robot.keyPress(KeyEvent.VK_S);
                    TimeUnit.MILLISECONDS.sleep(20);
                    robot.keyRelease(KeyEvent.VK_S);
                } else if (offsetZ < -0.04) {
                    robot.keyPress(KeyEvent.VK_D);
                    TimeUnit.MILLISECONDS.sleep(20);
                    robot.keyRelease(KeyEvent.VK_D);
                } else if (offsetZ > 0.04) {
                    robot.keyPress(KeyEvent.VK_A);
                    TimeUnit.MILLISECONDS.sleep(20);
                    robot.keyRelease(KeyEvent.VK_A);
                } else break;
            }
            if (debug.contains("positive Z")) {
                if (offsetX < -0.04) {
                    robot.keyPress(KeyEvent.VK_D);
                    TimeUnit.MILLISECONDS.sleep(20);
                    robot.keyRelease(KeyEvent.VK_D);
                } else if (offsetX > 0.04) {
                    robot.keyPress(KeyEvent.VK_A);
                    TimeUnit.MILLISECONDS.sleep(20);
                    robot.keyRelease(KeyEvent.VK_A);
                } else if (offsetZ < -0.04) {
                    robot.keyPress(KeyEvent.VK_S);
                    TimeUnit.MILLISECONDS.sleep(20);
                    robot.keyRelease(KeyEvent.VK_S);
                } else if (offsetZ > 0.04) {
                    robot.keyPress(KeyEvent.VK_W);
                    TimeUnit.MILLISECONDS.sleep(20);
                    robot.keyRelease(KeyEvent.VK_W);
                } else break;
            }
            if (debug.contains("negative Z")) {
                if (offsetX < -0.04) {
                    robot.keyPress(KeyEvent.VK_A);
                    TimeUnit.MILLISECONDS.sleep(20);
                    robot.keyRelease(KeyEvent.VK_A);
                } else if (offsetX > 0.04) {
                    robot.keyPress(KeyEvent.VK_D);
                    TimeUnit.MILLISECONDS.sleep(20);
                    robot.keyRelease(KeyEvent.VK_D);
                } else if (offsetZ < -0.04) {
                    robot.keyPress(KeyEvent.VK_W);
                    TimeUnit.MILLISECONDS.sleep(20);
                    robot.keyRelease(KeyEvent.VK_W);
                } else if (offsetZ > 0.04) {
                    robot.keyPress(KeyEvent.VK_S);
                    TimeUnit.MILLISECONDS.sleep(20);
                    robot.keyRelease(KeyEvent.VK_S);
                } else break;
            }
            TimeUnit.MILLISECONDS.sleep(100);
            pos = getPlayerPos(getDebug(getGameScreen()));
            offsetX = targetX - pos[0];
            offsetZ = targetZ - pos[2];

        }
        robot.keyRelease(KeyEvent.VK_SHIFT);
    }
}
