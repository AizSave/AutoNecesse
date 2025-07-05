package autonecesse.objects;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.pickup.PickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class Conveyor extends GameObject {
    public ObjectDamagedTextureArray texture;
    int movePotency;

    public Conveyor(int movePotency, Color mapColor) {
        super(new Rectangle(0, 0, 0, 0));
        this.movePotency = movePotency;
        this.mapColor = mapColor;
        this.objectHealth = 30;
        this.isLightTransparent = true;
        this.toolType = ToolType.ALL;
        this.hoverHitbox = new Rectangle(0, 0, 32, 32);
        this.displayMapTooltip = true;
        this.hoverHitboxSortY = -16;
    }

    public boolean itemFilter(Level level, ItemPickupEntity itemPickup, int tileX, int tileY) {
        return true;
    }

    public boolean inverted(Level level, int tileX, int tileY) {
        return false;
    }

    public boolean inverted(Level level, int tileX, int tileY, ItemPickupEntity itemPickup) {
        return inverted(level, tileX, tileY);
    }

    public boolean inverted(Level level, int tileX, int tileY, Mob mob) {
        return inverted(level, tileX, tileY);
    }

    public boolean invertedOnInvalid() {
        return false;
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        super.tickEffect(level, layerID, tileX, tileY);
        if (level.isClient()) {
            int rotation = level.getObjectRotation(tileX, tileY);
            int move = movePotency * (rotation == 1 || rotation == 2 ? 1 : -1);
            boolean horizontal = rotation % 2 == 1;
            for (PickupEntity pickup : level.entityManager.pickups) {
                if(pickup instanceof ItemPickupEntity) {
                    ItemPickupEntity itemPickup = (ItemPickupEntity) pickup;

                    boolean filter = itemFilter(level, itemPickup, tileX, tileY);
                    boolean pickupHorizontal = filter == horizontal;
                    int pickupMove = move * (inverted(level, tileX, tileY, itemPickup) ? -1 : 1);
                    if(!filter && ((invertedOnInvalid() || pickupHorizontal) && !(invertedOnInvalid() && pickupHorizontal))) {
                        pickupMove *= -1;
                    }

                    int pickupTileX = (int) (itemPickup.x / 32);
                    int pickupTileY = (int) (itemPickup.y / 32);

                    if (pickupTileX == tileX && pickupTileY == tileY) {
                        if (pickupHorizontal) {
                            itemPickup.x += pickupMove;
                        } else {
                            itemPickup.y += pickupMove;
                        }
                        int newPickupTileX = (int) (itemPickup.x / 32);
                        int newPickupTileY = (int) (itemPickup.y / 32);

                        if (pickupTileX != newPickupTileX || pickupTileY != newPickupTileY) {
                            GameObject newObject = level.getObject(newPickupTileX, newPickupTileY);
                            if (newObject instanceof Conveyor) {
                                int newRotation = level.getObjectRotation(newPickupTileX, newPickupTileY);
                                boolean newHorizontal = ((Conveyor) newObject).itemFilter(level, itemPickup, newPickupTileX, newPickupTileY) ==  (newRotation % 2 == 1);
                                if (pickupHorizontal != newHorizontal) {
                                    if (pickupTileX != newPickupTileX) {
                                        itemPickup.x = newPickupTileX * 32 + 16;
                                    }
                                    if (pickupTileY != newPickupTileY) {
                                        itemPickup.y = newPickupTileY * 32 + 16;
                                    }
                                }
                            }
                        }

                        level.getClient().network.sendPacket(new PacketPickupConveyorMove(itemPickup));
                    }
                }
            }
        }
    }

    @Override
    public void tick(Mob mob, Level level, int tileX, int tileY) {
        super.tick(level, tileX, tileY);
        int rotation = level.getObjectRotation(tileX, tileY);
        int move = movePotency * (rotation == 1 || rotation == 2 ? 1 : -1) * (inverted(level, tileX, tileY, mob) ? -1 : 1);
        boolean horizontal = rotation % 2 == 1;
        int newTileX = tileX;
        int newTileY = tileY;

        if (rotation % 2 == 1) {
            newTileX = (int) ((mob.x + move) / 32);
        } else {
            newTileY = (int) ((mob.y + move) / 32);
        }

        boolean change = false;
        if (newTileX != tileX || newTileY != tileY) {
            GameObject newObject = level.getObject(newTileX, newTileY);
            if (newObject instanceof Conveyor) {
                int newRotation = level.getObjectRotation(newTileX, newTileY);
                boolean newHorizontal = newRotation % 2 == 1;
                if (horizontal != newHorizontal) {
                    change = true;
                }
            }
        }

        if (change) {
            if (horizontal) {
                mob.setPos(newTileX * 32 + 16, mob.y, true);
            } else {
                mob.setPos(mob.x, newTileY * 32 + 16, true);
            }
        } else {
            if (horizontal) {
                mob.setPos(mob.x + move, mob.y, true);
            } else {
                mob.setPos(mob.x, mob.y + move, true);
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
        int rotation = level.getObjectRotation(tileX, tileY);
        if(inverted(level, tileX, tileY)) {
            rotation = (rotation + 2) % 4;
        }
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        TextureDrawOptions options = texture.initDraw().sprite(rotation % 4, getAnimation(level), 32).light(light).pos(drawX, drawY);
        tileList.add((tm) -> options.draw());
    }

    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0F);
        texture.initDraw().sprite(rotation % 4, getAnimation(level), 32).alpha(alpha).draw(drawX, drawY);
    }

    public int getAnimation(Level level) {
        return (int) ((level.getWorldEntity().getTime() / (100 / movePotency)) % 8);
    }

    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "conveyortip"));
        return tooltips;
    }

    public static class PacketPickupConveyorMove extends Packet {
        public final int pickupUniqueID;

        public PacketPickupConveyorMove(byte[] data) {
            super(data);
            PacketReader reader = new PacketReader(this);
            this.pickupUniqueID = reader.getNextInt();
        }

        public PacketPickupConveyorMove(PickupEntity pickup) {
            this.pickupUniqueID = pickup.getUniqueID();
            PacketWriter writer = new PacketWriter(this);
            writer.putNextInt(this.pickupUniqueID);
        }

        public void processServer(NetworkPacket packet, Server server, ServerClient client) {
            Level level = server.getClient(client.slot).getLevel();
            PickupEntity pickup = level.entityManager.pickups.get(this.pickupUniqueID, true);
            if (pickup instanceof ItemPickupEntity) {
                ItemPickupEntity itemPickup = (ItemPickupEntity) pickup;

                int tileX = (int) (itemPickup.x / 32);
                int tileY = (int) (itemPickup.y / 32);
                GameObject object = level.getObject(tileX, tileY);
                if (object instanceof Conveyor) {
                    Conveyor conveyor = (Conveyor) object;
                    int rotation = level.getObjectRotation(tileX, tileY);
                    int move = conveyor.movePotency * (rotation == 1 || rotation == 2 ? 1 : -1) * (conveyor.inverted(level, tileX, tileY, itemPickup) ? -1 : 1);
                    boolean filter = conveyor.itemFilter(level, itemPickup, tileX, tileY);
                    boolean horizontal = filter == (rotation % 2 == 1);
                    if(!filter && ((conveyor.invertedOnInvalid() || horizontal) && !(conveyor.invertedOnInvalid() && horizontal))) {
                        move *= -1;
                    }
                    if (horizontal) {
                        itemPickup.x += move;
                    } else {
                        itemPickup.y += move;
                    }
                    int newTileX = (int) (itemPickup.x / 32);
                    int newTileY = (int) (itemPickup.y / 32);
                    if (tileX != newTileX || tileY != newTileY) {
                        GameObject newObject = level.getObject(newTileX, newTileY);
                        if (newObject instanceof Conveyor) {
                            int newRotation = level.getObjectRotation(newTileX, newTileY);
                            boolean newHorizontal = ((Conveyor) newObject).itemFilter(level, itemPickup, newTileX, newTileY) == (newRotation % 2 == 1);
                            if (horizontal != newHorizontal) {
                                if (tileX != newTileX) {
                                    itemPickup.x = newTileX * 32 + 16;
                                }
                                if (tileY != newTileY) {
                                    itemPickup.y = newTileY * 32 + 16;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
