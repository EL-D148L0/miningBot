package max.sander;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Timeout {
    long end;
    int id;
    static ArrayList<Timeout> timeoutArrayList= new ArrayList<>();
    static int highestID = 0;
    static boolean paused = false;
    static long pauseStart;

    public Timeout(long end, int id) {
        this.end = end;
        this.id = id;
        timeoutArrayList.add(this);
    }

    static int findFreeID() {
        int newID = highestID + 1;
        highestID = newID;
        // overflow will not be a problem
        return newID;
    }

    public static int newTimeout(int duration) {
        //duration in millis
        int newID = findFreeID();
        long newEnd = System.currentTimeMillis() + duration;
        new Timeout(newEnd, newID);
        return newID;
    }

    public static void pauseTimeouts() {
        if (paused) {
            System.out.println("tried to pause while paused");
            return;
        }

        pauseStart = System.currentTimeMillis();
        paused = true;
    }

    public static void resumeTimeouts() {
        if (!paused) return;
        long pauseLength = System.currentTimeMillis() - pauseStart;

        for (Timeout t :
                timeoutArrayList) {
            t.end += pauseLength;
        }
        paused = false;

    }
    public static boolean hasExpired(int questionedID) throws HowDidThisHappenException {
        if (paused) throw new HowDidThisHappenException("timeouts are not supposed to be checked while time is paused");
        removeExpiredTimeouts();
        for (Timeout t:
             timeoutArrayList) {
            if (t.id == questionedID) {
                return false;
            }
        }
        return true;
    }
    static void removeExpiredTimeouts() {
        Queue<Timeout> queue = new LinkedList<>();
        for (Timeout t:
                timeoutArrayList) {
            if (System.currentTimeMillis() > t.end) {
                queue.add(t);
            }
        }

        while (queue.size() > 0) {
            timeoutArrayList.remove(queue.remove());
        }
    }
}
