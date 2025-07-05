package autonecesse.mobs;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.SellingShopItem;
import necesse.gfx.HumanGender;
import necesse.gfx.HumanLook;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MechanicHumanMob extends HumanShop {
    public MechanicHumanMob() {
        super(500, 200, "mechanic");
        this.attackCooldown = 500;
        this.attackAnimTime = 500;
        this.setSwimSpeed(1.0F);
        this.equipmentInventory.setItem(6, new InventoryItem("boxingglovegun"));

        // Wire
        this.shop.addSellingItem("wire", new SellingShopItem(1000, 100)).setStaticPriceBasedOnHappiness(2, 3, 1);

        // Wire tools
        this.shop.addSellingItem("wrench", new SellingShopItem(2, 1)).setStaticPriceBasedOnHappiness(100, 200, 20);
        this.shop.addSellingItem("cutter", new SellingShopItem(2, 1)).setStaticPriceBasedOnHappiness(100, 200, 20);

        // Wire inputs
        this.shop.addSellingItem("stonepressureplate", new SellingShopItem(100, 10)).setStaticPriceBasedOnHappiness(5, 10, 2);
        this.shop.addSellingItem("rocklever", new SellingShopItem(100, 10)).setStaticPriceBasedOnHappiness(5, 10, 2);

        // Wire outputs
        this.shop.addSellingItem("ledpanel", new SellingShopItem(50, 5)).setStaticPriceBasedOnHappiness(20, 30, 5);
        this.shop.addSellingItem("fireworkdispenser", new SellingShopItem(2, 1)).setStaticPriceBasedOnHappiness(200, 1000, 100);

        // Wire outputs - tramps (expensive if low happiness)
        this.shop.addSellingItem("dungeonarrowtrap", new SellingShopItem(50, 5)).setStaticPriceBasedOnHappiness(100, 1000, 20);
        this.shop.addSellingItem("dungeonflametrap", new SellingShopItem(50, 5)).setStaticPriceBasedOnHappiness(150, 1500, 30);
        this.shop.addSellingItem("dungeonvoidtrap", new SellingShopItem(50, 5)).setStaticPriceBasedOnHappiness(200, 2000, 40);
        this.shop.addSellingItem("tnt", new SellingShopItem(10, 2)).setStaticPriceBasedOnHappiness(1000, 10000, 100);

        // Mod content
        this.shop.addSellingItem("conveyor", new SellingShopItem(100, 20)).setStaticPriceBasedOnHappiness(3, 6, 2);
        this.shop.addSellingItem("splitterconveyor", new SellingShopItem(40, 8)).setStaticPriceBasedOnHappiness(15, 30, 6);
        this.shop.addSellingItem("rfilterconveyor", new SellingShopItem(16, 4)).setStaticPriceBasedOnHappiness(50, 75, 10);
        this.shop.addSellingItem("wiredconveyor", new SellingShopItem(10, 2)).setStaticPriceBasedOnHappiness(60, 100, 10);
        this.shop.addSellingItem("inserter", new SellingShopItem(16, 4)).setStaticPriceBasedOnHappiness(50, 75, 10);
        this.shop.addSellingItem("wiredinserter", new SellingShopItem(10, 2)).setStaticPriceBasedOnHappiness(120, 150, 20);
        this.shop.addSellingItem("idropper", new SellingShopItem(16, 4)).setStaticPriceBasedOnHappiness(50, 75, 10);
        this.shop.addSellingItem("iwireddropper", new SellingShopItem(10, 2)).setStaticPriceBasedOnHappiness(120, 150, 20);
        this.shop.addSellingItem("sdropper", new SellingShopItem(16, 4)).setStaticPriceBasedOnHappiness(80, 100, 15);
        this.shop.addSellingItem("swireddropper", new SellingShopItem(10, 2)).setStaticPriceBasedOnHappiness(160, 200, 25);


        // Cheap gates
        this.shop.addSellingItem("andgate", new SellingShopItem(20, 5)).setStaticPriceBasedOnHappiness(20, 40, 5);
        this.shop.addSellingItem("orgate", new SellingShopItem(20, 5)).setStaticPriceBasedOnHappiness(20, 40, 5);
        this.shop.addSellingItem("nandgate", new SellingShopItem(20, 5)).setStaticPriceBasedOnHappiness(20, 40, 5);
        this.shop.addSellingItem("norgate", new SellingShopItem(20, 5)).setStaticPriceBasedOnHappiness(20, 40, 5);
        this.shop.addSellingItem("xorgate", new SellingShopItem(20, 5)).setStaticPriceBasedOnHappiness(20, 40, 5);
        this.shop.addSellingItem("tflipflopgate", new SellingShopItem(20, 5)).setStaticPriceBasedOnHappiness(20, 40, 5);
        this.shop.addSellingItem("srlatchgate", new SellingShopItem(20, 5)).setStaticPriceBasedOnHappiness(20, 40, 5);

        // Expensive gates
        this.shop.addSellingItem("delaygate", new SellingShopItem(10, 2)).setStaticPriceBasedOnHappiness(50, 100, 10);
        this.shop.addSellingItem("timergate", new SellingShopItem(10, 2)).setStaticPriceBasedOnHappiness(50, 100, 10);
        this.shop.addSellingItem("countergate", new SellingShopItem(10, 2)).setStaticPriceBasedOnHappiness(50, 100, 10);
        this.shop.addSellingItem("buffergate", new SellingShopItem(10, 2)).setStaticPriceBasedOnHappiness(50, 100, 10);
        this.shop.addSellingItem("sensorgate", new SellingShopItem(10, 2)).setStaticPriceBasedOnHappiness(50, 100, 10);
        this.shop.addSellingItem("soundgate", new SellingShopItem(10, 2)).setStaticPriceBasedOnHappiness(50, 100, 10);
    }

    public void randomizeLook(HumanLook look, HumanGender gender, GameRandom random) {
        this.gender = HumanGender.MALE;
        super.randomizeLook(look, this.gender, random);
        look.setFacialFeature(random.getOneOf(1, 3, 4));
        this.settlerName = this.getRandomName(new GameRandom(this.settlerSeed));
    }

    public LootTable getLootTable() {
        return super.getLootTable();
    }

    public void setDefaultArmor(HumanDrawOptions drawOptions) {
        drawOptions.helmet(new InventoryItem("minerhat"));
        drawOptions.chestplate(new InventoryItem("labcoat"));
        drawOptions.boots(new InventoryItem("labboots"));
    }

    public List<InventoryItem> getRecruitItems(ServerClient client) {
        if (this.isTrapped()) {
            return Collections.emptyList();
        } else {
            GameRandom random = new GameRandom((long)this.getSettlerSeed() * 89L);
            if (this.isVisitor()) {
                return Collections.singletonList(new InventoryItem("coin", random.getIntBetween(800, 1000)));
            } else {
                LootTable secondItems = new LootTable(new CountOfTicketLootItems(2, 100, new LootItem("copper", Integer.MAX_VALUE), 100, new LootItem("iron", Integer.MAX_VALUE)));
                ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(random, random.getIntBetween(800, 1000), 0.20000000298023224, new LootItem("coin", Integer.MAX_VALUE));
                out.addAll(GameLootUtils.getItemsValuedAt(random, random.getIntBetween(16, 20), 0.20000000298023224, secondItems));
                out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
                return out;
            }
        }
    }


    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        return this.getLocalMessages("mechanictalk", 5);
    }
}
