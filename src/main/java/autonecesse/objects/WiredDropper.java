package autonecesse.objects;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.*;

public class WiredDropper extends Dropper {

    public WiredDropper(int movePotency, boolean wholeStack, Color mapColor) {
        super(movePotency, wholeStack, mapColor);
    }

    @Override
    public void tick(Level level, int tileX, int tileY) {
    }


    @Override
    public void onWireUpdate(Level level, int layerID, int tileX, int tileY, int wireID, boolean active) {
        super.onWireUpdate(level, layerID, tileX, tileY, wireID, active);
        if(active) tickDropItem(level, tileX, tileY);
    }

    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "wireddroppertip"));
        return tooltips;
    }
}
