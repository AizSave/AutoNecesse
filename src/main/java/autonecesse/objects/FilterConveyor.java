package autonecesse.objects;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventorySlot;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class FilterConveyor extends Conveyor {
    boolean inverted;

    public FilterConveyor(int movePotency, boolean inverted, Color mapColor) {
        super(movePotency, mapColor);
        this.inverted = inverted;
    }

    @Override
    public boolean itemFilter(Level level, ItemPickupEntity itemPickup, int tileX, int tileY) {
        ObjectEntity ent = level.entityManager.getObjectEntity(tileX, tileY);
        if (ent != null && ent.implementsOEInventory()) {
            return ((OEInventory) ent).getInventory().streamSlots().anyMatch(slot ->
                    slot.getItem() != null && slot.getItem().item.getID() == itemPickup.item.item.getID()
            );
        }
        return false;
    }

    @Override
    public boolean invertedOnInvalid() {
        return inverted;
    }

    public void addLayerDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int layerID, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        TextureDrawOptions options = texture.initDraw().sprite(rotation % 4, getAnimation(level), 32).light(light).pos(drawX, drawY);

        ObjectEntity ent = level.entityManager.getObjectEntity(tileX, tileY);
        final DrawOptions item;
        if (ent != null && ent.implementsOEInventory()) {
            InventoryItem invItem = ((OEInventory) ent).getInventory().streamSlots().map(InventorySlot::getItem).filter(Objects::nonNull).findFirst().orElse(null);
            item = invItem != null ? invItem.item.getItemSprite(invItem, perspective).initDraw().size(32).alpha(0.75F).light(level.getLightLevel(tileX, tileY)).pos(drawX, drawY) : () -> {
            };
        } else {
            item = () -> {
            };
        }

        tileList.add((tm) -> {
            options.draw();
            item.draw();
        });
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServer()) {
            OEInventoryContainer.openAndSendContainer(ContainerRegistry.OE_INVENTORY_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new FilterConveyorEntity(level, x, y);
    }

    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "filterconveyortip"));
        return tooltips;
    }

    public static class FilterConveyorEntity extends InventoryObjectEntity {
        public FilterConveyorEntity(Level level, int x, int y) {
            super(level, x, y, 20);
        }

        public boolean canQuickStackInventory() {
            return false;
        }

        public boolean canRestockInventory() {
            return false;
        }

        public boolean canSortInventory() {
            return false;
        }

        public boolean canUseForNearbyCrafting() {
            return false;
        }
    }

}
