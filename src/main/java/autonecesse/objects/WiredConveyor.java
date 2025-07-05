package autonecesse.objects;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.*;

public class WiredConveyor extends Conveyor {
    public WiredConveyor(int movePotency, Color mapColor) {
        super(movePotency, mapColor);
    }

    public boolean isWireActive(Level level, int tileX, int tileY) {
        return level.wireManager.isWireActiveAny(tileX, tileY);
    }

    public boolean inverted(Level level, int tileX, int tileY) {
        return isWireActive(level, tileX, tileY);
    }

    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "wiredconveyortip"));
        return tooltips;
    }
}
