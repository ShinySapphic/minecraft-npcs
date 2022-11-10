package me.lucidus.pathfindingnpc.entity.ai;

import me.lucidus.pathfindingnpc.entity.PathfindingNPC;
import me.lucidus.pathfindingnpc.entity.ai.astar.AStarPath;
import me.lucidus.pathfindingnpc.entity.ai.astar.Node;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class Navigator {

    protected final PathfindingNPC npc;

    protected List<Node> path;
    protected Location targetLocation;
    protected Location lastNPCLocation;
    protected int currentNodeIndex;
    protected int giveUpTime;
    protected int lastCheckTime;

    private boolean stuck;

    public Navigator(PathfindingNPC npc) {
        this.npc = npc;
        this.lastCheckTime = 0;
        this.lastNPCLocation = npc.getLocation();
    }

    public void moveTo(Location location) {
        if (location != null)
            targetLocation = location;
        else
            targetLocation = npc.getLocation();

        AStarPath aStar = new AStarPath(npc.getLocation(), targetLocation, 500);
        if (aStar.hasTooManyNodes())
            return;

        currentNodeIndex = 0;
        path = aStar.getPath();
        aStar.displayPath();
    }

    public void stop() {
        targetLocation = null;
        stuck = false;
        lastCheckTime = 0;
        npc.setSprinting(false);
        npc.setZza(0);
        Bukkit.broadcastMessage("STOP");
    }

    public void tick() { //Everytime next node gets iterated, make sure node is still walkable, otherwise recalculate path.
        if (path == null)
            return;
        if (stuck) {
            stop();
            /*if (giveUpTime <= 0) {
                stop();
                return;
            } Bukkit.broadcastMessage(giveUpTime + " - Time before give up");
            giveUpTime--;*/
        } else if (!foundTarget()) {
            if (currentNodeIndex >= path.size())
                stop();
            else {
                checkIfStuck();
                if (stuck)
                    return;
                Location currentLoc = path.get(currentNodeIndex).location;
                npc.lookAt(currentLoc.clone().add(0, npc.getEyeHeight(), 0)); //TODO Make AI State so we can call this if not bridging. We don't always need bot looking towards node

                npc.setSprinting(true);
                npc.setZza(1);
                //npc.doJump(); TODO Calcuate when to jump when finding path
                if (currentLoc.getBlockY() > npc.getBlockY())
                    npc.doJump();

                if (!npc.getLocation().getWorld().equals(currentLoc.getWorld()))
                    return;
                if (npc.getLocation().distanceSquared(currentLoc) < 0.5)
                    currentNodeIndex++;
            }
        }
    }

    private void checkIfStuck() {
        if (!targetLocation.getWorld().equals(npc.getLocation().getWorld()))
            stuck = true;
        else if (lastCheckTime > 100) {
            if (!foundTarget() && npc.getLocation().distanceSquared(lastNPCLocation) < 1)
                stuck = true;
            lastNPCLocation = npc.getLocation();
            lastCheckTime = 0;
        }

        if (stuck)
            giveUpTime = 100;
        lastCheckTime++;
    }

    public Node getCurrentNode() {
        return path != null ? path.get(currentNodeIndex) : null;
    }

    public boolean foundTarget() {
        return targetLocation == null;
    }

    public double getTargetX() {
        return targetLocation.getX();
    }

    public double getTargetY() {
        return targetLocation.getY();
    }

    public double getTargetZ() {
        return targetLocation.getZ();
    }

    public boolean isStuck() { //Use this in other pathfinder goals to see if npc should attempt to build up or something
        return stuck;
    }

    public int getGiveUpTime() {
        return giveUpTime;
    }
}
