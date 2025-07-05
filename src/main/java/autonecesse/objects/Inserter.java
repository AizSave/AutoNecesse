package autonecesse.objects;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameUtils;
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
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Inserter extends GameObject {
    public ObjectDamagedTextureArray texture;

    public Inserter(Color mapColor) {
        super(new Rectangle(0, 0, 0, 0));
        this.mapColor = mapColor;
        this.objectHealth = 30;
        this.isLightTransparent = true;
        this.toolType = ToolType.ALL;
        this.hoverHitbox = new Rectangle(0, 0, 32, 32);
        this.displayMapTooltip = true;
        this.hoverHitboxSortY = -16;
    }

    @Override
    public void tick(Level level, int tileX, int tileY) {
        super.tick(level, tileX, tileY);
        tickInsertItem(level, tileX, tileY);
    }

    public void tickInsertItem(Level level, int tileX, int tileY) {
        if(level.isServer()) {
            int entityX = tileX;
            int entityY = tileY;
            int rotation = level.getObjectRotation(tileX, tileY);
            if(rotation == 0) {
                entityY--;
            } else if(rotation == 1) {
                entityX++;
            } else if(rotation == 2) {
                entityY++;
            } else {
                entityX--;
            }
            ObjectEntity entity = level.entityManager.getObjectEntity(entityX, entityY);

            if (entity instanceof OEInventory) {
                if (entity instanceof FueledProcessingInventoryObjectEntity) {
                    FueledProcessingInventoryObjectEntity inventoryEntity = ((FueledProcessingInventoryObjectEntity) entity);
                    inventoryEntity.forceNextUpdate();
                    Inventory inventory = inventoryEntity.getInventory();
                    for (PickupEntity pickup : level.entityManager.pickups) {
                        int pickupTileX = (int) (pickup.x / 32);
                        int pickupTileY = (int) (pickup.y / 32);

                        if (pickupTileX == tileX && pickupTileY == tileY) {
                            ItemPickupEntity itemPickup = (ItemPickupEntity) pickup;
                            insertItem(itemPickup, inventory.streamSlots().filter(
                                    slot -> {
                                        if(slot.slot < inventoryEntity.fuelSlots) {
                                            return inventoryEntity.isValidFuelItem(itemPickup.item);
                                        } else {
                                            return slot.slot < inventoryEntity.fuelSlots + inventoryEntity.inputSlots;
                                        }
                                    }
                            ));
                            int entityTileX = entityX;
                            int entityTileY = entityY;
                            GameUtils.streamServerClients(level).forEach(c -> c.sendPacket(new PacketInsertPickup(pickup, entityTileX, entityTileY)));
                        }
                    }
                } else {
                    OEInventory inventoryEntity = ((OEInventory) entity);
                    Inventory inventory = inventoryEntity.getInventory();
                    for (PickupEntity pickup : level.entityManager.pickups) {
                        int pickupTileX = (int) (pickup.x / 32);
                        int pickupTileY = (int) (pickup.y / 32);

                        if (pickupTileX == tileX && pickupTileY == tileY) {
                            ItemPickupEntity itemPickup = (ItemPickupEntity) pickup;
                            insertItem(itemPickup, inventory.streamSlots());
                            int entityTileX = entityX;
                            int entityTileY = entityY;
                            GameUtils.streamServerClients(level).forEach(c -> c.sendPacket(new PacketInsertPickup(pickup, entityTileX, entityTileY)));
                        }
                    }
                }
            }
        }
    }

    public static void insertItem(ItemPickupEntity itemPickup, Stream<InventorySlot> slots) {
        List<InventorySlot> slotsList = slots.collect(Collectors.toList());
        for (InventorySlot slot : slotsList) {
            if (!itemPickup.removed()) {
                if (slot.getItemID() == itemPickup.item.item.getID()) {
                    int stackItem = slot.getItem().item.getStackSize();
                    int amountSlot = slot.getItem().getAmount();
                    int amountPickup = itemPickup.item.getAmount();
                    if (amountPickup + amountSlot > stackItem) {
                        int addedAmount = stackItem - amountSlot;
                        if (addedAmount > 0) {
                            slot.addAmount(addedAmount);
                            if (addedAmount == amountPickup) {
                                itemPickup.remove();
                            } else {
                                itemPickup.item.setAmount(amountPickup - addedAmount);
                            }
                        }
                    } else {
                        slot.addAmount(amountPickup);
                        itemPickup.remove();
                    }
                }
            }
        }

        if (!itemPickup.removed()) {
            for (InventorySlot slot : slotsList) {
                if (!itemPickup.removed()) {
                    if (slot.getItem() == null) {
                        int stackItem = itemPickup.item.itemStackSize();
                        int amountPickup = itemPickup.item.getAmount();
                        slot.setItem(itemPickup.item);
                        if (amountPickup > stackItem) {
                            slot.setAmount(stackItem);
                            itemPickup.item.setAmount(amountPickup - stackItem);
                        } else {
                            itemPickup.remove();
                        }
                    }
                }
            }
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
        tooltips.add(Localization.translate("itemtooltip", "insertertip"));
        return tooltips;
    }

    public static class PacketInsertPickup extends Packet {
        public final int pickupUniqueID;
        public final int tileX;
        public final int tileY;

        public PacketInsertPickup(byte[] data) {
            super(data);
            PacketReader reader = new PacketReader(this);
            this.pickupUniqueID = reader.getNextInt();
            this.tileX = reader.getNextInt();
            this.tileY = reader.getNextInt();
        }

        public PacketInsertPickup(PickupEntity pickup, int tileX, int tileY) {
            this.pickupUniqueID = pickup.getUniqueID();
            this.tileX = tileX;
            this.tileY = tileY;
            PacketWriter writer = new PacketWriter(this);
            writer.putNextInt(this.pickupUniqueID);
            writer.putNextInt(this.tileX);
            writer.putNextInt(this.tileY);
        }

        @Override
        public void processClient(NetworkPacket packet, Client client) {
            Level level = client.getLevel();
            PickupEntity pickup = level.entityManager.pickups.get(this.pickupUniqueID, true);
            if (pickup instanceof ItemPickupEntity) {
                ItemPickupEntity itemPickup = (ItemPickupEntity) pickup;

                ObjectEntity entity = level.entityManager.getObjectEntity(tileX, tileY);
                if (entity instanceof OEInventory) {
                    if (entity instanceof FueledProcessingInventoryObjectEntity) {
                        FueledProcessingInventoryObjectEntity inventoryEntity = ((FueledProcessingInventoryObjectEntity) entity);
                        inventoryEntity.forceNextUpdate();
                        Inventory inventory = inventoryEntity.getInventory();
                        insertItem(itemPickup, inventory.streamSlots().filter(
                                slot -> {
                                    if(slot.slot < inventoryEntity.fuelSlots) {
                                        return inventoryEntity.isValidFuelItem(itemPickup.item);
                                    } else {
                                        return slot.slot < inventoryEntity.fuelSlots + inventoryEntity.inputSlots;
                                    }
                                }
                        ));
                    } else {
                        OEInventory inventoryEntity = ((OEInventory) entity);
                        Inventory inventory = inventoryEntity.getInventory();
                        insertItem(itemPickup, inventory.streamSlots());
                    }
                }
            }
        }
    }

}
