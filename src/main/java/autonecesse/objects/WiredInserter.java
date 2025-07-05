package autonecesse.objects;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FueledProcessingInventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.pickup.PickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventorySlot;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.TorchObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WiredInserter extends Inserter {

    public WiredInserter(Color mapColor) {
        super(mapColor);
    }

    @Override
    public void tick(Level level, int tileX, int tileY) {
    }


    @Override
    public void onWireUpdate(Level level, int layerID, int tileX, int tileY, int wireID, boolean active) {
        super.onWireUpdate(level, layerID, tileX, tileY, wireID, active);
        if(active) tickInsertItem(level, tileX, tileY);
    }

    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "wiredinsertertip"));
        return tooltips;
    }
}
