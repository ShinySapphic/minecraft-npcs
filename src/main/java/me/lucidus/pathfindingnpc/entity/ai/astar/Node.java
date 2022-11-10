package me.lucidus.pathfindingnpc.entity.ai.astar;

import me.lucidus.pathfindingnpc.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {

    public final Location location;

    public int gCost;
    public int hCost;
    public int heapIndex;
    public Node parent;

    private final int initialCost;

    public Node(Location location) {
        this.location = location;
        this.initialCost = 0;
    }

    public int fCost() {
        return gCost + hCost;
    }

    public List<Node> getNeighbours() {
         List<Node> neighbours = new ArrayList<>();
         for (int x = -1; x <= 1; x++)
             for (int y = -1; y <= 1; y++)
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0)
                        continue;

                    int checkY = location.getBlockY() + y;
                    if (checkY < 0 || checkY > 255)
                        continue;

                    Location neighbourLoc = location.clone().add(x, y, z);
                    if (!isWalkable(neighbourLoc))
                        continue;

                    BlockFace face = location.getBlock().getFace(neighbourLoc.getBlock());
                    if (!Util.isBlockAdjacent(face) && neighbourLoc.getY() == location.getY()) {
                        Block corner = neighbourLoc.clone().add(0, 1, 0).getBlock();
                        boolean cornerBlocked = false;

                        for (BlockFace faces : BlockFace.values()) {
                            if (!Util.isBlockAdjacent(faces))
                                continue;
                            if (corner.getRelative(faces).isPassable())
                                continue;
                            cornerBlocked = true;
                            break;
                        }
                        if (cornerBlocked)
                            continue;
                    }

                    neighbours.add(new Node(neighbourLoc));
                }
        return neighbours;
    }

    public int getDistanceCost(Node otherNode) {
        return (int) location.distance(otherNode.location);
    }

    // assign initial cost here instead of returning
    private boolean isWalkable(Location loc) {
        Location current = loc.clone().subtract(0, 1, 0);
        Material currentType = current.getBlock().getType();

        if (current.getBlock().isPassable() || currentType.equals(Material.CACTUS) || currentType.equals(Material.FIRE) || currentType.equals(Material.LAVA))
            return false;
        for (int i = 1; i <= 2; i++) {
            current = current.add(0, 1, 0);
            if (!current.getBlock().isPassable())
                return false;
        }
        return true;
    }

    public int getInitialCost() {
        return initialCost;
    }

    public int compareTo(Node otherNode) {
        if (this.fCost() < otherNode.fCost())
            return 1;
        else if (this.fCost() == otherNode.fCost()) {
            if (this.hCost < otherNode.hCost)
                return 1;
            return 0;
        }
        return -1;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Node node = (Node) obj;
        return Objects.equals(location, node.location);
    }

    public int hashCode() {
        return Objects.hash(location);
    }
}
