package autonecesse.objects;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FueledProcessingInventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventorySlot;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Dropper extends Conveyor {
    public ObjectDamagedTextureArray texture;
    public boolean wholeStack;
    public static int dropForce = 64;

    public Dropper(int movePotency, boolean wholeStack, Color mapColor) {
        super(movePotency, mapColor);
        this.wholeStack = wholeStack;
    }

    @Override
    public void tick(Level level, int tileX, int tileY) {
        super.tick(level, tileX, tileY);
        tickDropItem(level, tileX, tileY);
    }

    @Override
    public void tick(Mob mob, Level level, int tileX, int tileY) {
    }

    public void tickDropItem(Level level, int tileX, int tileY) {
        if (level.isServer()) {
            int chestX = tileX;
            int chestY = tileY;
            int rotation = level.getObjectRotation(tileX, tileY);
            if (rotation == 0) {
                chestY++;
            } else if (rotation == 1) {
                chestX--;
            } else if (rotation == 2) {
                chestY--;
            } else {
                chestX++;
            }
            ObjectEntity entity = level.entityManager.getObjectEntity(chestX, chestY);

            if (entity instanceof OEInventory) {
                if (entity instanceof FueledProcessingInventoryObjectEntity) {
                    FueledProcessingInventoryObjectEntity inventoryEntity = ((FueledProcessingInventoryObjectEntity) entity);
                    Inventory inventory = inventoryEntity.getInventory();
                    AtomicBoolean cont = new AtomicBoolean(true);
                    inventory.streamSlots().forEach(slot -> {
                        if (slot.slot >= inventoryEntity.fuelSlots + inventoryEntity.inputSlots && cont.get() && !slot.isSlotClear() && slot.getItem() != null && slot.getItem().item != null) {
                            dropItem(level, slot, rotation, tileX, tileY);
                            cont.set(false);
                        }
                    });
                } else {
                    OEInventory inventoryEntity = ((OEInventory) entity);
                    Inventory inventory = inventoryEntity.getInventory();
                    AtomicBoolean cont = new AtomicBoolean(true);
                    inventory.streamSlots().forEach(slot -> {
                        if (cont.get() && !slot.isSlotClear() && slot.getItem() != null && slot.getItem().item != null) {
                            dropItem(level, slot, rotation, tileX, tileY);
                            cont.set(false);
                        }
                    });
                }
            }
        }
    }

    public void dropItem(Level level, InventorySlot slot, int rotation, int tileX, int tileY) {
        int dropX = tileX * 32 + 16;
        int dropY = tileY * 32 + 16;
        int dX = 0;
        int dY = 0;

        if (rotation == 0) {
            dropY += 12;
            dY -= dropForce;
        } else if (rotation == 1) {
            dropX -= 12;
            dX += dropForce;
        } else if (rotation == 2) {
            dropY -= 12;
            dY += dropForce;
        } else {
            dropX += 12;
            dX -= dropForce;
        }

        int slotAmount = slot.getAmount();
        if(wholeStack || slotAmount == 1) {
            level.entityManager.pickups.add(
                    new ItemPickupEntity(level, slot.getItem(), dropX, dropY, dX, dY, 0, 20F)
            );

            slot.clearSlot();
        } else {
            InventoryItem item = slot.getItem().copy();
            item.setAmount(1);
            level.entityManager.pickups.add(
                    new ItemPickupEntity(level, item, dropX, dropY, dX, dY, 0, 30F)
            );

            slot.setAmount(slotAmount - 1);
        }
    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay(this, "objects/" + this.getStringID());
    }

    public void addLayerDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int layerID, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        TextureDrawOptions options = texture.initDraw().sprite(rotation % 4, 0, 32).light(light).pos(drawX, drawY);
        tileList.add((tm) -> options.draw());
    }

    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0F);
        texture.initDraw().sprite(rotation % 4, 0, 32).alpha(alpha).draw(drawX, drawY);
    }

    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.removeLast();
        tooltips.add(Localization.translate("itemtooltip", "droppertip"));
        return tooltips;
    }
}
