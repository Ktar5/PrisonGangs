/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of gangs.
 * 
 * gangs can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.gangs.chunk;

import com.minecave.gangs.Gangs;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ChunkListener implements Listener {

    private final Gangs plugin;

    public ChunkListener(Gangs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkMove(PlayerMoveEvent event) {
        if(event.getFrom().getChunk().equals(event.getTo().getChunk())) {
            return;
        }
        Player player = event.getPlayer();
        Chunk chunk = event.getTo().getChunk();
        if(plugin.getGangCoordinator().isChunkClaimed(chunk)) {
            player.sendMessage();
            return;
        }
    }
}
