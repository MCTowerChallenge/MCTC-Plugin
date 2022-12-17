//// https://www.spigotmc.org/threads/serializing-spigot-entities-and-nbt-data-easily.457784/
//
//package io.github.idkahn.towerchallenge.misc.entitybottles;
//
//import com.mojang.brigadier.exceptions.CommandSyntaxException;
//import io.github.idkahn.towerchallenge.TowerChallenge;
//import org.bukkit.Location;
//import org.bukkit.World;
//import org.bukkit.entity.Entity;
//import org.bukkit.util.io.BukkitObjectInputStream;
//import org.bukkit.util.io.BukkitObjectOutputStream;
//import org.jetbrains.annotations.Nullable;
//
//import java.io.*;
//import java.util.UUID;
//import java.util.zip.GZIPInputStream;
//import java.util.zip.GZIPOutputStream;
//
//public class BottleEntity implements Serializable {
//    @Serial
//    private static final long serialVersionUID = -1681012206529286330L;
//
//    private String entity;
//    private UUID uniqueID;
//
//    public BottleEntity(UUID uniqueID, Entity entity) {
//        this.uniqueID = uniqueID;
//        this.entity = this.getNBTCompoundData(entity).toString();
//    }
//
//    public NBTTagCompound getNBTCompoundData(Entity entity) {
//
//        net.minecraft.server.v1_16_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
//        NBTTagCompound compound = new NBTTagCompound();
//        nmsEntity.a_(compound);
//
//        return compound;
//    }
//
//    public Entity getEntityFromNBT(String compound, World world, @Nullable Location location) throws CommandSyntaxException {
//        NBTTagCompound nbtTagCompound = MojangsonParser.parse(compound);
//
//        WorldServer worldServer = ((CraftWorld) world).getHandle();
//
//        net.minecraft.server.v1_16_R1.Entity entity = EntityTypes.a(nbtTagCompound, worldServer, (entity1 -> {
//            entity1.dead = false;
//            UUID uniqueID = entity1.getUniqueID();
//            uniqueID = UUID.randomUUID();
//
//            if (location != null) {
//                entity1.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
//            }
//
//            // dead = false
//            // uniqueID must be unique
//
//            return !worldServer.addEntitySerialized(entity1) ? null : entity1;
//
//        }));
//        if (entity != null) {
//            if (entity instanceof EntityInsentient entityInsentient) {
//                entityInsentient.prepare(worldServer, worldServer.getDamageScaler(entity.getChunkCoordinates()), EnumMobSpawn.COMMAND, null, null);
//            }
//            return entity.getBukkitEntity();
//        }
//        return null;
//    }
//
//    public boolean saveData() {
//        try {
//            File targetDir = new File(TowerChallenge.me.getDataFolder(), "BottleEntities");
//            File targetFile = new File(targetDir.getPath() + File.separator + uniqueID + ".gz");
//            targetFile.createNewFile();
//
//            FileOutputStream outputStream = new FileOutputStream(targetFile, false);
//            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
//            BukkitObjectOutputStream out = new BukkitObjectOutputStream(gzipOutputStream);
//
//            out.writeObject(this);
//            out.close();
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public static BottleEntity loadData(UUID uniqueID) throws IllegalArgumentException {
//        try {
//            FileInputStream fileInputStream = new FileInputStream(TowerChallenge.me.getDataFolder() + File.separator + "BottleEntities" + File.separator + uniqueID + ".gz");
//            GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
//            BukkitObjectInputStream in = new BukkitObjectInputStream(gzipInputStream);
//
//            BottleEntity ex = (BottleEntity) in.readObject();
//
//            in.close();
//            return ex;
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public void setEntity(String entity) {
//        this.entity = entity;
//    }
//
//    public String getEntity() {
//        return entity;
//    }
//
//    public Entity getBukkitEntity(World world, Location location) {
//        try {
//            return this.getEntityFromNBT(this.entity, world, location);
//        } catch (CommandSyntaxException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//}
