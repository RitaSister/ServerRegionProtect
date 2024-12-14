package net.ritasister.wgrp.loader;

import net.ritasister.wgrp.WorldGuardRegionProtectPaperPlugin;
import net.ritasister.wgrp.api.platform.Platform;
import net.ritasister.wgrp.util.utility.VersionCheck;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class WGRPCompatibilityCheck {

    private final WorldGuardRegionProtectPaperPlugin wgrpPlugin;
    private static String PLATFORM_NAME;

    public WGRPCompatibilityCheck(final @NotNull WorldGuardRegionProtectPaperPlugin wgrpPlugin) {
        this.wgrpPlugin = wgrpPlugin;
    }

    public void checkStartUpVersionServer() {
        if (!wgrpPlugin.getVersionCheck().isVersionSupported()) {
            wgrpPlugin.getLogger().severe(String.format(
                    """
                            \n====================================================
                            
                                    WorldGuardRegionProtect only works on %s!
                                    Please refer to this thread: https://www.spigotmc.org/resources/81321/
                                    Visit the main post on SpigotMC and download the correct version of the plugin for your server version.
                            
                            ====================================================
                            """, VersionCheck.SUPPORTED_VERSION_RANGE
            ));
            Bukkit.getServer().getPluginManager().disablePlugin(wgrpPlugin.getWgrpPaperBase());
        }
    }

    public void detectWhatIsPlatformRun() {
        final String minecraftVersion = Bukkit.getBukkitVersion().split("-")[0];
        final String pluginVersion = wgrpPlugin.getWgrpPaperBase().getDescription().getVersion();
        final Platform.Type platformName = wgrpPlugin.getType();
        final String className;
        final Platform.Type detectedPlatform;

        if (platformName == Platform.Type.PAPER) {
            className = "com.destroystokyo.paper.ParticleBuilder";
            detectedPlatform = Platform.Type.PAPER;
        } else if (platformName == Platform.Type.FOLIA) {
            className = "io.papermc.paper.threadedregions.RegionizedServer";
            detectedPlatform = Platform.Type.FOLIA;
        } else if (platformName == Platform.Type.BUKKIT) {
            className = "org.spigotmc.SpigotConfig";
            detectedPlatform = Platform.Type.SPIGOT;
        } else if (platformName == Platform.Type.SPIGOT && isRunningOnBukkit()) {
            setPlatformName(Platform.Type.BUKKIT.getPlatformName());
            handleTrustPlatform(pluginVersion, Platform.Type.BUKKIT.getPlatformName(), minecraftVersion);
            return;
        } else {
            handleUnTrustPlatform(pluginVersion, minecraftVersion);
            return;
        }

        detectPlatformByClassName(className, detectedPlatform, pluginVersion, minecraftVersion);
    }

    private void detectPlatformByClassName(String className, Platform.@NotNull Type detectedPlatform, String pluginVersion, String minecraftVersion) {
        try {
            Class.forName(className);
            setPlatformName(detectedPlatform.getPlatformName());
            handleTrustPlatform(pluginVersion, detectedPlatform.getPlatformName(), minecraftVersion);
        } catch (ClassNotFoundException ignored) {
            handleUnTrustPlatform(pluginVersion, minecraftVersion);
        }
    }

    private void handleTrustPlatform(final String pluginVersion, final @NotNull String type, final @NotNull String minecraftVersion) {
        if (type.equals(Platform.Type.BUKKIT.getPlatformName()) || type.equals(Platform.Type.SPIGOT.getPlatformName())) {
            wgrpPlugin.getLogger().warn(String.format(
                    """
                            ====================================================
                            
                                    WorldGuardRegionProtect %s
                                    Server running on %s - %s.
                                    It is recommended to use Paper, Folia or its forks for better performance and support!
                            
                            ====================================================
                            """, pluginVersion, PLATFORM_NAME, minecraftVersion));
        }
        if (type.equals(Platform.Type.PAPER.getPlatformName()) || type.equals(Platform.Type.FOLIA.getPlatformName())) {
            setPlatformName(type);
            wgrpPlugin.getLogger().info(String.format(
                    """
                            ====================================================
                            
                                    WorldGuardRegionProtect %s
                                    Server running on %s - %s.
                                    Your setup is optimal for plugin performance and support.
                            
                            ====================================================
                            """, pluginVersion, PLATFORM_NAME, minecraftVersion));
        }
    }

    private void handleUnTrustPlatform(final String pluginVersion, final @NotNull String minecraftVersion) {
        wgrpPlugin.getLogger().info(String.format(
                """
                        \n====================================================
                        
                                WorldGuardRegionProtect %s
                                Server running on %s - %s.
                        
                                It is recommended to use Paper, Folia or its forks for better performance and support!
                                Avoid using untrusted or unknown server forks.
                                Support is not available for servers running on untrusted implementations.
                        
                        ====================================================
                        """, pluginVersion, Platform.Type.UNKNOWN.getPlatformName(), minecraftVersion));
    }

    public void notifyAboutBuild() {
        wgrpPlugin.getLogger().info(String.format(
                """ 
                        Using %s language version %s. Author of this localization - %s.
                        """,
                wgrpPlugin.getConfigLoader().getMessages().get("langTitle.language"),
                wgrpPlugin.getConfigLoader().getMessages().get("langTitle.version"),
                wgrpPlugin.getConfigLoader().getMessages().get("langTitle.author")
        ));
    }

    private boolean isRunningOnBukkit() {
        return Bukkit.getServer().getName().contains("Bukkit") || Bukkit.getServer().getName().contains("CraftBukkit");
    }

    private static void setPlatformName(final @NotNull String platformName) {
        PLATFORM_NAME = platformName;
    }

    public static String getPlatformName() {
        return PLATFORM_NAME;
    }
}
