package me.tpcoffline.blocksurfers.game;


import me.tpcoffline.blocksurfers.game.commands.TestCommand;
import me.tpcoffline.blocksurfers.game.parkour.ParkourBlockHandler;
import me.tpcoffline.blocksurfers.game.parkour.ParkourGenerator;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;

import java.util.function.Predicate;

public class GameServer {
    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init(new Auth.Online());

        // Instance Oluştur
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        // Işıklandırma ve elmas blok
        instanceContainer.setChunkSupplier(LightingChunk::new);
        instanceContainer.setGenerator(unit -> {});
        instanceContainer.setBlock(0, 41, 0, Block.DIAMOND_BLOCK);


        // Oyuncu Giriş eventi
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(instanceContainer);
            event.getPlayer().setRespawnPoint(new Pos(0.5, 42, 0.5));
        });

        // test komutu
        ParkourGenerator parkourGenerator = new ParkourGenerator();
        MinecraftServer.getCommandManager().register(new TestCommand(parkourGenerator));


        // handlerlı bloğa bastığını kontrol etme
        var playerStepBlockNode = EventNode.value("player_step_block", EventFilter.PLAYER, Player::isOnGround);
        playerStepBlockNode.addListener(EventListener.builder(PlayerMoveEvent.class)
                .filter(event -> event.getPlayer().getInstance().getBlock(event.getNewPosition().sub(0,1,0)).handler() instanceof ParkourBlockHandler)
                .handler(event -> {
                    var instance = event.getPlayer().getInstance();
                    var blockPos = event.getNewPosition().sub(0, 1, 0);
                    var block = instance.getBlock(blockPos);

                    ParkourBlockHandler handler = (ParkourBlockHandler) block.handler();
                    assert handler != null;
                    handler.onStep(event.getPlayer(),blockPos, block);

                })
                .build());
        globalEventHandler.addChild(playerStepBlockNode);




        // Sunucuyu Aç
        minecraftServer.start("0.0.0.0", 25565);
    }
}