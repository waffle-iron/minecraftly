package com.minecraftly.core.bungee.handlers.module;

import com.minecraftly.core.bungee.MinecraftlyBungeeCore;
import com.minecraftly.core.packets.LocationContainer;
import com.minecraftly.core.packets.PacketTeleport;
import com.sk89q.intake.Command;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.Map;

/**
 * Created by Keir on 05/04/2015.
 */
public class SpawnHandler {

    private MinecraftlyBungeeCore minecraftly;
    private ServerInfo spawnServer;
    private LocationContainer spawnLocation;

    public SpawnHandler(MinecraftlyBungeeCore minecraftly) {
        this.minecraftly = minecraftly;

        Configuration configuration = minecraftly.getConfiguration().getSection("spawn");
        spawnServer = minecraftly.getProxy().getServerInfo(configuration.getString("server"));

        if (spawnServer == null) {
            throw new IllegalArgumentException("Invalid spawn server.");
        }

        spawnLocation = new LocationContainer((Map<String, Object>) configuration.get("location"));
    }

    @Command(aliases = "spawn", desc = "Teleports the player to the main spawn location", max = 0)
    public void spawnCommand(final ProxiedPlayer proxiedPlayer) {
        if (proxiedPlayer.getServer().getInfo().equals(spawnServer)) {
            sendTeleportPacket(proxiedPlayer);
        } else {
            proxiedPlayer.connect(spawnServer, new Callback<Boolean>() {
                @Override
                public void done(Boolean success, Throwable throwable) {
                    if (success) {
                        sendTeleportPacket(proxiedPlayer);
                    }
                }
            });
        }
    }

    private void sendTeleportPacket(ProxiedPlayer proxiedPlayer) {
        minecraftly.getGateway().sendPacket(proxiedPlayer, new PacketTeleport(spawnLocation));
    }

}