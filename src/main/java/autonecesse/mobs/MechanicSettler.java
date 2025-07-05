package autonecesse.mobs;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.Settler;

import java.util.function.Supplier;

public class MechanicSettler extends Settler {
    public MechanicSettler() {
        super("mechanichuman");
    }

    public boolean isAvailableForClient(SettlementLevelData settlement, PlayerStats stats) {
        return super.isAvailableForClient(settlement, stats) && stats.crafted_items.get() > 20;
    }

    public GameMessage getAcquireTip() {
        return new LocalMessage("settlement", "foundinvillagetip");
    }

    public void addNewRecruitSettler(SettlementLevelData data, boolean isRandomEvent, TicketSystemList<Supplier<HumanMob>> ticketSystem) {
        if ((isRandomEvent || !this.doesSettlementHaveThisSettler(data))) {
            ticketSystem.addObject(50, this.getNewRecruitMob(data));
        }
    }
}
