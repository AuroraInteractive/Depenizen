package net.gnomeffinway.depenizen.objects.worldguard;

import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.aufdemrand.denizen.objects.*;
import net.aufdemrand.denizen.tags.Attribute;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.gnomeffinway.depenizen.support.Supported;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorldGuardRegion implements dObject {

    /////////////////////
    //   PATTERNS
    /////////////////

    final static Pattern regionPattern = Pattern.compile("(?:region@)?(.+),(.+)", Pattern.CASE_INSENSITIVE);

    /////////////////////
    //   OBJECT FETCHER
    /////////////////

    @Fetchable("region")
    public static WorldGuardRegion valueOf(String string) {
        if (string == null) return null;

        Matcher m = regionPattern.matcher(string);
        if (m.matches()) {
            String regionName = m.group(1);
            String worldName = m.group(2);
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                dB.echoError("valueOf WorldGuard region returning null: Invalid world '" + worldName + "'");
                return null;
            }
            WorldGuardPlugin plugin = Supported.get("WORLDGUARD").getPlugin();
            RegionManager manager = plugin.getRegionManager(world);
            if (!manager.hasRegion(regionName)) {
                dB.echoError("valueOf WorldGuard region returning null: Invalid region '" + regionName
                        + "' for world '" + worldName + "'");
                return null;
            }
        }

        return null;
    }

    public static boolean matches(String arg) {
        return regionPattern.matcher(arg).matches();
    }


    /////////////////////
    //   STATIC CONSTRUCTORS
    /////////////////

    ProtectedRegion region = null;
    World world = null;

    public WorldGuardRegion(ProtectedRegion region, World world) {
        this.region = region;
        this.world = world;
    }


    /////////////////////
    //   INSTANCE FIELDS/METHODS
    /////////////////

    public ProtectedRegion getRegion() {
        return region;
    }


    /////////////////////
    //   dObject Methods
    /////////////////

    private String prefix = "Region";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public dObject setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public String debug() {
        return (prefix + "='<A>" + identify() + "<G>' ");
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    @Override
    public String getObjectType() {
        return "WorldGuardRegion";
    }

    @Override
    public String identify() {
        return "region@" + region.getId() + "," + world.getName();
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public String getAttribute(Attribute attribute) {

        // <--[tag]
        // @attribute <region@region.as_cuboid>
        // @returns dCuboid
        // @group conversion
        // @description
        // Converts a cuboid-shaped region to a dCuboid.
        // @plugin WorldGuard
        // -->
        if (attribute.startsWith("as_cuboid")) {
            if (!(region instanceof ProtectedCuboidRegion)) {
                dB.echoError("<region@region.as_cuboid> requires a Cuboid-shaped region!");
                return Element.NULL.getAttribute(attribute.fulfill(1));
            }
            return new dCuboid(BukkitUtil.toLocation(world, region.getMinimumPoint()),
                    BukkitUtil.toLocation(world, region.getMaximumPoint())).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <region@region.id>
        // @returns Element
        // @description
        // Gets the ID name of the region.
        // -->
        if (attribute.startsWith("id")) {
            return new Element(region.getId()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <region@region.world>
        // @returns dWorld
        // @description
        // Gets the dWorld this region is in.
        // -->
        if (attribute.startsWith("world")) {
            return new dWorld(world).getAttribute(attribute.fulfill(1));
        }

        return new Element(identify()).getAttribute(attribute.fulfill(1));

    }

}