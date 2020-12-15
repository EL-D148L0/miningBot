package max.sander;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
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
    static int yawDiff; // values are made for facing toward negative Z     yawdiff is added to yaw values
    static int currentTunnelingDirection = 0; //  0: positive X 1: negative X 2: positive Z 3: negative Z
    static boolean debuggingMode = true;


    //COLOR RGB INTS
    static int green = -16711936;
    static int gray = -8355712;
    static int red = -65536;
    static int orange = -14336;
    static int black = -16777216;
    static int white = -1;
    static int blue = -16776961;
    static int cyan = -16711681;


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

    */


    public static void main(String[] args) {
	// write your code here
        String debug;
        double[] pos;
        try {
            //TimeUnit.MILLISECONDS.sleep(1000);
            robot = new Robot();
            /*map = ImageIO.read(new File("map.png"));
            boolean isBlank = true;
            for (int x = 0; x < map.getWidth(); x++) {
                for (int y = 0; y < map.getHeight(); y++) {
                    if (map.getRGB(x, y) != Color.WHITE.getRGB()) {
                        isBlank = false;
                    }
                }
            }
            System.out.println(isBlank);
            debug = getDebug(getGameScreen());
            if (isBlank) {
                pos = getPlayerPos(debug);
                int playerX = (int) Math.floor(pos[0]);
                int playerZ = (int) Math.floor(pos[2]);
                if (debug.contains("positive X")) {
                    mapOffsetX = playerX;
                    mapOffsetZ = playerZ - (map.getHeight() / 2);
                } else if (debug.contains("negative X")) {
                    mapOffsetX = playerX - map.getWidth();
                    mapOffsetZ = playerZ - (map.getHeight() / 2);
                } else if (debug.contains("positive Z")) {
                    mapOffsetX = playerX - (map.getWidth() / 2);
                    mapOffsetZ = playerZ;
                } else if (debug.contains("negative Z")) {
                    mapOffsetX = playerX - (map.getWidth() / 2);
                    mapOffsetZ = playerZ - map.getHeight();
                }
                FileWriter myWriter = new FileWriter("data.txt");
                myWriter.write("mapOffsetX:" + mapOffsetX + "\n");
                myWriter.write("mapOffsetZ:" + mapOffsetZ + "\n");
                myWriter.close();
                mapColorPos(playerX, playerZ, Color.GREEN);
            } else {
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
            }*/
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

            //writeChat("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz .,:/=(){}[]-+*~1234567890#'\"!");
            //centerPosition();
//            scanUpperHalf();
////            breakFallingStack();
//            writeChat("logoff test");
//            logOff();
//            pointAt(180, 17);
//            mineBlock();
            /*centerPosition();
            pointAt(180 + yawDiff, 0);
            robot.keyPress(KeyEvent.VK_W);
            TimeUnit.MILLISECONDS.sleep(600);
            robot.keyRelease(KeyEvent.VK_W);*/
            /*
            String s;
            while (true) {
                pointAt(180 + yawDiff, 17);
                debug = getDebug(getGameScreen());
                pos = getPlayerPosFloored(debug);

                if (debug.contains("positive X") || debug.contains("negative X")) {
                    if (pos[0] % 8 == 0) placeTorch();
                } else if (debug.contains("positive Z") || debug.contains("negative Z")) {
                    if (pos[2] % 8 == 0) placeTorch();
                }
                System.out.println("mining" + Arrays.toString(getLookingAtBlockCoords(debug)));
                mineBlockWithTool();
                s = scanUpperHalf();
                System.out.println(s);
                writeChat(s);
                pointAt(180 + yawDiff, 45);
                System.out.println("mining" + Arrays.toString(getLookingAtBlockCoordsFromRobot()));
                mineBlockWithTool();
                s = scanLowerHalf();
                System.out.println(s);
                writeChat(s);
                robot.keyPress(KeyEvent.VK_W);
                TimeUnit.MILLISECONDS.sleep(600);
                robot.keyRelease(KeyEvent.VK_W);

            }*/
            /*int[] amounts = getSlotAmounts();
            String[] names = getSlotContent();
            String out = "";
            for (int i = 0; i < names.length; i++) {
                if (!names[i].equals("empty")) {
                    out += amounts[i] + "x " + names[i].replaceAll("\\s+", " ") + "    ";
                }
            }
            System.out.println(out);
            writeChat(out);*/



            /*currentTunnelingDirection = 0;
            String out = mineVeinTunnelLevel();
            System.out.println(out);*/
            /*

            moveToBlock(40, 48, 20000);
            for (int i = 1; i < 9; i++) {
                moveToBlockRough(40+i, 48, 20000);

            }
            moveToBlock(40+9, 48, 20000);*/
//            boolean out = moveToBlock(40, 48, 20000);


/*
            debug = getDebug(getGameScreen());
            faceDirection(getDirection(debug));
            centerPosition();
            robot.keyPress(KeyEvent.VK_W);
            TimeUnit.MILLISECONDS.sleep(100);
            robot.keyRelease(KeyEvent.VK_W);
            faceDirection(getDirection(debug));
            BlockPosWithDirection block3up = new BlockPosWithDirection(debug).forward(2).up(4);
            pointAtPosSneaking(block3up.backward(0.58).down(0.42).left(0.5));

            block3up = block3up.down();
            //robot.keyRelease(KeyEvent.VK_SHIFT);
            pointAtPos(block3up.backward(0.50).down(0.42));*/
            fillSandRoofHole();
//            writeChat("moved to 40, 48:" + out);
            if (false) throw new DebugTextIncompleteException("whatever"); // this is here to keep the catch even if i'm not using anything that generates this exception

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
    static String mineVeinTunnelLevel() throws InterruptedException {
        /* call this facing the wall that contains ore
        * this should mine all the ore of the vein that is on the same level as the tunnel and return to the location where it was called returning "done".
        * if it runs into an unexpected situation (cave or liquid etc) it will return to the location it was called, block off the dug out space and return "unexpected".
        * */

        String debug = getDebug(getGameScreen());
        BlockPosWithDirection playerPos = new BlockPosWithDirection(debug);

        VeinMinerTreeElement root = new VeinMinerTreeElement(playerPos.getX(), playerPos.getY(), playerPos.getZ());
        VeinMinerTreeElement currentElement = new VeinMinerTreeElement(playerPos.forward().getX(), playerPos.forward().getY(), playerPos.forward().getZ(), root);

        while (!VeinMinerTreeElement.isCleared()) {

        }



        /*TODO
            - make first thing part of loop after making root element on starting position and first ore element in front of it



        * */

        //TODO fleeing mechanics must contain map recolouring of cutoof tunnel part





        BlockPosWithDirection minedBlockPos = new BlockPosWithDirection(debug).up().forward();
        pointAtPos(minedBlockPos.backward(0.5));
        mineBlockWithTool();



        //repeated for the first time cause that's where it starts so things are different
        boolean oreLeft = false;
        boolean oreRight = false;
        boolean oreForward = false;
        // forward block

        pointAtPos(minedBlockPos.forward(0.5));

        TimeUnit.MILLISECONDS.sleep(100);

        debug = getDebug(getGameScreen());
        String block = getLookingAtBlock(debug);
        double[] blockPos = getLookingAtBlockCoords(debug);
        if (debug.contains("Targeted Entity")) {
            retreatAndBlock(currentTunnelingDirection);
            return "unexpected";
        }
        if (!getLookingAtFluid(debug).equals("minecraft:empty")) {
            placeBlock(minedBlockPos.down(0.5));
            return "unexpected";
        }
        boolean fallingsand = false;
        if (!minedBlockPos.forward().equalsDoubleArray(blockPos)) {
            if (minedBlockPos.equalsDoubleArray(blockPos)) {
                fallingsand = true;
                pointAtPos(minedBlockPos.down(0.5));
                breakFallingStack();
                TimeUnit.SECONDS.sleep(5);
                leftClick();
                pointAtPos(minedBlockPos.forward(0.5));
                debug = getDebug(getGameScreen());
                block = getLookingAtBlock(debug);
                blockPos = getLookingAtBlockCoords(debug);
                if (debug.contains("Targeted Entity")) {
                    retreatAndBlock(currentTunnelingDirection);
                    return "unexpected";
                }
                if (!getLookingAtFluid(debug).equals("minecraft:empty")) {
                    placeBlock(minedBlockPos.down(0.5));
                    return "unexpected";
                }
            }
            if (!minedBlockPos.forward().equalsDoubleArray(blockPos)) {
                writeChat("air");
                if (mapGetColor(minedBlockPos.forward().toDoubleArray()) != black) {
                    mapColorPos(minedBlockPos.forward().toDoubleArray(), Color.RED);
                    placeBlock(minedBlockPos.down(0.5));
                    return "unexpected";
                }
            }
        }
        switch (block) {
            case "minecraft:infested_stone":
                mapColorPos(blockPos, Color.ORANGE);
                placeBlock(minedBlockPos.down(0.5));
                return "unexpected";
            case "minecraft:emerald_ore": case "minecraft:gold_ore": case "minecraft:iron_ore": case "minecraft:coal_ore":
            case "minecraft:diamond_ore": case "minecraft:redstone_ore": case "minecraft:lapis_ore":
                mapColorPos(blockPos, Color.CYAN);
                oreForward = true;
                break;
            case "minecraft:cobblestone": case "minecraft:stone": case "minecraft:diorite": case "minecraft:granite":
            case "minecraft:andesite": case "minecraft:dirt": case "minecraft:sand": case "minecraft:gravel":
                mapColorPos(blockPos, Color.GRAY);
                break;
            default:
                mapColorPos(blockPos, Color.BLUE);
                writeChat(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                System.out.println(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                placeBlock(minedBlockPos.down(0.5));
                return "unexpected";
        }
        //forward block done

        // left block

        pointAtPos(minedBlockPos.left(0.5));

        debug = getDebug(getGameScreen());
        block = getLookingAtBlock(debug);
        blockPos = getLookingAtBlockCoords(debug);
        if (debug.contains("Targeted Entity")) {
            retreatAndBlock(currentTunnelingDirection);
            return "unexpected";
        }
        if (!getLookingAtFluid(debug).equals("minecraft:empty")) {
            placeBlock(minedBlockPos.down(0.5));
            return "unexpected";
        }
        if (!minedBlockPos.left().equalsDoubleArray(blockPos)) {
            writeChat("air");
            if (mapGetColor(minedBlockPos.forward().toDoubleArray()) != black) {
                mapColorPos(minedBlockPos.forward().toDoubleArray(), Color.RED);
                placeBlock(minedBlockPos.down(0.5));
                return "unexpected";
            }
        }
        switch (block) {
            case "minecraft:infested_stone":
                mapColorPos(blockPos, Color.ORANGE);
                placeBlock(minedBlockPos.down(0.5));
                return "unexpected";
            case "minecraft:emerald_ore": case "minecraft:gold_ore": case "minecraft:iron_ore": case "minecraft:coal_ore":
            case "minecraft:diamond_ore": case "minecraft:redstone_ore": case "minecraft:lapis_ore":
                mapColorPos(blockPos, Color.CYAN);
                oreLeft = true;
                break;
            case "minecraft:cobblestone": case "minecraft:stone": case "minecraft:diorite": case "minecraft:granite":
            case "minecraft:andesite": case "minecraft:dirt": case "minecraft:sand": case "minecraft:gravel":
                mapColorPos(blockPos, Color.GRAY);
                break;
            default:
                mapColorPos(blockPos, Color.BLUE);
                writeChat(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                System.out.println(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                placeBlock(minedBlockPos.down(0.5));
                return "unexpected";
        }
        //left block done
        // right block

        pointAtPos(minedBlockPos.right(0.5));

        debug = getDebug(getGameScreen());
        block = getLookingAtBlock(debug);
        blockPos = getLookingAtBlockCoords(debug);
        if (debug.contains("Targeted Entity")) {
            retreatAndBlock(currentTunnelingDirection);
            return "unexpected";
        }
        if (!getLookingAtFluid(debug).equals("minecraft:empty")) {
            placeBlock(minedBlockPos.down(0.5));
            return "unexpected";
        }
        if (!minedBlockPos.right().equalsDoubleArray(blockPos)) {
            writeChat("air");
            if (mapGetColor(minedBlockPos.forward().toDoubleArray()) != black) {
                mapColorPos(minedBlockPos.forward().toDoubleArray(), Color.RED);
                placeBlock(minedBlockPos.down(0.5));
                return "unexpected";
            }
        }
        switch (block) {
            case "minecraft:infested_stone":
                mapColorPos(blockPos, Color.ORANGE);
                placeBlock(minedBlockPos.down(0.5));
                return "unexpected";
            case "minecraft:emerald_ore": case "minecraft:gold_ore": case "minecraft:iron_ore": case "minecraft:coal_ore":
            case "minecraft:diamond_ore": case "minecraft:redstone_ore": case "minecraft:lapis_ore":
                mapColorPos(blockPos, Color.CYAN);
                oreRight = true;
                break;
            case "minecraft:cobblestone": case "minecraft:stone": case "minecraft:diorite": case "minecraft:granite":
            case "minecraft:andesite": case "minecraft:dirt": case "minecraft:sand": case "minecraft:gravel":
                mapColorPos(blockPos, Color.GRAY);
                break;
            default:
                mapColorPos(blockPos, Color.BLUE);
                writeChat(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                System.out.println(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                placeBlock(minedBlockPos.down(0.5));
                return "unexpected";
        }
        //right block done
        // top block

        if (!fallingsand) {
            pointAtPos(minedBlockPos.up(0.5));

            debug = getDebug(getGameScreen());
            block = getLookingAtBlock(debug);
            blockPos = getLookingAtBlockCoords(debug);
            if (debug.contains("Targeted Entity")) {
                retreatAndBlock(currentTunnelingDirection);
                return "unexpected";
            }
            if (!getLookingAtFluid(debug).equals("minecraft:empty")) {
                placeBlock(minedBlockPos.down(0.5));
                return "unexpected";
            }
            if (!minedBlockPos.up().equalsDoubleArray(blockPos)) {
                writeChat("air");
                if (mapGetColor(minedBlockPos.forward().toDoubleArray()) != black) {
                    mapColorPos(minedBlockPos.forward().toDoubleArray(), Color.RED);
                    placeBlock(minedBlockPos.down(0.5));
                    return "unexpected";
                }
            }
            switch (block) {
                case "minecraft:infested_stone":
                    mapColorPos(blockPos, Color.ORANGE);
                    placeBlock(minedBlockPos.down(0.5));
                    return "unexpected";
                case "minecraft:emerald_ore": case "minecraft:gold_ore": case "minecraft:iron_ore": case "minecraft:coal_ore":
                case "minecraft:diamond_ore": case "minecraft:redstone_ore": case "minecraft:lapis_ore":
                    mapColorPos(blockPos, Color.MAGENTA);
                    break;
                case "minecraft:cobblestone": case "minecraft:stone": case "minecraft:diorite": case "minecraft:granite":
                case "minecraft:andesite": case "minecraft:dirt": case "minecraft:sand": case "minecraft:gravel":
                    break;
                default:
                    mapColorPos(blockPos, Color.BLUE);
                    writeChat(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                    System.out.println(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                    placeBlock(minedBlockPos.down(0.5));
                    return "unexpected";
            }
        }
        //top block done




        return "done";
    }
    static String mineVeinMinerTreeElement(VeinMinerTreeElement currentElement, BlockPosList seenBlocks, boolean levelWithTunnel) throws InterruptedException, DebugTextIncompleteException {
        /*mines the given VeinMinerTreeElement. does not flee or move on. generates subelements. changes element to "mined" or "blocked". does not remove duplicates.
        * is called from a position where the VeinMinerTreeElement is in view
        * returns:
        * done: done, success
        * aborted: mining was aborted due to locally fixable problem like water. this vein will continue to be mined.
        * flee: bigger problem like monster. this vein should be abandoned and blocked off.
        * */




        String debug = getDebug(getGameScreen());
        BlockPosWithDirection playerPos = new BlockPosWithDirection(debug);

        BlockPosWithDirection minedBlockPos = new BlockPosWithDirection(currentElement.getX(), currentElement.getY(), currentElement.getZ(), getDirection(debug)).up();
        pointAtPos(minedBlockPos.backward(0.5));
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
        String x = reactToScan(minedBlockPos, examinedBlockPos, seenBlocks, levelWithTunnel, oreForward, fallingsand);
        if (x != null) return x;
        
        // upper left block
        examinedBlockPos = minedBlockPos.left();
        x = reactToScan(minedBlockPos, examinedBlockPos, seenBlocks, levelWithTunnel, oreLeft, fallingsand);
        if (x != null) return x;
        
        // upper right block
        examinedBlockPos = minedBlockPos.right();
        x = reactToScan(minedBlockPos, examinedBlockPos, seenBlocks, levelWithTunnel, oreLeft, fallingsand);
        if (x != null) return x;

        // top block

        if (fallingsand.get()) {
            fillSandRoofHole();
        } else {
            examinedBlockPos = minedBlockPos.up();
            x = reactToScanTop(minedBlockPos, examinedBlockPos, seenBlocks, levelWithTunnel);
            if (x != null) return x;

        }



        if (fallingsand.get()) System.out.println("adsasd");
        if (oreForward.get()) System.out.println("adsasd");
        if (oreLeft.get()) System.out.println("adsasd");
        if (oreRight.get()) System.out.println("adsasd");





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
        TimeUnit.MILLISECONDS.sleep(100);




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

    private static String reactToScan(BlockPosWithDirection minedBlockPos, BlockPosWithDirection examinedBlockPos, BlockPosList seenBlocks, boolean levelWithTunnel, BooleanHolder ore, BooleanHolder fallingsand) throws InterruptedException, DebugTextIncompleteException {
        String result;
        String debug;
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
    private static String reactToScanTop(BlockPosWithDirection minedBlockPos, BlockPosWithDirection examinedBlockPos, BlockPosList seenBlocks, boolean levelWithTunnel) throws InterruptedException, DebugTextIncompleteException {
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

    static String scanBlock(BlockPosWithDirection examinedBlockPos, BlockPosWithDirection minedBlockPos, BlockPosList seenBlocks) throws InterruptedException, DebugTextIncompleteException {
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
        pointAtPos(examinedBlockPointAtPos);
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
                pointAtPos(minedBlockPos.down(0.5));
                breakFallingStackSlab();
                TimeUnit.SECONDS.sleep(2);
                mineBlockWithTool();
                pointAtPos(examinedBlockPointAtPos);
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
                writeChatDebug("air");
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
    static boolean blockFluidBreakin(BlockPosWithDirection blockPos) throws InterruptedException, DebugTextIncompleteException {
        pointAtPos(blockPos.down(0.5));
        String debug = getDebug(getGameScreen());
        double[] lookingAtBlockPos = getLookingAtBlockCoordsNotNull(debug);
        if (blockPos.down().equalsDoubleArray(lookingAtBlockPos)) {
            placeBlock();
            return true;
        } else {
            pointAtPos(blockPos.left(0.5));
            debug = getDebug(getGameScreen());
            lookingAtBlockPos = getLookingAtBlockCoordsNotNull(debug);
            if (blockPos.left().equalsDoubleArray(lookingAtBlockPos)) {
                placeBlock();
                return true;
            } else {
                pointAtPos(blockPos.right(0.5));
                debug = getDebug(getGameScreen());
                lookingAtBlockPos = getLookingAtBlockCoordsNotNull(debug);
                if (blockPos.right().equalsDoubleArray(lookingAtBlockPos)) {
                    placeBlock();
                    return true;
                } else {
                    pointAtPos(blockPos.forward(0.5));
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
        if (debuggingMode) writeChat(in);
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

        if (!moveToBlockRough(x, z, timeout)) return false;


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

            TimeUnit.MILLISECONDS.sleep(20);

            debug = getDebug(getGameScreen());
            playerPos = getPlayerPos(debug);
            diffX = playerPos[0] - x;
            diffZ = playerPos[2] - z;

            if (System.currentTimeMillis() > end) break;
        }
        /*while (Math.abs(diffX) > 0.2 || Math.abs(diffZ) > 0.2) {

            if (diffX < -0.2) {
                robot.keyPress(keyXPlus);
                keyXPlusPressed = true;
            } else if (keyXPlusPressed) {
                robot.keyRelease(keyXPlus);
            }
            if (diffX > 0.2) {
                robot.keyPress(keyXMinus);
                keyXMinusPressed = true;
            } else if (keyXMinusPressed) {
                robot.keyRelease(keyXMinus);
            }
            if (diffZ < -0.2) {
                robot.keyPress(keyZPlus);
                keyZPlusPressed = true;
            } else if (keyZPlusPressed) {
                robot.keyRelease(keyZPlus);
            }
            if (diffZ > 0.2) {
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
        }*/
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


        while (Math.abs(diffX) > 0.2 || Math.abs(diffZ) > 0.2) {

            if (diffX < -0.2) {
                robot.keyPress(keyXPlus);
                keyXPlusPressed = true;
            } else if (keyXPlusPressed) {
                robot.keyRelease(keyXPlus);
            }
            if (diffX > 0.2) {
                robot.keyPress(keyXMinus);
                keyXMinusPressed = true;
            } else if (keyXMinusPressed) {
                robot.keyRelease(keyXMinus);
            }
            if (diffZ < -0.2) {
                robot.keyPress(keyZPlus);
                keyZPlusPressed = true;
            } else if (keyZPlusPressed) {
                robot.keyRelease(keyZPlus);
            }
            if (diffZ > 0.2) {
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
    static void retreatAndBlock(int dir) throws InterruptedException {
        // goes back one block in tunneling direction and blocks tunnel in front of it
        String debug = getDebug(getGameScreen());
        faceDirection(dir);
        BlockPosWithDirection barrierPos = new BlockPosWithDirection(debug);
        robot.keyPress(KeyEvent.VK_S);
        TimeUnit.MILLISECONDS.sleep(600);
        robot.keyRelease(KeyEvent.VK_S);
        pointAtPos(barrierPos.down(0.5));
        placeBlock();
        pointAtPos(barrierPos.up(0.5));
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
        /*
        result = new BufferedImage(textboxWidth / fontPixelSize, (textboxHeight / fontPixelSize) + 9, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < result.getWidth(); x++) {
            for (int y = result.getHeight() - 9; y < result.getHeight(); y++) {
                result.setRGB(x, y, Color.WHITE.getRGB());
            }
        }

        for (int x = textboxX; x < textboxX + textboxWidth; x++) {
            for (int y = textboxY; y < textboxY + textboxHeight; y++) {
                if ((x - 2) % fontPixelSize == 0 && (y - 1) % fontPixelSize == 0) {
                    color = new Color(img.getRGB(x, y));
                    if (color.getRed() == 84 && color.getGreen() == 84 && color.getBlue() == 84){
                        result.setRGB((x - 2 - textboxX) / fontPixelSize, (y - 1 - textboxY) / fontPixelSize, Color.BLACK.getRGB());
                    } else {
                        result.setRGB((x - 2 - textboxX) / fontPixelSize, (y - 1 - textboxY) / fontPixelSize, Color.WHITE.getRGB());

                    }
                }
            }
        }
        resultString += " " + FontTranslator.translate(result).trim();*/
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
        System.out.println("x: "+ mapX+" z: "+mapZ + " game: x: " + (int) Math.floor(pos[0]) + " z: " + (int) Math.floor(pos[2]));
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
        System.out.println("x: "+ mapX+" z: "+mapZ + " game: x: " + (int) Math.floor(pos[0]) + " z: " + (int) Math.floor(pos[2]));
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
                return img;
            } else {
                if (firstRun) {
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
    static void pointAtOld(double targetYaw, double targetPitch) throws InterruptedException {
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        int mouseX = mouseLocation.x;
        int mouseY = mouseLocation.y;
        while (true) {
            double[] facing = getFacingFromRobot();
            double yawDiff = targetYaw - facing[0];
            double pitchDiff = targetPitch - facing[1];
            if (yawDiff < -180) {
                yawDiff +=360;
            }
            if (yawDiff > 180) {
                yawDiff -=360;
            }
            if (yawDiff > 10) {
                robot.mouseMove(mouseX + 50, mouseY);
            } else if (yawDiff < -10) {
                robot.mouseMove(mouseX - 50, mouseY);
            } else if (yawDiff > 1) {
                robot.mouseMove(mouseX + 10, mouseY);
            } else if (yawDiff < -1) {
                robot.mouseMove(mouseX - 10, mouseY);
            } else if (yawDiff > 0) {
                robot.mouseMove(mouseX + 1, mouseY);
            } else if (yawDiff < -0) {
                robot.mouseMove(mouseX - 1, mouseY);
            } else if (pitchDiff > 10) {
                robot.mouseMove(mouseX, mouseY + 50);
            } else if (pitchDiff < -10) {
                robot.mouseMove(mouseX, mouseY - 50);
            } else if (pitchDiff > 1) {
                robot.mouseMove(mouseX, mouseY + 10);
            } else if (pitchDiff < -1) {
                robot.mouseMove(mouseX, mouseY - 10);
            } else if (pitchDiff > 0) {
                robot.mouseMove(mouseX, mouseY + 1);
            } else if (pitchDiff < -0) {
                robot.mouseMove(mouseX, mouseY - 1);
            } else {
//                System.out.println(yawDiff);
//                System.out.println(pitchDiff);
                break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
    static String scanUpperHalf() throws InterruptedException {
        /*this function will scan the now visible blocks that have emerged after the upper block in a tunnel is mined from a position directly in front of it.
        * it will also mine falling blocks if they have gotten in the way.
        * return values shall be as follows
        * stone     if there is just stone, andesite, granite and dirt
        * air       if at least one of the blocks is air. same response as fluid
        * fluid     if a fluid is detected. this shall be responded to with replacing the mined block and maneuvering around the spot
        * tunnel    if an old tunnel marked on the map is uncovered.
        * falling   if the mined block is replaced by dirt or gravel from above
        * ore       if one of the new blocks is ore. mining and re-locating of ore will be done after the second block is mined
        * monster   if an entity is detected. fallingsand and items are not visible through the debug screen
        * infested  if silverfish infested stone is detected
        * unexpected if something unexpected happens
        *
        * if multiple returns are possible this is the priority in descending order:
        * monster
        * fluid
        * tunnel
        * air
        * infested
        * ore
        * stone
        * */
        boolean ore = false;
        boolean fallingsand = false;
        String debug = getDebug(getGameScreen());
        if (debug.contains("Targeted Entity")) return "monster";
        if (!getLookingAtFluid(debug).equals("minecraft:empty")) return "fluid";

        pointAt(180 + yawDiff, 17);
        debug = getDebug(getGameScreen());
        String block = getLookingAtBlock(debug);
        double[] blockPos = getLookingAtBlockCoords(debug);
        double[] playerPos = getPlayerPosFloored(debug);
        if (debug.contains("Targeted Entity")) return "monster";
        if (!getLookingAtFluid(debug).equals("minecraft:empty")) return "fluid";
        double[] targetedBlockPos = playerPos.clone();
        double[] minedBlockPos = playerPos.clone();
        targetedBlockPos[1] += 1;
        minedBlockPos[1] += 1;
        if (debug.contains("positive X")) {
            targetedBlockPos[0] += 2;
            minedBlockPos[0] += 1;
        } else if (debug.contains("negative X")) {
            targetedBlockPos[0] += -2;
            minedBlockPos[0] += -1;
        } else if (debug.contains("positive Z")) {
            targetedBlockPos[2] += 2;
            minedBlockPos[2] += 1;
        } else if (debug.contains("negative Z")) {
            targetedBlockPos[2] += -2;
            minedBlockPos[2] += -1;
        }
//        mapColorPos(minedBlockPos, Color.BLUE);
        if (!Arrays.equals(targetedBlockPos, blockPos)) {
            //writeChat(Arrays.toString(playerPos) + Arrays.toString(minedBlockPos) + Arrays.toString(blockPos));
            if (Arrays.equals(minedBlockPos, blockPos)) {
                writeChat("fallingSand");
                fallingsand = true;
                pointAt(180 + yawDiff, 45);
                breakFallingStack();
                TimeUnit.SECONDS.sleep(5);
                pointAt(180 + yawDiff, 17);
                debug = getDebug(getGameScreen());
                block = getLookingAtBlock(debug);
                blockPos = getLookingAtBlockCoords(debug);
                if (debug.contains("Targeted Entity")) return "monster";
                if (!getLookingAtFluid(debug).equals("minecraft:empty")) return "fluid";
                if (block.equals("minecraft:torch")) leftClick();
                TimeUnit.MILLISECONDS.sleep(100);
                debug = getDebug(getGameScreen());
                block = getLookingAtBlock(debug);
                blockPos = getLookingAtBlockCoords(debug);
                if (debug.contains("Targeted Entity")) return "monster";
                if (!getLookingAtFluid(debug).equals("minecraft:empty")) return "fluid";
            }
            if (!Arrays.equals(targetedBlockPos, blockPos)) {
                writeChat("air");
                if (mapGetColor(targetedBlockPos) == black) {
                    return "tunnel";
                } else {
                    mapColorPos(targetedBlockPos, Color.RED);
                }
                return "air";
            }
        }
        switch (block) {
            case "minecraft:infested_stone":
                mapColorPos(blockPos, Color.ORANGE);
                return "infested";
            case "minecraft:emerald_ore":
            case "minecraft:gold_ore":
            case "minecraft:iron_ore":
            case "minecraft:coal_ore":
            case "minecraft:diamond_ore":
            case "minecraft:redstone_ore":
            case "minecraft:lapis_ore":
                ore = true;
                mapColorPos(blockPos, Color.CYAN);
                break;
            case "minecraft:cobblestone":
            case "minecraft:stone":
            case "minecraft:diorite":
            case "minecraft:granite":
            case "minecraft:andesite":
            case "minecraft:dirt":
            case "minecraft:sand":
            case "minecraft:gravel":
                mapColorPos(blockPos, Color.GRAY);
            break;
            default:
                mapColorPos(blockPos, Color.BLUE);
                writeChat(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                System.out.println(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                break;
        }

        // left block


        pointAt(150 + yawDiff, 17);
        debug = getDebug(getGameScreen());
        block = getLookingAtBlock(debug);
        blockPos = getLookingAtBlockCoords(debug);
        playerPos = getPlayerPosFloored(debug);
        if (debug.contains("Targeted Entity")) return "monster";
        if (!getLookingAtFluid(debug).equals("minecraft:empty")) return "fluid";
        targetedBlockPos = playerPos.clone();
        minedBlockPos = playerPos.clone();
        targetedBlockPos[1] += 1;
        minedBlockPos[1] += 1;
        if (debug.contains("positive X")) {
            targetedBlockPos[0] += 1;
            targetedBlockPos[2] += -1;
            minedBlockPos[0] += 1;
        } else if (debug.contains("negative X")) {
            targetedBlockPos[0] += -1;
            targetedBlockPos[2] += 1;
            minedBlockPos[0] += -1;
        } else if (debug.contains("positive Z")) {
            targetedBlockPos[0] += 1;
            targetedBlockPos[2] += 1;
            minedBlockPos[2] += 1;
        } else if (debug.contains("negative Z")) {
            targetedBlockPos[0] += -1;
            targetedBlockPos[2] += -1;
            minedBlockPos[2] += -1;
        }
//        mapColorPos(minedBlockPos, Color.BLUE);
        if (!Arrays.equals(targetedBlockPos, blockPos)) {
            if (Arrays.equals(minedBlockPos, blockPos)) {
                writeChat("fallingSand");
                fallingsand = true;
                pointAt(180 + yawDiff, 45);
                breakFallingStack();
                TimeUnit.SECONDS.sleep(5);
                pointAt(150 + yawDiff, 17);
                debug = getDebug(getGameScreen());
                block = getLookingAtBlock(debug);
                blockPos = getLookingAtBlockCoords(debug);
                if (debug.contains("Targeted Entity")) return "monster";
                if (!getLookingAtFluid(debug).equals("minecraft:empty")) return "fluid";
                if (block.equals("minecraft:torch")) leftClick();
                TimeUnit.MILLISECONDS.sleep(100);
                debug = getDebug(getGameScreen());
                block = getLookingAtBlock(debug);
                blockPos = getLookingAtBlockCoords(debug);
                if (debug.contains("Targeted Entity")) return "monster";
                if (!getLookingAtFluid(debug).equals("minecraft:empty")) return "fluid";
            }
            if (!Arrays.equals(targetedBlockPos, blockPos)) {
                writeChat("air");
                if (mapGetColor(targetedBlockPos) == black) {
                    return "tunnel";
                } else {
                    mapColorPos(targetedBlockPos, Color.RED);
                }
                return "air";
            }

        }
        switch (block) {
            case "minecraft:infested_stone":
                mapColorPos(blockPos, Color.ORANGE);
                return "infested";
            case "minecraft:emerald_ore":
            case "minecraft:gold_ore":
            case "minecraft:iron_ore":
            case "minecraft:coal_ore":
            case "minecraft:diamond_ore":
            case "minecraft:redstone_ore":
            case "minecraft:lapis_ore":
                ore = true;
                mapColorPos(blockPos, Color.CYAN);
                break;
            case "minecraft:cobblestone":
            case "minecraft:stone":
            case "minecraft:diorite":
            case "minecraft:granite":
            case "minecraft:andesite":
                case "minecraft:dirt":
                case "minecraft:sand":
                case "minecraft:gravel":
                    mapColorPos(blockPos, Color.GRAY);
                break;
            default:
                mapColorPos(blockPos, Color.BLUE);
                writeChat(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure or i missed a usual underground block");
                System.out.println(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                break;
        }

        // right block


        pointAt(-150 + yawDiff, 17);
        debug = getDebug(getGameScreen());
        block = getLookingAtBlock(debug);
        blockPos = getLookingAtBlockCoords(debug);
        playerPos = getPlayerPosFloored(debug);
        if (debug.contains("Targeted Entity")) return "monster";
        if (!getLookingAtFluid(debug).equals("minecraft:empty")) return "fluid";
        targetedBlockPos = playerPos.clone();
        minedBlockPos = playerPos.clone();
        targetedBlockPos[1] += 1;
        minedBlockPos[1] += 1;
        if (debug.contains("positive X")) {
            targetedBlockPos[0] += 1;
            targetedBlockPos[2] += 1;
            minedBlockPos[0] += 1;
        } else if (debug.contains("negative X")) {
            targetedBlockPos[0] += -1;
            targetedBlockPos[2] += -1;
            minedBlockPos[0] += -1;
        } else if (debug.contains("positive Z")) {
            targetedBlockPos[0] += -1;
            targetedBlockPos[2] += 1;
            minedBlockPos[2] += 1;
        } else if (debug.contains("negative Z")) {
            targetedBlockPos[0] += 1;
            targetedBlockPos[2] += -1;
            minedBlockPos[2] += -1;
        }
//        mapColorPos(minedBlockPos, Color.BLUE);
        if (!Arrays.equals(targetedBlockPos, blockPos)) {
            if (Arrays.equals(minedBlockPos, blockPos)) {
                writeChat("fallingSand");
                fallingsand = true;
                pointAt(180 + yawDiff, 45);
                breakFallingStack();
                TimeUnit.SECONDS.sleep(5);
                pointAt(-150 + yawDiff, 17);
                debug = getDebug(getGameScreen());
                block = getLookingAtBlock(debug);
                blockPos = getLookingAtBlockCoords(debug);
                if (debug.contains("Targeted Entity")) return "monster";
                if (!getLookingAtFluid(debug).equals("minecraft:empty")) return "fluid";
                if (block.equals("minecraft:torch")) leftClick();
                TimeUnit.MILLISECONDS.sleep(100);
                debug = getDebug(getGameScreen());
                block = getLookingAtBlock(debug);
                blockPos = getLookingAtBlockCoords(debug);
                if (debug.contains("Targeted Entity")) return "monster";
                if (!getLookingAtFluid(debug).equals("minecraft:empty")) return "fluid";
            }
            if (!Arrays.equals(targetedBlockPos, blockPos)) {
                writeChat("air");
                if (mapGetColor(targetedBlockPos) == black) {
                    return "tunnel";
                } else {
                    mapColorPos(targetedBlockPos, Color.RED);
                }
                return "air";
            }
        }

        switch (block) {
            case "minecraft:infested_stone":
                mapColorPos(blockPos, Color.ORANGE);
                return "infested";
            case "minecraft:emerald_ore":
            case "minecraft:gold_ore":
            case "minecraft:iron_ore":
            case "minecraft:coal_ore":
            case "minecraft:diamond_ore":
            case "minecraft:redstone_ore":
            case "minecraft:lapis_ore":
                ore = true;
                mapColorPos(blockPos, Color.CYAN);
                break;
            case "minecraft:cobblestone":
            case "minecraft:stone":
            case "minecraft:diorite":
            case "minecraft:granite":
            case "minecraft:andesite":
                case "minecraft:dirt":
                case "minecraft:sand":
                case "minecraft:gravel":
                    mapColorPos(blockPos, Color.GRAY);
                break;
            default:
                mapColorPos(blockPos, Color.BLUE);
                writeChat(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure or i missed a usual underground block");
                System.out.println(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                break;
        }


        // top block


        if (!fallingsand) {
            pointAt(180 + yawDiff, -25);
            debug = getDebug(getGameScreen());
            block = getLookingAtBlock(debug);
            blockPos = getLookingAtBlockCoords(debug);
            playerPos = getPlayerPosFloored(debug);
            if (debug.contains("Targeted Entity")) return "monster";
            if (!getLookingAtFluid(debug).equals("minecraft:empty")) return "fluid";
            targetedBlockPos = playerPos.clone();
            minedBlockPos = playerPos.clone();
            targetedBlockPos[1] += 2;
            minedBlockPos[1] += 1;
            if (debug.contains("positive X")) {
                targetedBlockPos[0] += 1;
                minedBlockPos[0] += 1;
            } else if (debug.contains("negative X")) {
                targetedBlockPos[0] += -1;
                minedBlockPos[0] += -1;
            } else if (debug.contains("positive Z")) {
                targetedBlockPos[2] += 1;
                minedBlockPos[2] += 1;
            } else if (debug.contains("negative Z")) {
                targetedBlockPos[2] += -1;
                minedBlockPos[2] += -1;
            }
//        mapColorPos(minedBlockPos, Color.BLUE);
            if (!Arrays.equals(targetedBlockPos, blockPos)) {
                //writeChat(Arrays.toString(playerPos) + Arrays.toString(minedBlockPos) + Arrays.toString(blockPos));
                writeChat("air");
                if (mapGetColor(targetedBlockPos) == black) {
                    return "tunnel";
                } else {
                    mapColorPos(targetedBlockPos, Color.RED);
                }
                return "air";

            }
            switch (block) {
                case "minecraft:infested_stone":
                    mapColorPos(blockPos, Color.ORANGE);
                    return "infested";
                case "minecraft:emerald_ore":
                case "minecraft:gold_ore":
                case "minecraft:iron_ore":
                case "minecraft:coal_ore":
                case "minecraft:diamond_ore":
                case "minecraft:redstone_ore":
                case "minecraft:lapis_ore":
                    ore = true;
                    mapColorPos(blockPos, Color.MAGENTA);
                    break;
                case "minecraft:cobblestone":
                case "minecraft:stone":
                case "minecraft:diorite":
                case "minecraft:granite":
                case "minecraft:andesite":
                case "minecraft:dirt":
                case "minecraft:sand":
                case "minecraft:gravel":
                    break;
                default:
                    mapColorPos(blockPos, Color.BLUE);
                    writeChat(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure or i missed a usual underground block");
                    System.out.println(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                    break;
            }
        }


        // done checking blocks

        if (ore) return "ore";

        return "stone";
    }
    static String scanLowerHalf() throws InterruptedException {

        boolean ore = false;
//        boolean fallingsand = false;
        String debug = getDebug(getGameScreen());
        if (debug.contains("Targeted Entity")) return "monster";
        if (!getLookingAtFluid(debug).equals("minecraft:empty")) return "fluid";

        pointAt(180 + yawDiff, 42);
        debug = getDebug(getGameScreen());
        String block = getLookingAtBlock(debug);
        double[] blockPos = getLookingAtBlockCoords(debug);
        double[] playerPos = getPlayerPosFloored(debug);
        if (debug.contains("Targeted Entity")) return "monster";
        if (!getLookingAtFluid(debug).equals("minecraft:empty")) return "fluid";
        double[] targetedBlockPos = playerPos.clone();
        double[] minedBlockPos = playerPos.clone();
        if (debug.contains("positive X")) {
            targetedBlockPos[0] += 2;
            minedBlockPos[0] += 1;
        } else if (debug.contains("negative X")) {
            targetedBlockPos[0] += -2;
            minedBlockPos[0] += -1;
        } else if (debug.contains("positive Z")) {
            targetedBlockPos[2] += 2;
            minedBlockPos[2] += 1;
        } else if (debug.contains("negative Z")) {
            targetedBlockPos[2] += -2;
            minedBlockPos[2] += -1;
        }
//        mapColorPos(minedBlockPos, Color.BLUE);
        if (!Arrays.equals(targetedBlockPos, blockPos)) {
            //writeChat(Arrays.toString(playerPos) + Arrays.toString(minedBlockPos) + Arrays.toString(blockPos));
            if (Arrays.equals(minedBlockPos, blockPos)) {
                writeChat("somethig unexpected happened");
                return "unexpected";
            }
            if (!Arrays.equals(targetedBlockPos, blockPos)) {
                writeChat("air");
                if (mapGetColor(targetedBlockPos) == black) {
                    return "tunnel";
                } else {
                    mapColorPos(targetedBlockPos, Color.RED);
                }
                return "air";
            }
        }
        switch (block) {
            case "minecraft:infested_stone":
                mapColorPos(blockPos, Color.ORANGE);
                return "infested";
            case "minecraft:emerald_ore":
            case "minecraft:gold_ore":
            case "minecraft:iron_ore":
            case "minecraft:coal_ore":
            case "minecraft:diamond_ore":
            case "minecraft:redstone_ore":
            case "minecraft:lapis_ore":
                ore = true;
                mapColorPos(blockPos, Color.CYAN);
                break;
            case "minecraft:cobblestone":
            case "minecraft:stone":
            case "minecraft:diorite":
            case "minecraft:granite":
            case "minecraft:andesite":
            case "minecraft:dirt":
            case "minecraft:sand":
            case "minecraft:gravel":
                mapColorPos(blockPos, Color.GRAY);
                break;
            default:
                mapColorPos(blockPos, Color.BLUE);
                writeChat(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                System.out.println(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                break;
        }

        // left block


        pointAt(150 + yawDiff, 42);
        debug = getDebug(getGameScreen());
        block = getLookingAtBlock(debug);
        blockPos = getLookingAtBlockCoords(debug);
        playerPos = getPlayerPosFloored(debug);
        if (debug.contains("Targeted Entity")) return "monster";
        if (!getLookingAtFluid(debug).equals("minecraft:empty")) return "fluid";
        targetedBlockPos = playerPos.clone();
        minedBlockPos = playerPos.clone();
        if (debug.contains("positive X")) {
            targetedBlockPos[0] += 1;
            targetedBlockPos[2] += -1;
            minedBlockPos[0] += 1;
        } else if (debug.contains("negative X")) {
            targetedBlockPos[0] += -1;
            targetedBlockPos[2] += 1;
            minedBlockPos[0] += -1;
        } else if (debug.contains("positive Z")) {
            targetedBlockPos[0] += 1;
            targetedBlockPos[2] += 1;
            minedBlockPos[2] += 1;
        } else if (debug.contains("negative Z")) {
            targetedBlockPos[0] += -1;
            targetedBlockPos[2] += -1;
            minedBlockPos[2] += -1;
        }
//        mapColorPos(minedBlockPos, Color.BLUE);
        if (!Arrays.equals(targetedBlockPos, blockPos)) {
            //writeChat(Arrays.toString(playerPos) + Arrays.toString(minedBlockPos) + Arrays.toString(blockPos));
            writeChat("air");
            if (mapGetColor(targetedBlockPos) == black) {
                return "tunnel";
            } else {
                mapColorPos(targetedBlockPos, Color.RED);
            }
            return "air";

        }
        switch (block) {
            case "minecraft:infested_stone":
                mapColorPos(blockPos, Color.ORANGE);
                return "infested";
            case "minecraft:emerald_ore":
            case "minecraft:gold_ore":
            case "minecraft:iron_ore":
            case "minecraft:coal_ore":
            case "minecraft:diamond_ore":
            case "minecraft:redstone_ore":
            case "minecraft:lapis_ore":
                ore = true;
                mapColorPos(blockPos, Color.CYAN);
                break;
            case "minecraft:cobblestone":
            case "minecraft:stone":
            case "minecraft:diorite":
            case "minecraft:granite":
            case "minecraft:andesite":
            case "minecraft:dirt":
            case "minecraft:sand":
            case "minecraft:gravel":
                mapColorPos(blockPos, Color.GRAY);
                break;
            default:
                mapColorPos(blockPos, Color.BLUE);
                writeChat(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure or i missed a usual underground block");
                System.out.println(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                break;
        }

        // right block


        pointAt(-150 + yawDiff, 42);
        debug = getDebug(getGameScreen());
        block = getLookingAtBlock(debug);
        blockPos = getLookingAtBlockCoords(debug);
        playerPos = getPlayerPosFloored(debug);
        if (debug.contains("Targeted Entity")) return "monster";
        if (!getLookingAtFluid(debug).equals("minecraft:empty")) return "fluid";
        targetedBlockPos = playerPos.clone();
        minedBlockPos = playerPos.clone();
        if (debug.contains("positive X")) {
            targetedBlockPos[0] += 1;
            targetedBlockPos[2] += 1;
            minedBlockPos[0] += 1;
        } else if (debug.contains("negative X")) {
            targetedBlockPos[0] += -1;
            targetedBlockPos[2] += -1;
            minedBlockPos[0] += -1;
        } else if (debug.contains("positive Z")) {
            targetedBlockPos[0] += -1;
            targetedBlockPos[2] += 1;
            minedBlockPos[2] += 1;
        } else if (debug.contains("negative Z")) {
            targetedBlockPos[0] += 1;
            targetedBlockPos[2] += -1;
            minedBlockPos[2] += -1;
        }
//        mapColorPos(minedBlockPos, Color.BLUE);
        if (!Arrays.equals(targetedBlockPos, blockPos)) {
            //writeChat(Arrays.toString(playerPos) + Arrays.toString(minedBlockPos) + Arrays.toString(blockPos));

            if (mapGetColor(targetedBlockPos) == black) {
                return "tunnel";
            } else {
                mapColorPos(targetedBlockPos, Color.RED);
            }
            return "air";

        }

        switch (block) {
            case "minecraft:infested_stone":
                mapColorPos(blockPos, Color.ORANGE);
                return "infested";
            case "minecraft:emerald_ore":
            case "minecraft:gold_ore":
            case "minecraft:iron_ore":
            case "minecraft:coal_ore":
            case "minecraft:diamond_ore":
            case "minecraft:redstone_ore":
            case "minecraft:lapis_ore":
                ore = true;
                mapColorPos(blockPos, Color.CYAN);
                break;
            case "minecraft:cobblestone":
            case "minecraft:stone":
            case "minecraft:diorite":
            case "minecraft:granite":
            case "minecraft:andesite":
            case "minecraft:dirt":
            case "minecraft:sand":
            case "minecraft:gravel":
                mapColorPos(blockPos, Color.GRAY);
                break;
            default:
                mapColorPos(blockPos, Color.BLUE);
                writeChat(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure or i missed a usual underground block");
                System.out.println(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                break;
        }


        // bottom block


        pointAt(180 + yawDiff, 60);
        debug = getDebug(getGameScreen());
        block = getLookingAtBlock(debug);
        blockPos = getLookingAtBlockCoords(debug);
        playerPos = getPlayerPosFloored(debug);
        if (debug.contains("Targeted Entity")) return "monster";
        if (!getLookingAtFluid(debug).equals("minecraft:empty")) return "fluid";
        targetedBlockPos = playerPos.clone();
        minedBlockPos = playerPos.clone();
        targetedBlockPos[1] += -1;
        minedBlockPos[1] += 1;
        if (debug.contains("positive X")) {
            targetedBlockPos[0] += 1;
            minedBlockPos[0] += 1;
        } else if (debug.contains("negative X")) {
            targetedBlockPos[0] += -1;
            minedBlockPos[0] += -1;
        } else if (debug.contains("positive Z")) {
            targetedBlockPos[2] += 1;
            minedBlockPos[2] += 1;
        } else if (debug.contains("negative Z")) {
            targetedBlockPos[2] += -1;
            minedBlockPos[2] += -1;
        }
//        mapColorPos(minedBlockPos, Color.BLUE);
        if (!Arrays.equals(targetedBlockPos, blockPos)) {
            //writeChat(Arrays.toString(playerPos) + Arrays.toString(minedBlockPos) + Arrays.toString(blockPos));
            writeChat("air");
            if (mapGetColor(targetedBlockPos) == black) {
                return "tunnel";
            } else {
                mapColorPos(targetedBlockPos, Color.RED);
            }
            return "air";

        }
        switch (block) {
            case "minecraft:infested_stone":
                mapColorPos(blockPos, Color.ORANGE);
                return "infested";
            case "minecraft:emerald_ore":
            case "minecraft:gold_ore":
            case "minecraft:iron_ore":
            case "minecraft:coal_ore":
            case "minecraft:diamond_ore":
            case "minecraft:redstone_ore":
            case "minecraft:lapis_ore":
                ore = true;
                if (mapGetColor(blockPos) == Color.MAGENTA.getRGB()) {
                    mapColorPos(blockPos, Color.YELLOW);
                } else {
                    mapColorPos(blockPos, Color.PINK);
                }
                break;
            case "minecraft:cobblestone":
            case "minecraft:stone":
            case "minecraft:diorite":
            case "minecraft:granite":
            case "minecraft:andesite":
            case "minecraft:dirt":
            case "minecraft:sand":
            case "minecraft:gravel":
                break;
            default:
                mapColorPos(blockPos, Color.BLUE);
                writeChat(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure or i missed a usual underground block");
                System.out.println(block + " at position " + Arrays.toString(blockPos) + " this is probably a structure.");
                break;
        }



        // done checking blocks

        if (ore) return "ore";

        mapColorPos(minedBlockPos, Color.BLACK);
        return "stone";
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
