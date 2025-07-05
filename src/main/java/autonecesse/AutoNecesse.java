package autonecesse;

import autonecesse.mobs.MechanicHumanMob;
import autonecesse.mobs.MechanicSettler;
import autonecesse.objects.*;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.*;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;

import java.awt.*;

@ModEntry
public class AutoNecesse {

    public void init() {
        ObjectRegistry.registerObject("conveyor", new Conveyor(2, new Color(16, 16, 16)), 0.6F, true);
        ObjectRegistry.registerObject("splitterconveyor", new SplitterConveyor(2), 3F, true);

        ObjectRegistry.registerObject("rfilterconveyor", new FilterConveyor(2, false, new Color(233, 134, 39)), -1F, true);
        ObjectRegistry.registerObject("lfilterconveyor", new FilterConveyor(2, true, new Color(233, 134, 39)), -1F, true);

        ObjectRegistry.registerObject("wiredconveyor", new WiredConveyor(2, new Color(140, 32, 32)), -1F, true);

        ObjectRegistry.registerObject("inserter", new Inserter(new Color(233, 134, 39)), -1F, true);
        ObjectRegistry.registerObject("wiredinserter", new WiredInserter(new Color(140, 32, 32)), -1F, true);

        ObjectRegistry.registerObject("idropper", new Dropper(2, false, new Color(233, 134, 39)), -1F, true);
        ObjectRegistry.registerObject("iwireddropper", new WiredDropper(2, false, new Color(140, 32, 32)), -1F, true);
        ObjectRegistry.registerObject("sdropper", new Dropper(2, true, new Color(233, 134, 39)), -1F, true);
        ObjectRegistry.registerObject("swireddropper", new WiredDropper(2, true, new Color(140, 32, 32)), -1F, true);

        MobRegistry.registerMob("mechanichuman", MechanicHumanMob.class, true);

        SettlerRegistry.registerSettler("mechanic", new MechanicSettler());

        PacketRegistry.registerPacket(Conveyor.PacketPickupConveyorMove.class);
        PacketRegistry.registerPacket(Inserter.PacketInsertPickup.class);
    }

    public void postInit() {
        registerRecipes(
                new Recipe("conveyor", 25, RecipeTechRegistry.WORKSTATION, new Ingredient[]{
                        new Ingredient("stone", 25),
                        new Ingredient("copperbar", 2),
                        new Ingredient("ironbar", 1)
                }),
                new Recipe("splitterconveyor", 5, RecipeTechRegistry.WORKSTATION, new Ingredient[]{
                        new Ingredient("stone", 25),
                        new Ingredient("copperbar", 2),
                        new Ingredient("ironbar", 1)
                }),
                new Recipe("rfilterconveyor", 1, RecipeTechRegistry.DEMONIC_WORKSTATION, new Ingredient[]{
                        new Ingredient("conveyor", 1),
                        new Ingredient("goldbar", 1)
                }),
                new Recipe("rfilterconveyor", 1, RecipeTechRegistry.NONE, new Ingredient[]{
                        new Ingredient("lfilterconveyor", 1)
                }, true),
                new Recipe("lfilterconveyor", 1, RecipeTechRegistry.DEMONIC_WORKSTATION, new Ingredient[]{
                        new Ingredient("conveyor", 1),
                        new Ingredient("goldbar", 1)
                }),
                new Recipe("lfilterconveyor", 1, RecipeTechRegistry.NONE, new Ingredient[]{
                        new Ingredient("rfilterconveyor", 1)
                }, true),
                new Recipe("wiredconveyor", 1, RecipeTechRegistry.TUNGSTEN_WORKSTATION, new Ingredient[]{
                        new Ingredient("conveyor", 1),
                        new Ingredient("goldbar", 1),
                        new Ingredient("wire", 10)
                }),
                new Recipe("inserter", 1, RecipeTechRegistry.DEMONIC_WORKSTATION, new Ingredient[]{
                        new Ingredient("conveyor", 1),
                        new Ingredient("goldbar", 1)
                }),
                new Recipe("wiredinserter", 1, RecipeTechRegistry.TUNGSTEN_WORKSTATION, new Ingredient[]{
                        new Ingredient("inserter", 1),
                        new Ingredient("wire", 10)
                }),
                new Recipe("idropper", 1, RecipeTechRegistry.DEMONIC_WORKSTATION, new Ingredient[]{
                        new Ingredient("conveyor", 1),
                        new Ingredient("goldbar", 1)
                }),
                new Recipe("iwireddropper", 1, RecipeTechRegistry.TUNGSTEN_WORKSTATION, new Ingredient[]{
                        new Ingredient("idropper", 1),
                        new Ingredient("wire", 10)
                }),
                new Recipe("sdropper", 1, RecipeTechRegistry.DEMONIC_WORKSTATION, new Ingredient[]{
                        new Ingredient("idropper", 1),
                        new Ingredient("goldbar", 1)
                }),
                new Recipe("swireddropper", 1, RecipeTechRegistry.TUNGSTEN_WORKSTATION, new Ingredient[]{
                        new Ingredient("sdropper", 1),
                        new Ingredient("wire", 10)
                })
        );
    }

    String lastItem = "tnt";
    public void registerRecipes(Recipe... recipes) {
        for (Recipe recipe : recipes) {
            Recipes.registerModRecipe(
                    recipe.showAfter(lastItem)
            );
            lastItem = recipe.resultStringID;
        }
    }

}
