package io.github.mystievous.towerchallenge.eventspecific.valentines;

import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;

public class FerrisWheel {

    public static final String ENTITY_TAG = "FerrisWheelEntity";
    private static final Vector OFFSET = new Vector(2.5d, -3.0d, 0.5d);

    private final TowerChallenge plugin;

    private final CommandSender sender = Bukkit.createCommandSender(component -> {});

    private BukkitTask[] carTasks;

    private static final Location[] path = new Location[]{
            new Location(Worlds.Feb2023(), 99, 69, -2114).add(OFFSET),
            new Location(Worlds.Feb2023(), 99, 71, -2110).add(OFFSET),
            new Location(Worlds.Feb2023(), 99, 74, -2107).add(OFFSET),
            new Location(Worlds.Feb2023(), 99, 79, -2105).add(OFFSET),
            new Location(Worlds.Feb2023(), 99, 83, -2105).add(OFFSET),
            new Location(Worlds.Feb2023(), 99, 86, -2107).add(OFFSET),
            new Location(Worlds.Feb2023(), 99, 87, -2109).add(OFFSET),
            new Location(Worlds.Feb2023(), 99, 87, -2112).add(OFFSET),
            new Location(Worlds.Feb2023(), 99, 84, -2115).add(OFFSET),
            new Location(Worlds.Feb2023(), 99, 87, -2118).add(OFFSET),
            new Location(Worlds.Feb2023(), 99, 87, -2121).add(OFFSET),
            new Location(Worlds.Feb2023(), 99, 85, -2124).add(OFFSET),
            new Location(Worlds.Feb2023(), 99, 82, -2125).add(OFFSET),
            new Location(Worlds.Feb2023(), 99, 79, -2125).add(OFFSET),
            new Location(Worlds.Feb2023(), 99, 74, -2123).add(OFFSET),
            new Location(Worlds.Feb2023(), 99, 71, -2120).add(OFFSET),
            new Location(Worlds.Feb2023(), 99, 69, -2116).add(OFFSET)
    };

    public FerrisWheel(TowerChallenge plugin) {
        this.plugin = plugin;
//        for (Location location : path) {
//            ArmorStand armorStand = (ArmorStand) Worlds.Feb2023().spawnEntity(location, EntityType.ARMOR_STAND);
//            armorStand.setGravity(false);
//            armorStand.addScoreboardTag(ENTITY_TAG);
//        }
        // 0 4 9 13
        loadCars();
    }

    public void reload() {
        unloadCars();
        loadCars();
    }

