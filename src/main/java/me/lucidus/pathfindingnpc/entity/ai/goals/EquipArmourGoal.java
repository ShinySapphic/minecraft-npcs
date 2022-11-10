package me.lucidus.pathfindingnpc.entity.ai.goals;

import com.google.common.collect.Sets;
import me.lucidus.pathfindingnpc.entity.PathfindingNPC;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.Set;

public class EquipArmourGoal extends Goal {
    protected final PathfindingNPC npc;
    private final Set<String> armorTypes = Sets.newHashSet("_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS");

    private PlayerInventory inv;
    private String armor;

    public EquipArmourGoal(PathfindingNPC npc) {
        this.npc = npc;
    }

    public boolean canUse() {
        this.inv = npc.getPlayerInventory();
        stringLoop:
        for (String armor : armorTypes) {
            if (!Arrays.toString(inv.getContents()).contains(armor))
                continue;
            for (ItemStack armorContents : inv.getArmorContents()) {
                if (armorContents != null) {
                    if (armorContents.getType().toString().contains(armor)) //TODO check armor stats and equip if better armor
                        continue stringLoop;
                }
            }
            this.armor = armor;
        }
        return armor != null;
    }

    public void start() {
        if (inv.getItemInMainHand().toString().contains(armor) || inv.getItemInOffHand().toString().contains(armor)) {
            boolean offHand = inv.getItemInOffHand().toString().contains(armor);
            npc.useItem(offHand, true);
        } else {
            int slot = 0;

            for (Material mat : Material.values()) {
                if (!mat.toString().contains(armor))
                    continue;
                if (inv.first(mat) == -1)
                    continue;
                slot = inv.first(mat);
            }
            if (slot == 0)
                return;
            ItemStack temp;

            switch (armor) {
                case "_HELMET":
                    temp = inv.getHelmet();
                    inv.setHelmet(inv.getItem(slot));
                    break;
                case "_CHESTPLATE":
                    temp = inv.getChestplate();
                    inv.setChestplate(inv.getItem(slot));
                    break;
                case "_LEGGINGS":
                    temp = inv.getLeggings();
                    inv.setLeggings(inv.getItem(slot));
                    break;
                case "_BOOTS":
                    temp = inv.getBoots();
                    inv.setBoots(inv.getItem(slot));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + armor);
            }
            inv.setItem(slot, temp);
        }
        armor = null;
    }
}
