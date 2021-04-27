package max.sander;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static max.sander.Main.*;

public class Vein {
    ArrayList<BlockPos> seenBlocks;// blocks that are mined get removed from this list
    ArrayList<BlockPos> oreBlocks;
    ArrayList<BlockPos> minedBlocks;
    ArrayList<BlockPos> hazardBlockingBlocks;
    ArrayList<BlockPosWithHeight> floorPlan;// does not include spaces with height 1
    ArrayList<BlockPos> futureScanBlocks;
    ArrayList<BlockPos> ScanIgnoreBlocks;//blocks that shall be not scanned

    public Vein(ArrayList<BlockPos> seenBlocks, ArrayList<BlockPos> oreBlocks, ArrayList<BlockPos> minedBlocks, ArrayList<BlockPos> hazardBlockingBlocks) {
        this.seenBlocks = seenBlocks;
        this.oreBlocks = oreBlocks;
        this.minedBlocks = minedBlocks;
        this.hazardBlockingBlocks = hazardBlockingBlocks;
        this.floorPlan = getFloorPlan();
        this.futureScanBlocks = new ArrayList<BlockPos>();
        //System.out.println("aaaa" + getUnknownNeighbors(new BlockPos(45, 56, 86)));
    }

    public ArrayList<BlockPosWithHeight> getFloorPlan() {
        floorPlan = new ArrayList<>();
        for (BlockPos block : seenBlocks) {
            int height = 0;
            while (true) {
                if (minedBlocks.contains(block.up(height + 1))) {
                    height += 1;
                } else break;
            }
            if (height > 1) {
                floorPlan.add(new BlockPosWithHeight(block, height));
            }
        }
        return floorPlan;
    }
    public boolean mine() throws InterruptedException, HowDidThisHappenException, UnexpectedGameBehaviourException {
        // if this is at a different time than right after discovering ore in the tunnel weird things might happen.

        BlockPosWithHeight startPoint = getStartPoint();
        moveTo(startPoint);
        int startDirection = getStartDirection(startPoint);
        BlockPos toBeMined = new BlockPos(startPoint.up(2).move(startDirection));
        if (!minedBlocks.contains(toBeMined)) {
            int temp = pointMineScanReact(toBeMined);
        }
        return true;
    }
    private int pointMineScanReact(BlockPos target) throws InterruptedException, HowDidThisHappenException, UnexpectedGameBehaviourException {
        int response = pointAtBlockResponseCodes(target);
        if (response != ResponseCodes.OK) {
            if (response == ResponseCodes.NO_ROUTE) {
                return ResponseCodes.NO_ROUTE;
            } else {
                throw new HowDidThisHappenException("needs investigation");
            }
        }
        if (!mineBlockWithTool()) throw new HowDidThisHappenException("needs investigation");
        registerAsMined(target);
        response = scan(target);
        if (response == ResponseCodes.OK) {
            return ResponseCodes.OK;
        }
        //todo continue here. making placeBlock function now





        return ResponseCodes.OK;
    }
    private boolean placeBlock(BlockPos target) {
        ArrayList<BlockPos> solidNeighbors = getSolidNeighbors(target);//todo make function that checks stuff
        return true;
    }
    private void registerAsMined(BlockPos target) {
        seenBlocks.remove(target);
        oreBlocks.remove(target);
        futureScanBlocks.remove(target);
        minedBlocks.add(target);
    }
    private int scan(BlockPos minedBlock) throws InterruptedException, HowDidThisHappenException, UnexpectedGameBehaviourException {
        //possible returns: OK, FLUID, AIR, MONSTER
        ArrayList<BlockPos> unknownNeighbors = getUnknownNeighbors(minedBlock);
        for (BlockPos unknownBlock : unknownNeighbors) {
            int response = pointAtBlockResponseCodesSand(unknownBlock);
            if (response == ResponseCodes.NO_ROUTE) {
                futureScanBlocks.add(unknownBlock);
                continue;
            }
            if (response == ResponseCodes.OK) {
                //add to lists
                seenBlocks.add(unknownBlock);
                String debug = Main.getDebug(Main.getGameScreen());
                if (Util.arrayContainsString(BlockTypes.ORES, Main.getLookingAtBlock(debug))) {
                    oreBlocks.add(unknownBlock);
                }
                continue;
            }
            return response;

        }
        return ResponseCodes.OK;
    }
    private boolean pointAtBlock(BlockPos target) throws InterruptedException {
        //todo so far not tested
        String debug = Main.getDebug(Main.getGameScreen());

        double targetYaw;
        double targetPitch;
        double eyeHeight = 1.62;
        double[] eyePos = Main.getPlayerPos(debug);
        eyePos[1] += eyeHeight;
        ArrayList<BlockPos> possibleObstacles = getPossibleObstacles(target, eyePos);
        BlockPos pointingTarget = ViewCalculations.obstructedTargetPoint(target, eyePos, possibleObstacles);
        if (pointingTarget == null) return false;
        Direction dir = new Direction(eyePos, pointingTarget);
        targetYaw = dir.getYaw();
        targetPitch = dir.getPitch();


        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        int mouseX = mouseLocation.x;
        int mouseY = mouseLocation.y;
        double[] facing;
        double yawDiff;
        double pitchDiff;
        while (true) {
            debug = Main.getDebug(Main.getGameScreen());
            facing = Main.getFacing(debug);
            yawDiff = targetYaw - facing[0];
            pitchDiff = targetPitch - facing[1];
            if (yawDiff < -180) {
                yawDiff +=360;
            }
            if (yawDiff > 180) {
                yawDiff -=360;
            }
            if (target == Main.getLookingAtBlockPos(debug)) break;
            if (yawDiff == 0 && pitchDiff == 0) break;
            robot.mouseMove((int) (mouseX + Math.round(yawDiff*10)), (int) (mouseY + Math.round(pitchDiff*10)));
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private int pointAtBlockResponseCodesSand(BlockPos target) throws InterruptedException, UnexpectedGameBehaviourException, HowDidThisHappenException {
        //todo so far not tested

        String debug = Main.getDebug(Main.getGameScreen());

        double targetYaw;
        double targetPitch;
        double eyeHeight = 1.62;
        double[] eyePos = Main.getPlayerPos(debug);
        eyePos[1] += eyeHeight;
        ArrayList<BlockPos> possibleObstacles = getPossibleObstacles(target, eyePos);
        BlockPos pointingTarget = ViewCalculations.obstructedTargetPoint(target, eyePos, possibleObstacles);
        if (pointingTarget == null) return ResponseCodes.NO_ROUTE;
        Direction dir = new Direction(eyePos, pointingTarget);
        targetYaw = dir.getYaw();
        targetPitch = dir.getPitch();


        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        int mouseX = mouseLocation.x;
        int mouseY = mouseLocation.y;
        double[] facing;
        double yawDiff;
        double pitchDiff;
        while (true) {
            debug = Main.getDebug(Main.getGameScreen());
            facing = Main.getFacing(debug);
            yawDiff = targetYaw - facing[0];
            pitchDiff = targetPitch - facing[1];
            if (yawDiff < -180) {
                yawDiff +=360;
            }
            if (yawDiff > 180) {
                yawDiff -=360;
            }
            if (!Main.getLookingAtFluid(debug).equals("minecraft:empty")) return ResponseCodes.FLUID;
            if (target == Main.getLookingAtBlockPos(debug)) return ResponseCodes.OK;

            if (yawDiff == 0 && pitchDiff == 0) {
                SpaceLooper spaceLooper = new SpaceLooper(new BlockPos(eyePos), target);
                BlockPos blockPos = Main.getLookingAtBlockPos(debug);
                if (blockPos == null) {
                    return ResponseCodes.AIR;
                }
                if (spaceLooper.contains(blockPos)) {
                    if (!Util.arrayContainsString(BlockTypes.FALLING_BLOCKS, Main.getLookingAtBlock(debug))) throw new UnexpectedGameBehaviourException("well something unexpected happened");
                    int response = handleSand(blockPos);
                    if (response != ResponseCodes.AIR) return response;
                    // no fancy stonecutter tricks required. dig sand with shovel and count sands
                    //todo continue here idk what to put here. probably block it. or just nothing.
                }
                return ResponseCodes.AIR;
            }
            if (debug.contains("Targeted Entity")) return ResponseCodes.MONSTER;
            robot.mouseMove((int) (mouseX + Math.round(yawDiff*10)), (int) (mouseY + Math.round(pitchDiff*10)));
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private int handleSand(BlockPos sandPos) throws InterruptedException, HowDidThisHappenException {
        // is called when pointing in the right direction
        String debug;
        int amount = 0;
        BlockPos bottomSand = sandPos;
        BlockPos topMinedBlock = sandPos;
        if (minedBlocks.contains(bottomSand.down())) {
            bottomSand = bottomSand.down();
            if (minedBlocks.contains(bottomSand.down())) {
                throw new HowDidThisHappenException("this function is not made to dig sand out of holes");
            }
        }

        while (true) {
            if (minedBlocks.contains(topMinedBlock.up())) {
                topMinedBlock = topMinedBlock.up();
            } else break;
        }
        while (true) {
            int response = pointAtBlockResponseCodes(sandPos);
            if (response == ResponseCodes.OBSTACLE || response == ResponseCodes.NO_ROUTE) {
                throw new HowDidThisHappenException("this should be impossible");
            } else if (response == ResponseCodes.OK) {
                mineBlockWithTool();
                TimeUnit.MILLISECONDS.sleep(300);
                amount += 1;
                topMinedBlock = topMinedBlock.up();
                minedBlocks.add(topMinedBlock);
            } else if (response == ResponseCodes.AIR) {
                break;
            } else return response;
        }
        if (bottomSand != sandPos) {
            int response = pointAtBlockResponseCodes(sandPos);
            if (response == ResponseCodes.OBSTACLE || response == ResponseCodes.NO_ROUTE || response == ResponseCodes.AIR) {
                throw new HowDidThisHappenException("this should be impossible");
            } else if (response == ResponseCodes.OK) {
                mineBlockWithTool();
                TimeUnit.MILLISECONDS.sleep(300);
                amount += 1;
                topMinedBlock = topMinedBlock.up();
                minedBlocks.add(topMinedBlock);
            } else return response;
        }
        return ResponseCodes.OK;
    }
    private int pointAtBlockResponseCodes(BlockPos target) throws InterruptedException {
        //todo so far not tested

        String debug = Main.getDebug(Main.getGameScreen());

        double targetYaw;
        double targetPitch;
        double eyeHeight = 1.62;
        double[] eyePos = Main.getPlayerPos(debug);
        eyePos[1] += eyeHeight;
        ArrayList<BlockPos> possibleObstacles = getPossibleObstacles(target, eyePos);
        BlockPos pointingTarget = ViewCalculations.obstructedTargetPoint(target, eyePos, possibleObstacles);
        if (pointingTarget == null) return ResponseCodes.NO_ROUTE;
        Direction dir = new Direction(eyePos, pointingTarget);
        targetYaw = dir.getYaw();
        targetPitch = dir.getPitch();


        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        int mouseX = mouseLocation.x;
        int mouseY = mouseLocation.y;
        double[] facing;
        double yawDiff;
        double pitchDiff;
        while (true) {
            debug = Main.getDebug(Main.getGameScreen());
            facing = Main.getFacing(debug);
            yawDiff = targetYaw - facing[0];
            pitchDiff = targetPitch - facing[1];
            if (yawDiff < -180) {
                yawDiff +=360;
            }
            if (yawDiff > 180) {
                yawDiff -=360;
            }
            if (!Main.getLookingAtFluid(debug).equals("minecraft:empty")) return ResponseCodes.FLUID;
            if (target == Main.getLookingAtBlockPos(debug)) return ResponseCodes.OK;
            if (yawDiff == 0 && pitchDiff == 0) {
                BlockPos blockPos = Main.getLookingAtBlockPos(debug);
                if (blockPos == null) {
                    return ResponseCodes.AIR;
                }
                SpaceLooper spaceLooper = new SpaceLooper(new BlockPos(eyePos), target);
                if (spaceLooper.contains(blockPos)) {
                    return ResponseCodes.OBSTACLE;
                }
                return ResponseCodes.AIR;
            }
            if (debug.contains("Targeted Entity")) return ResponseCodes.MONSTER;
            robot.mouseMove((int) (mouseX + Math.round(yawDiff*10)), (int) (mouseY + Math.round(pitchDiff*10)));
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private ArrayList<BlockPos> getPossibleObstacles(BlockPos target, double[] eyePos) {
        ArrayList<BlockPos> possibleObstacles = new ArrayList<>();
        SpaceLooper spaceLooper = new SpaceLooper(new BlockPos(eyePos), target);
        for (BlockPos block : seenBlocks) {
            if (spaceLooper.contains(block)) {
                possibleObstacles.add(block);
            }
        }
        return possibleObstacles;
    }

    private ArrayList<BlockPos> getUnknownNeighbors(BlockPos pos) {
        ArrayList<BlockPos> out = new ArrayList<>();
        out.add(pos.up());
        out.add(pos.down());
        out.add(pos.addX(1));
        out.add(pos.addX(-1));
        out.add(pos.addZ(1));
        out.add(pos.addZ(-1));
        out.removeIf(block -> minedBlocks.contains(block) || seenBlocks.contains(block));
        return out;
    }
    private ArrayList<BlockPos> getSolidNeighbors(BlockPos pos) {
        ArrayList<BlockPos> out = new ArrayList<>();
        out.add(pos.up());
        out.add(pos.down());
        out.add(pos.addX(1));
        out.add(pos.addX(-1));
        out.add(pos.addZ(1));
        out.add(pos.addZ(-1));
        out.removeIf(block -> minedBlocks.contains(block) || !seenBlocks.contains(block));
        return out;
    }

    private int getStartDirection(BlockPosWithHeight startPoint) throws HowDidThisHappenException {
        BlockPos testPos = new BlockPos(startPoint);
        testPos = testPos.up(2).addX(1);
        if (minedBlocks.contains(testPos) && !minedBlocks.contains(testPos.down())) return Directions.POSITIVE_X;
        testPos = testPos.addX(-2);
        if (minedBlocks.contains(testPos) && !minedBlocks.contains(testPos.down())) return Directions.NEGATIVE_X;
        testPos = testPos.addX(1).addZ(1);
        if (minedBlocks.contains(testPos) && !minedBlocks.contains(testPos.down())) return Directions.POSITIVE_Z;
        testPos = testPos.addZ(-2);
        if (minedBlocks.contains(testPos) && !minedBlocks.contains(testPos.down())) return Directions.NEGATIVE_Z;
        if (oreBlocks.get(0).getX() > startPoint.getX()) {
            return Directions.POSITIVE_X;
        } else if (oreBlocks.get(0).getX() < startPoint.getX()) {
            return Directions.NEGATIVE_X;
        } else if (oreBlocks.get(0).getZ() > startPoint.getZ()) {
            return Directions.POSITIVE_Z;
        } else if (oreBlocks.get(0).getZ() < startPoint.getZ()) {
            return Directions.NEGATIVE_Z;
        }//if no return at this point the ore should either be above or under the player. the direction that is following the tunnel will be returned by the following code
        testPos = new BlockPos(startPoint);
        testPos = testPos.up(2).addX(1);
        if (minedBlocks.contains(testPos)) return Directions.NEGATIVE_X;
        testPos = testPos.addX(-2);
        if (minedBlocks.contains(testPos)) return Directions.POSITIVE_X;
        testPos = testPos.addX(1).addZ(1);
        if (minedBlocks.contains(testPos)) return Directions.NEGATIVE_Z;
        testPos = testPos.addZ(-2);
        if (minedBlocks.contains(testPos)) return Directions.POSITIVE_Z;

        throw new HowDidThisHappenException("i really have no idea how we got here. something must be wrong with the values that were passed into Vein");
    }

    private boolean moveTo(BlockPosWithHeight target) throws InterruptedException, HowDidThisHappenException {
        ArrayList<BlockPosWithHeight> path = getPath(target);
        if (path == null) {
            return false;
        }
        return Main.follow3dPath(path);
    }

    private ArrayList<BlockPosWithHeight> getPath(BlockPosWithHeight target) throws HowDidThisHappenException, InterruptedException {
        ArrayList<BlockPosWithHeight> path = Main.get3dPath(this.floorPlan, Main.getCurrentBPWH(this.floorPlan, Main.getDebug(Main.getGameScreen())), target);
        return path;
    }

    private BlockPosWithHeight getStartPoint() {
        this.floorPlan = getFloorPlan();
        BlockPosWithHeight startPoint = floorPlan.get(0);
        double distance = 100000;
        for (BlockPosWithHeight block : floorPlan) {
            double distanceNew = Main.distance3D(oreBlocks.get(0).toDoubleArray(), block.toDoubleArray());
            if (distanceNew < distance) {
                distance = distanceNew;
                startPoint = block;
            }
        }
        return startPoint;
    }
}
