package me.lucidus.pathfindingnpc.entity.ai.astar;

import me.lucidus.pathfindingnpc.NPCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AStarPath {

    private Node startNode;
    private Node targetNode;
    private final int maxNodes;

    private final Heap openSet;
    private final Set<Node> closedSet = new HashSet<>();

    private List<Node> path;

    public AStarPath(Location start, Location target, int maxNodes) {
        this.startNode = new Node(start.getBlock().getLocation().add(0.5, 0, 0.5));
        this.targetNode = new Node(target.getBlock().getLocation().add(0.5, 0, 0.5));
        this.maxNodes = maxNodes;
        this.openSet = new Heap(maxNodes);
    }

    public List<Node> getPath() {
        openSet.add(startNode);

        while (openSet.size() > 0) {
            Node current = openSet.removeFirst();
            closedSet.add(current);

            if (current.equals(targetNode)) {
                retracePath(startNode, targetNode);
                return path;
            }
            if (hasTooManyNodes()) {
                Bukkit.broadcastMessage("TOO MANY NODES");
                break;
            }

            for (Node neighbour : current.getNeighbours()) {
                if (closedSet.contains(neighbour))
                    continue;

                int newCostToNeighbour = current.gCost + current.getDistanceCost(neighbour) + neighbour.getInitialCost();

                if (newCostToNeighbour < neighbour.gCost || !openSet.contains(neighbour)) {
                    neighbour.gCost = newCostToNeighbour;
                    neighbour.hCost = neighbour.getDistanceCost(targetNode);
                    neighbour.parent = current;

                    if (neighbour.equals(targetNode))
                        targetNode = neighbour;
                    else if (neighbour.equals(startNode))
                        startNode = neighbour;

                    if (!openSet.contains(neighbour))
                        openSet.add(neighbour);
                    else
                        openSet.updateItem(neighbour);
                }
            }
        }
        return null;
    }

    public void retracePath(Node startNode, Node endNode) {
        List<Node> path = new ArrayList<>();
        Node currentNode = endNode;

        while (!currentNode.equals(startNode)) {
            path.add(currentNode);
            currentNode = currentNode.parent;
        }
        Collections.reverse(path);

        this.path = path;
    }

    public void displayPath() {
        if (path == null)
            return;

        new BukkitRunnable() {
            int time = 100;
            public void run() {
                if (time <= 0)
                    cancel();
                for (Node nodes : path)
                    nodes.location.getWorld().spawnParticle(Particle.REDSTONE, nodes.location, 1, new Particle.DustOptions(Color.fromRGB(0, 100, 255), 1));
                time--;
            }
        }.runTaskTimer(NPCPlugin.getPlugin(NPCPlugin.class), 0, 0);
    }

    public boolean hasTooManyNodes() {
        return openSet.size() + closedSet.size() > maxNodes;
    }
}
