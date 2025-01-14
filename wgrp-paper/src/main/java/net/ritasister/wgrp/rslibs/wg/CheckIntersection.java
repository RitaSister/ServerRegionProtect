package net.ritasister.wgrp.rslibs.wg;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.ritasister.wgrp.WorldGuardRegionProtectPaperPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CheckIntersection {

    public final WorldGuardRegionProtectPaperPlugin wgrpPlugin;

    public CheckIntersection(final WorldGuardRegionProtectPaperPlugin wgrpPlugin) {
        this.wgrpPlugin = wgrpPlugin;
    }

    public boolean checkIntersection(final Player player) {
        try {
            final LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
            final Region selection = localSession.getSelection(BukkitAdapter.adapt(player.getWorld()));
            return checkIntersection(selection, player);
        } catch (IncompleteRegionException | NullPointerException ex) {
            return false;
        }
    }

    private boolean checkIntersection(final Region selection, Player player) {
        if (selection instanceof CuboidRegion) {
            final BlockVector3 min = selection.getMinimumPoint();
            final BlockVector3 max = selection.getMaximumPoint();
            final RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
            final RegionManager regions = rc.get(selection.getWorld());
            final ProtectedRegion __dummy__ = new ProtectedCuboidRegion("__dummy__", min, max);
            assert regions != null;
            final ApplicableRegionSet set = regions.getApplicableRegions(__dummy__);
            final List<String> regionProtectList = wgrpPlugin.getConfigLoader()
                    .getConfig()
                    .getRegionProtectMap()
                    .get(player.getWorld().getName());

            if (regionProtectList != null) {
                for (final ProtectedRegion rg : set) {
                    if (regionProtectList.stream().anyMatch(region -> rg.getId().equalsIgnoreCase(region))) {
                        return false;
                    }
                }
            }
            for (final ProtectedRegion rg : set) {
                final List<String> breakAllowRegions = wgrpPlugin.getConfigLoader()
                        .getConfig()
                        .getRegionProtectOnlyBreakAllowMap()
                        .get(player.getWorld().getName());

                if (breakAllowRegions != null && breakAllowRegions.stream().anyMatch(region -> rg.getId().equalsIgnoreCase(region))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean checkCIntersection(final Player player, final String... args) {
        final Region selection = this.getCylSelection(player, args);
        return this.checkIntersection(selection, player);
    }

    public boolean checkPIntersection(final Player player, final String... args) {
        final Region selection = this.getPyramidSelection(player, args);
        return this.checkIntersection(selection, player);
    }

    public boolean checkSIntersection(final Player player, final String... args) {
        final Region selection = this.getSphereSelection(player, args);
        return this.checkIntersection(selection, player);
    }

    public boolean checkUIntersection(final Player player, final String... args) {
        final Region selection = this.getUpSelection(player, args);
        return this.checkIntersection(selection, player);
    }

    public boolean checkCPIntersection(final Player player) {
        final Region selection = this.getPasteSelection(player);
        return this.checkIntersection(selection, player);
    }

    @Contract("_, _ -> new")
    private @NotNull CuboidRegion getCylSelection(final Player player, final String... args) {
        int x = 1;
        int y = 1;
        int z = 0;
        try {
            if (args[2].contains(",")) {
                x = Integer.parseInt(args[2].split(",")[0]);
                z = Integer.parseInt(args[2].split(",")[1]);
            } else {
                x = Integer.parseInt(args[2]);
            }
            y = Integer.parseInt(args[3]);
        } catch (Exception ignored) {
        }
        final Location loc1 = player.getLocation().subtract(x, y, z);
        final Location loc2 = player.getLocation().add(x, y, z);
        return new CuboidRegion(
                BukkitAdapter.adapt(player.getWorld()),
                BukkitAdapter.asVector(loc1).toBlockPoint(),
                BukkitAdapter.asVector(loc2).toBlockPoint()
        );
    }

    private @Nullable CuboidRegion getPyramidSelection(final Player player, final String... args) {
        if (args.length < 3) {
            return null;
        }
        int i = 1;
        try {
            i = Integer.parseInt(args[2]);
        } catch (Exception ignored) {
        }
        final Location loc1 = player.getLocation().subtract(i, i, i);
        final Location loc2 = player.getLocation().add(i, i, i);
        return new CuboidRegion(
                BukkitAdapter.adapt(player.getWorld()),
                BukkitAdapter.asVector(loc1).toBlockPoint(),
                BukkitAdapter.asVector(loc2).toBlockPoint()
        );
    }

    private @Nullable CuboidRegion getSphereSelection(final Player player, final String... args) {
        if (args.length < 3) {
            return null;
        }
        final String[] cr = args[2].split(",");
        int y2;
        int z2;
        final int x2 = z2 = y2 = Integer.parseInt(cr[0]);
        try {
            y2 = Integer.parseInt(cr[1]);
            z2 = Integer.parseInt(cr[2]);
        } catch (Exception ignored) {
        }
        final Location loc1 = player.getLocation().subtract(x2, y2, z2);
        final Location loc2 = player.getLocation().add(x2, y2, z2);
        return new CuboidRegion(
                BukkitAdapter.adapt(player.getWorld()),
                BukkitAdapter.asBlockVector(loc1),
                BukkitAdapter.asBlockVector(loc2)
        );
    }

    private @Nullable CuboidRegion getUpSelection(final Player player, final String... args) {
        try {
            final int v = Integer.parseInt(args[1]);
            final Location loc1 = player.getLocation().add(0.0, v, 0.0);
            final Location loc2 = player.getLocation().add(0.0, v, 0.0);
            return new CuboidRegion(
                    BukkitAdapter.adapt(player.getWorld()),
                    BukkitAdapter.asBlockVector(loc1),
                    BukkitAdapter.asBlockVector(loc2)
            );
        } catch (Exception ex) {
            return null;
        }
    }

    private @Nullable CuboidRegion getPasteSelection(final Player player) {
        try {
            final LocalSession session = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
            final ClipboardHolder holder = session.getClipboard();
            final Clipboard clipboard = holder.getClipboard();
            final BlockVector3 to = session.getPlacementPosition(BukkitAdapter.adapt(player));
            final BlockVector3 min = to.add(clipboard.getRegion().getMinimumPoint().subtract(clipboard.getOrigin()));
            final BlockVector3 max = to.add(clipboard.getRegion().getMaximumPoint().subtract(clipboard.getOrigin()));
            return new CuboidRegion(BukkitAdapter.adapt(player.getWorld()), min, max);
        } catch (Exception e) {
            return null;
        }
    }

}
