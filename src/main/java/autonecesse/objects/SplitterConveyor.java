package autonecesse.objects;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.*;

public class SplitterConveyor extends Conveyor {
    public SplitterConveyor(int movePotency) {
        super(movePotency, new Color(233, 134, 39));
    }

    @Override
    public boolean inverted(Level level, int x, int y, ItemPickupEntity itemPickup) {
        return new GameRandom(itemPickup.getUniqueID()).getChance(0.5F);
    }

    @Override
    public boolean inverted(Level level, int x, int y, Mob mob) {
        long seed = mob.getUniqueID() + ((x & 0xFFFFFFFFL) << 32) | (y & 0xFFFFFFFFL);
        return new GameRandom(seed).getChance(0.5F);
    }

    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "splitterconveyortip"));
        return tooltips;
    }
}
