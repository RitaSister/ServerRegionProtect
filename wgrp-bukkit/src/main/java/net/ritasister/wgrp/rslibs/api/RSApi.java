package net.ritasister.wgrp.rslibs.api;

import lombok.extern.slf4j.Slf4j;
import net.ritasister.wgrp.WGRPContainer;
import net.ritasister.wgrp.WorldGuardRegionProtect;
import net.ritasister.wgrp.rslibs.checker.EntityCheckType;
import net.ritasister.wgrp.rslibs.checker.EntityCheckTypeProvider;
import net.ritasister.wgrp.rslibs.permissions.UtilPermissions;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Utility api class for other classes to use the necessary methods and other.
 */
@Slf4j
public class RSApi {

    private final WorldGuardRegionProtect wgRegionProtect;

    private final WGRPContainer wgrpContainer;

    private final EntityCheckTypeProvider entityCheckTypeProvider;

    public RSApi(final WorldGuardRegionProtect wgRegionProtect) {
        this.wgRegionProtect = wgRegionProtect;
        this.wgrpContainer = this.wgRegionProtect.getWgrpContainer();
        this.entityCheckTypeProvider = new EntityCheckTypeProvider(wgrpContainer);
    }

    /**
     * Check if a player has permission for use Listener.
     *
     * @param player who send this command.
     * @param perm   permission to check.
     * @return can a player use this listener.
     */
    public boolean isPlayerListenerPermission(@NotNull Player player, @NotNull UtilPermissions perm) {
        return player.hasPermission(perm.getPermissionName());
    }

    /**
     * Check if an entity has permission for use Listener.
     *
     * @param entity who send this command.
     * @param perm   permission to check.
     * @return can an entity use this listener.
     */
    public boolean isEntityListenerPermission(@NotNull Entity entity, @NotNull UtilPermissions perm) {
        return !entity.hasPermission(perm.getPermissionName());
    }

    /**
     * Send notification to admin.
     *
     * @param player        player object.
     * @param playerName    player name.
     * @param senderCommand name command if player attempt to use in a region.
     * @param regionName    the region name, if player attempts to use command in a region.
     */
    public void notify(Player player, String playerName, String senderCommand, String regionName) {
        if (regionName == null) {
            return;
        }
        if (wgRegionProtect.getWgrpContainer().getConfig().getSpyCommandNotifyAdminEnable() && this.isPlayerListenerPermission(
                player,
                UtilPermissions.REGION_PROTECT_NOTIFY_ADMIN
        )) {
            for (String cmd : wgRegionProtect.getWgrpContainer().getConfig().getSpyCommandList()) {
                if (cmd.equalsIgnoreCase(senderCommand.toLowerCase()) && wgRegionProtect.getWgrpContainer().getConfig().getSpyCommandNotifyAdminPlaySoundEnable()) {
                    player.playSound(player.getLocation(), wgRegionProtect.getWgrpContainer().getConfig().getSpyCommandNotifyAdminPlaySound().toLowerCase(), 1, 1);
                    wgRegionProtect.getWgrpContainer()
                            .getMessages()
                            .get("messages.Notify.sendAdminInfoIfUsedCommandInRG")
                            .replace("<player>", playerName)
                            .replace("<cmd>", cmd)
                            .replace("<region>", regionName).send(player);
                }
            }
        }
    }

    /**
     * Send notify to admin.
     *
     * @param playerName    player name.
     * @param senderCommand name command if Player attempt to use in a region.
     * @param regionName    region name, if Player attempts to use command in a region.
     */
    public void notify(String playerName, String senderCommand, String regionName) {
        if (regionName == null) {
            return;
        }
        if (wgRegionProtect.getWgrpContainer().getConfig().getSpyCommandNotifyConsoleEnable()) {
            for (String cmd : wgRegionProtect.getWgrpContainer().getConfig().getSpyCommandList()) {
                if (cmd.equalsIgnoreCase(senderCommand.toLowerCase())) {
                    ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
                    wgRegionProtect.getWgrpContainer()
                            .getMessages()
                            .get("messages.Notify.sendAdminInfoIfUsedCommandInRG")
                            .replace("<player>", playerName)
                            .replace("<cmd>", cmd)
                            .replace("<region>", regionName).send(consoleSender);
                }
            }
        }
    }

    /**
     * Send a notification to the administrator if Player attempts to interact with a region from WorldGuard.
     *
     * @param admin         message for an admin who destroys a region.
     * @param suspectPlayer object player for method.
     * @param suspectName   player name who interacting with a region.
     * @param action        get the actions.
     * @param regionName    region name.
     * @param x             position of block.
     * @param y             position of block.
     * @param z             position of block.
     * @param world         position of block in world.
     */
    public void notifyIfActionInRegion(
            Player admin,
            Player suspectPlayer,
            String suspectName,
            RegionAction action,
            String regionName,
            double x,
            double y,
            double z,
            String world) {
        if (this.isPlayerListenerPermission(
                suspectPlayer,
                UtilPermissions.SPY_INSPECT_FOR_SUSPECT
        ) && wgRegionProtect.getWgrpContainer().getConfig().getSpyCommandNotifyAdminEnable()) {
            wgRegionProtect.getWgrpContainer()
                    .getMessages()
                    .get("messages.Notify.sendAdminInfoIfActionInRegion")
                    .replace("<player>", suspectName)
                    .replace("<action>", action.getAction())
                    .replace("<region>", regionName)
                    .replace("<x>", String.valueOf(x))
                    .replace("<y>", String.valueOf(y))
                    .replace("<z>", String.valueOf(z))
                    .replace("<world>", world).send(admin);
        }
    }

    /**
     * Initializes all used NMS classes, constructors, fields and methods.
     * Returns {@code true} if everything went successfully and version marked as compatible,
     * {@code false} if anything went wrong or version not marked as compatible.
     *
     * @return {@code true} if server version compatible, {@code false} if not
     */
    public boolean isVersionSupported() {
        List<String> supportedVersions = List.of("v1_20_R1");
        String supportedVersionRange = "1.20";
        String serverPackage = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            long time = System.currentTimeMillis();
            if (supportedVersions.contains(serverPackage)) {
                log.info("Loaded NMS hook in " + (System.currentTimeMillis() - time) + "ms");
                return true;
            } else {
                log.info(
                        "No compatibility issue was found, but this plugin version does not claim to support your server package (" + serverPackage + ").");
            }
        } catch (Exception ex) {
            if (supportedVersions.contains(serverPackage)) {
                log.error(
                        "Your server version is marked as compatible, but a compatibility issue was found. Please report the error below (include your server version & fork too)");
            } else {
                log.error("Your server version is completely unsupported. This plugin version only " +
                        "supports " + supportedVersionRange + ". Disabling.");
            }
        }
        return false;
    }

    public void entityCheck(Cancellable cancellable, Entity entity, @NotNull Entity checkEntity) {
        if (!wgrpContainer.getRsRegion().checkStandingRegion(checkEntity.getLocation(), wgrpContainer.getConfig().getRegionProtectMap())
                || !wgrpContainer.getRsApi().isEntityListenerPermission(entity, UtilPermissions.REGION_PROTECT)) {
            return;
        }
        EntityCheckType entityCheckType = entityCheckTypeProvider.getCheck(checkEntity);
        if(entityCheckType.check(checkEntity)) {
            cancellable.setCancelled(true);
        }
    }

}