    public void loadCars() {
        PassengerCar[] cars = new PassengerCar[]{
                new PassengerCar(new Location(Worlds.Feb2023(), 99, 69, -2115).add(OFFSET), 0),
                new PassengerCar(path[3].clone().add(0, 1, 0), 4),
                new PassengerCar(path[8], 9),
                new PassengerCar(path[13].clone().add(0, 1, 0), 14)
        };
        carTasks = new BukkitTask[cars.length];
        for (int i = 0; i < cars.length; i++) {
            PassengerCar car = cars[i];
            Bukkit.getPluginManager().registerEvents(car, plugin);
            carTasks[i] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                int nextLocation = car.getNextLocation();
                boolean pastPoint = car.moveToLocation(path[nextLocation]);
                if (pastPoint) {
                    nextLocation++;
                    if (nextLocation >= path.length) {
                        nextLocation = 0;
                    }
                    car.setNextLocation(nextLocation);
                }
            }, 0, 1);
        }
    }

    public void unloadCars() {
        for (BukkitTask task : carTasks) {
            task.cancel();
        }
        List<Entity> entities = Bukkit.selectEntities(sender, String.format("@e[tag=%s]", ENTITY_TAG));
        for (Entity entity : entities) {
            entity.remove();
        }
    }

    public class PassengerCar implements Listener {

        public static final String SEAT_TAG = "FerrisWheelCar";
        public static final Vector HOOK_OFFSET = new Vector(0.0d, 1.66d, 0.0d);

        // 101.67 66.00 -2114.74
        // 102.28 65.34 -2115.84
        public static final Vector[] SEAT_OFFSETS = new Vector[]{
                new Vector(0.61d, -0.66d, -1.26d),
                new Vector(-0.61d, -0.66d, -1.26d),
                new Vector(-0.61d, -0.66d, 1.26d),
                new Vector(0.61d, -0.66d, 1.26d),
        };

        /**
         * Speed in Blocks per Second
         */
        public static final double SPEED = 0.5;

        private static final double BLOCKS_PER_SEC_CONVERSION = 0.1d;

        public static ArmorStand summonArmorStand(Location location) {
            World world = Worlds.Feb2023();
            ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
            armorStand.setGravity(false);
            armorStand.addScoreboardTag(ENTITY_TAG);
            armorStand.setInvisible(true);
            armorStand.addDisabledSlots(EquipmentSlot.values());
            armorStand.setInvulnerable(true);
            return armorStand;
        }

        private final ArmorStand hook;
        private final ArmorStand car;
        private final ArmorStand[] seats;

        private int nextLocation;

        public PassengerCar(Location startingLocation, int nextLocation) {
            hook = summonArmorStand(startingLocation.clone().add(HOOK_OFFSET));
            hook.setItem(EquipmentSlot.HEAD, new ItemStack(Material.HEART_OF_THE_SEA) {{
                ItemMeta meta = getItemMeta();
                meta.setCustomModelData(2);
                setItemMeta(meta);
            }});
            hook.setRotation(-90, 0);
            car = summonArmorStand(startingLocation);
            car.setItem(EquipmentSlot.HEAD, new ItemStack(Material.HEART_OF_THE_SEA) {{
                ItemMeta meta = getItemMeta();
                meta.setCustomModelData(1);
                setItemMeta(meta);
            }});
            car.setRotation(-90, 0);
//            car.addScoreboardTag(SEAT_TAG);

            seats = new ArmorStand[SEAT_OFFSETS.length];
            for (int i = 0; i < SEAT_OFFSETS.length; i++) {
                Location seatLocation = startingLocation.clone().add(SEAT_OFFSETS[i]);
                if (seatLocation.getZ() > startingLocation.getZ()) {
                    seatLocation.setYaw(180.0f);
                }
                seats[i] = summonArmorStand(seatLocation);
                seats[i].addScoreboardTag(SEAT_TAG);
            }

            this.nextLocation = nextLocation;
        }

        private void moveEntity(Entity entity, Vector movement) {
            Bukkit.dispatchCommand(sender, String.format("execute as %s at @s run tp @s ~%f ~%f ~%f", entity.getUniqueId(), movement.getX(), movement.getY(), movement.getZ()));
        }

        public boolean moveToLocation(Location location) {
            Location currentLocation = car.getLocation();
            Vector direction = location.toVector().subtract(currentLocation.toVector());
            direction.multiply(1 / direction.length());
            Vector movement = direction.clone().multiply(SPEED * BLOCKS_PER_SEC_CONVERSION);
//            Location toLocation = currentLocation.add(movement);
            moveEntity(hook, movement);
            moveEntity(car, movement);
            for (ArmorStand seat : seats) {
                moveEntity(seat, movement);
            }
            currentLocation = car.getLocation();
            Vector newDirection = location.toVector().subtract(currentLocation.toVector());
            newDirection.multiply(1 / newDirection.length());
            return !direction.equals(newDirection);
        }

        public int getNextLocation() {
            return nextLocation;
        }

        public void setNextLocation(int nextLocation) {
            this.nextLocation = nextLocation;
        }

        @EventHandler
        public void onEntityInteract(final PlayerInteractAtEntityEvent event) {
            if (event.getHand().equals(EquipmentSlot.HAND)) {
                Player player = event.getPlayer();
                Entity entity = event.getRightClicked();
                if (entity.getScoreboardTags().contains(SEAT_TAG)) {
                    entity.addPassenger(player);
                }
            }
        }
    }

}
