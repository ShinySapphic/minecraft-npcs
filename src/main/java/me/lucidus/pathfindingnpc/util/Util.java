package me.lucidus.pathfindingnpc.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class Util {

    private static final ImmutableSet<BlockFace> ADJACENT_FACES = Sets.immutableEnumSet(
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.WEST,
            BlockFace.EAST,
            BlockFace.NORTH,
            BlockFace.SOUTH
    );

    public static boolean isBlockAdjacent(BlockFace face) {
        return ADJACENT_FACES.contains(face);
    }

    public static List<Block> getNearbyBlocks(Location loc, int radius) {
        List<Block> blocks = new ArrayList<>();
        for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++)
            for (int y = loc.getBlockY() - radius; y <= loc.getBlockY() + radius; y++)
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                    blocks.add(new Location(loc.getWorld(), x, y, z).getBlock());
                }
        return blocks;
    }

    public static List<Block> getPossibleStandingBlocks(Entity entity) {
        List<Block> blocks = new ArrayList<>();

        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                for (int y = -1; y <= 1; y++) {
                    if (i == 0 || j == 0)
                        continue;

                    double x = (entity.getBoundingBox().getWidthX() / 2) * i;
                    double z = (entity.getBoundingBox().getWidthZ() / 2) * j;

                    Block block = entity.getLocation().add(x, y, z).getBlock();

                    if (block.getType().isAir())
                        continue;
                    blocks.add(block);
                }
        return blocks;
    }

    public static Location getMidLocFromBlock(Block block) {
        Location loc = block.getBoundingBox().getCenter().toLocation(block.getWorld());
        if (block.getType().isAir())
            loc = block.getLocation().add(0.5, 0.5, 0.5);
        return loc;
    }

    public static Location getMidLocFromEntity(Entity entity) {
        return entity.getBoundingBox().getCenter().toLocation(entity.getWorld());
    }
}
