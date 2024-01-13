package io.github.mctowerchallenge.mctcplugin.eventspecific.jan2024.quests;

import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.character.*;
import io.github.mystievous.mystigui.GuiUtil;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class NPCMaps {

    public static Map<Class<? extends Trait>, Integer> npcIds = new HashMap<>(Map.ofEntries(
            Map.entry(SteveSkellington.SteveTrait.class, 111),
            Map.entry(Percy.PercyTrait.class, 108),
            Map.entry(Boney.BoneyTrait.class, 110),
            Map.entry(Erie.ErieTrait.class, 106),
            Map.entry(Henry.HenryTrait.class, 109),
            Map.entry(Penelope.PenelopeTrait.class, 114),
            Map.entry(ButtStallion.ButtStallionTrait.class, 113),
            Map.entry(Pete.PeteTrait.class, 118),
            Map.entry(Dave.DaveTrait.class, 117),
            Map.entry(Soup.SoupTrait.class, 116),
            Map.entry(Moollicient.MoollicientTrait.class, 115),
            Map.entry(Ari.AriTrait.class, 120),
            Map.entry(Alice.AliceTrait.class, 112),
            Map.entry(PolarW.PolarWTrait.class, 107)
    ));

    public static Map<Class<? extends Trait>, float[]> preLookYaws = new HashMap<>(Map.ofEntries(
            Map.entry(SteveSkellington.SteveTrait.class, new float[]{90, 270}),
            Map.entry(Percy.PercyTrait.class, new float[]{-70, -50}),
            Map.entry(Boney.BoneyTrait.class, new float[]{105, 135}),
            Map.entry(Erie.ErieTrait.class, new float[]{0, 360}),
            Map.entry(Henry.HenryTrait.class, new float[]{0, 360}),
            Map.entry(Penelope.PenelopeTrait.class, new float[]{0, 360}),
            Map.entry(ButtStallion.ButtStallionTrait.class, new float[]{0, 360}),
            Map.entry(Pete.PeteTrait.class, new float[]{0, 360}),
            Map.entry(Dave.DaveTrait.class, new float[]{0, 360}),
            Map.entry(Soup.SoupTrait.class, new float[]{150, 210}),
            Map.entry(Moollicient.MoollicientTrait.class, new float[]{-30, 30}),
            Map.entry(Ari.AriTrait.class, new float[]{150, 210}),
            Map.entry(Alice.AliceTrait.class, new float[]{-30, 30}),
            Map.entry(PolarW.PolarWTrait.class, new float[]{140, 235})
    ));

    public static Map<Class<? extends Trait>, float[]> preLookPitches = new HashMap<>(Map.ofEntries(
            Map.entry(SteveSkellington.SteveTrait.class, new float[]{0, 15}),
            Map.entry(Percy.PercyTrait.class, new float[]{-25, -10}),
            Map.entry(Boney.BoneyTrait.class, new float[]{15, 30}),
            Map.entry(Erie.ErieTrait.class, new float[]{0, 0}),
            Map.entry(Henry.HenryTrait.class, new float[]{0, 0}),
            Map.entry(Penelope.PenelopeTrait.class, new float[]{0, 0}),
            Map.entry(ButtStallion.ButtStallionTrait.class, new float[]{0, 0}),
            Map.entry(Pete.PeteTrait.class, new float[]{0, 0}),
            Map.entry(Dave.DaveTrait.class, new float[]{0, 0}),
            Map.entry(Soup.SoupTrait.class, new float[]{0, 0}),
            Map.entry(Moollicient.MoollicientTrait.class, new float[]{0, 0}),
            Map.entry(Ari.AriTrait.class, new float[]{0, 0}),
            Map.entry(Alice.AliceTrait.class, new float[]{0, 0}),
            Map.entry(PolarW.PolarWTrait.class, new float[]{-35, 0})
    ));

    public static Map<Class<? extends Trait>, Location> preLocations = new HashMap<>(Map.ofEntries(
            Map.entry(SteveSkellington.SteveTrait.class, new Location(Worlds.Jan2024(), -1398.458000d, 69.000000d, -356.024500d, 111.046120f, 5.692204f)),
            Map.entry(Percy.PercyTrait.class, new Location(Worlds.Jan2024(), -1406.700000d, 72.000000d, -347.553400d, -62.222744f, -14.142388f)),
            Map.entry(Boney.BoneyTrait.class, new Location(Worlds.Jan2024(), -1403.721200d, 72.000000d, -346.111800d, 109.649147f, 22.799355f)),
            Map.entry(Erie.ErieTrait.class, new Location(Worlds.Jan2024(), -1413.261800d, 69.000000d, -359.187500d, -48.036301f, 13.940359f)),
            Map.entry(Henry.HenryTrait.class, new Location(Worlds.Jan2024(), -1385.428100d, 69.000000d, -354.519600d, -62.339882f, 5.253787f)),
            Map.entry(Penelope.PenelopeTrait.class, new Location(Worlds.Jan2024(), -1395.236800d, 69.062500d, -368.590000d, 60.308281f, 0.000000f)),
            Map.entry(ButtStallion.ButtStallionTrait.class, new Location(Worlds.Jan2024(), -1395.282500d, 69.062500d, -371.162100d, -52.546463f, 0.000000f)),
            Map.entry(Pete.PeteTrait.class, new Location(Worlds.Jan2024(), -1408.559700d, 69.000000d, -384.366400d, -112.674530f, 0.000000f)),
            Map.entry(Dave.DaveTrait.class, new Location(Worlds.Jan2024(), -1407.493800d, 69.062500d, -441.387700d, 157.239685f, 0.000000f)),
            Map.entry(Soup.SoupTrait.class, new Location(Worlds.Jan2024(), -1407.651400d, 69.000000d, -470.208300d, 99.380974f, 0.000000f)),
            Map.entry(Moollicient.MoollicientTrait.class, new Location(Worlds.Jan2024(), -1407.498600d, 69.000000d, -473.058500d, 156.683243f, 0.000000f)),
            Map.entry(Ari.AriTrait.class, new Location(Worlds.Jan2024(), -1408.509100d, 70.000000d, -477.154700d, -161.089508f, 0.000000f)),
            Map.entry(Alice.AliceTrait.class, new Location(Worlds.Jan2024(), -1408.300000d, 70.000000d, -479.700000d, -7.536316f, 0.000000f)),
            Map.entry(PolarW.PolarWTrait.class, new Location(Worlds.Jan2024(), -1388.525900d, 69.000000d, -481.146400d, -175.323608f, -6.555727f))
    ));

    public static Map<Class<? extends Trait>, float[]> postLookYaws = new HashMap<>(Map.ofEntries(
            Map.entry(SteveSkellington.SteveTrait.class, new float[]{145, 175}),
            Map.entry(Percy.PercyTrait.class, new float[]{160, 200}),
            Map.entry(Boney.BoneyTrait.class, new float[]{170, 218}),
            Map.entry(Erie.ErieTrait.class, new float[]{170, 195}),
            Map.entry(Henry.HenryTrait.class, new float[]{180, 210}),
            Map.entry(Penelope.PenelopeTrait.class, new float[]{10, 40}),
            Map.entry(ButtStallion.ButtStallionTrait.class, new float[]{-4, 30}),
            Map.entry(Pete.PeteTrait.class, new float[]{-55, -17}),
            Map.entry(Dave.DaveTrait.class, new float[]{-40, -10}),
            Map.entry(Soup.SoupTrait.class, new float[]{-21, -2}),
            Map.entry(Moollicient.MoollicientTrait.class, new float[]{-22, -6}),
            Map.entry(Ari.AriTrait.class, new float[]{-3, 25}),
            Map.entry(Alice.AliceTrait.class, new float[]{-8, 20}),
            Map.entry(PolarW.PolarWTrait.class, new float[]{0, 19})
    ));

    public static Map<Class<? extends Trait>, float[]> postLookPitches = new HashMap<>(Map.ofEntries(
            Map.entry(SteveSkellington.SteveTrait.class, new float[]{0, 15}),
            Map.entry(Percy.PercyTrait.class, new float[]{0, 15}),
            Map.entry(Boney.BoneyTrait.class, new float[]{0, 15}),
            Map.entry(Erie.ErieTrait.class, new float[]{0, 20}),
            Map.entry(Henry.HenryTrait.class, new float[]{0, 18}),
            Map.entry(Penelope.PenelopeTrait.class, new float[]{-20, -9}),
            Map.entry(ButtStallion.ButtStallionTrait.class, new float[]{-20, -8}),
            Map.entry(Pete.PeteTrait.class, new float[]{0, 0}),
            Map.entry(Dave.DaveTrait.class, new float[]{-25, 0}),
            Map.entry(Soup.SoupTrait.class, new float[]{-10, -2}),
            Map.entry(Moollicient.MoollicientTrait.class, new float[]{-10, -2}),
            Map.entry(Ari.AriTrait.class, new float[]{6, 16}),
            Map.entry(Alice.AliceTrait.class, new float[]{6, 16}),
            Map.entry(PolarW.PolarWTrait.class, new float[]{-12, -3})
    ));

    public static Map<Class<? extends Trait>, Location> postLocations = new HashMap<>(Map.ofEntries(
            Map.entry(SteveSkellington.SteveTrait.class, new Location(Worlds.Jan2024(), -1397.542946d, 72.000000d, -345.918289d, -147.412903f, 2.120862f)),
            Map.entry(Percy.PercyTrait.class, new Location(Worlds.Jan2024(), -1401.445133d, 72.92, -345.172217d, 180, 0)),
            Map.entry(Boney.BoneyTrait.class, new Location(Worlds.Jan2024(), -1403.721200d, 72.000000d, -346.111800d, 109.649147f, 22.799355f)),
            Map.entry(Erie.ErieTrait.class, new Location(Worlds.Jan2024(), -1400.280055d, 72.000000d, -350.326397d, 159.222031f, 0.000000f)),
            Map.entry(Henry.HenryTrait.class, new Location(Worlds.Jan2024(), -1404.711039d, 72.000000d, -348.157287d, -168.362305f, 0.000000f)),
            Map.entry(Penelope.PenelopeTrait.class, new Location(Worlds.Jan2024(), -1396.957512d, 69.062500d, -360.056098d, 129.009583f, 0.000000f)),
            Map.entry(ButtStallion.ButtStallionTrait.class, new Location(Worlds.Jan2024(), -1398.514705d, 69.000000d, -360.914088d, -33.927063f, 0.000000f)),
            Map.entry(Pete.PeteTrait.class, new Location(Worlds.Jan2024(), -1407.482339d, 73.000000d, -356.530282d, -100.122559f, 0.000000f)),
            Map.entry(Dave.DaveTrait.class, new Location(Worlds.Jan2024(), -1406.426394d, 69.062500d, -358.443429d, -41.242294f, 0.000000f)),
            Map.entry(Soup.SoupTrait.class, new Location(Worlds.Jan2024(), -1405.854987d, 69.062500d, -372.248475d, -158.977509f, 0.000000f)),
            Map.entry(Moollicient.MoollicientTrait.class, new Location(Worlds.Jan2024(), -1407.390301d, 69.062500d, -371.731161d, 22.838144f, 0.000000f)),
            Map.entry(Ari.AriTrait.class, new Location(Worlds.Jan2024(), -1398.492741d, 74.500000d, -364.547206d, 168.976212f, 0.000000f)),
            Map.entry(Alice.AliceTrait.class, new Location(Worlds.Jan2024(), -1399.766169d, 74.500000d, -364.613534d, -10.297278f, 0.000000f)),
            Map.entry(PolarW.PolarWTrait.class, new Location(Worlds.Jan2024(), -1398.610002d, 69.000000d, -370.285439d, -161.315430f, -3.595920f))
    ));

    public static Map<Class<? extends Trait>, ItemStack[]> preItems = new HashMap<>(Map.ofEntries(
            Map.entry(SteveSkellington.SteveTrait.class, new ItemStack[]{null, null}),
            Map.entry(Percy.PercyTrait.class, new ItemStack[]{null, null}),
            Map.entry(Erie.ErieTrait.class, new ItemStack[]{null, null})
    ));

    public static Map<Class<? extends Trait>, ItemStack[]> postItems = new HashMap<>(Map.ofEntries(
            Map.entry(SteveSkellington.SteveTrait.class, new ItemStack[]{GuiUtil.formatItem("Trumpet", Material.OBSIDIAN, 42), null}),
            Map.entry(Percy.PercyTrait.class, new ItemStack[]{GuiUtil.formatItem("Drumstick", Material.STICK, 4), GuiUtil.formatItem("Drumstick", Material.STICK, 4)}),
            Map.entry(Erie.ErieTrait.class, new ItemStack[]{GuiUtil.formatItem("Guitar", Material.OBSIDIAN, 41), null})
    ));

}
