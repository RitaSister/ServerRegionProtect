package net.ritasister.wgrp.util.utility;

import net.ritasister.wgrp.WorldGuardRegionProtectPaperPlugin;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.List;

public class VersionCheck {

    private final WorldGuardRegionProtectPaperPlugin wgrpPlugin;

    public VersionCheck(final WorldGuardRegionProtectPaperPlugin plugin) {
        this.wgrpPlugin = plugin;
    }

    public final static String SUPPORTED_VERSION_RANGE = "1.20 - 1.21.4";
    public final static List<String> SUPPORTED_VERSION = Arrays.asList(
            "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6",
            "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4"
    );

    /**
     * Initializes all used NMS classes, constructor fields, and methods.
     * Returns {@code true} if everything went successfully and version marked as compatible,
     * {@code false} if anything went wrong or version not marked as compatible.
     *
     * @return {@code true} if server version compatible, {@code false} if not
     */
    public boolean isVersionSupported() {
        final String minecraftVersion = Bukkit.getBukkitVersion().split("-")[0];
        try {
            final long time = System.currentTimeMillis();
            if (SUPPORTED_VERSION.contains(minecraftVersion)) {
                wgrpPlugin.getLogger().info("Loaded NMS hook in " + (System.currentTimeMillis() - time) + "ms");
                wgrpPlugin.getLogger().info(String.format("Current support versions range %s", SUPPORTED_VERSION_RANGE));
                return true;
            } else {
                wgrpPlugin.getLogger().info(
                        "No compatibility issue was found, but this plugin version does not claim to support your server package (" + minecraftVersion + ").");
            }
        } catch (Exception ignored) {
            if (SUPPORTED_VERSION.contains(minecraftVersion)) {
                wgrpPlugin.getLogger().severe(
                        "Your server version is marked as compatible, but a compatibility issue was found. Please report the error below (include your server version & fork too)");
            } else {
                wgrpPlugin.getLogger().severe(
                        "Your server version is completely unsupported. This plugin version only " +
                                "supports " + SUPPORTED_VERSION_RANGE + ". Disabling.");
            }
        }
        return false;
    }

}
