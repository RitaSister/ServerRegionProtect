package net.ritasister.wgrp;

import lombok.AllArgsConstructor;
import net.ritasister.wgrp.api.WorldGuardRegionProtect;
import org.bukkit.plugin.java.JavaPlugin;

@AllArgsConstructor
public final class WorldGuardRegionProtectBukkitBase extends JavaPlugin implements WorldGuardRegionProtectBase {

    private WorldGuardRegionProtectBukkitPlugin worldGuardRegionProtectBukkitPlugin;

    @Override
    public void onEnable() {
        worldGuardRegionProtectBukkitPlugin = new WorldGuardRegionProtectBukkitPlugin(this);
    }

    @Override
    public void onDisable() {
        worldGuardRegionProtectBukkitPlugin.unLoad();
    }

    @Override
    public WorldGuardRegionProtect getApi() {
        return worldGuardRegionProtectBukkitPlugin;
    }

}
