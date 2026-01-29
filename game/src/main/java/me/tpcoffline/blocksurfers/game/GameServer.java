package me.tpcoffline.blocksurfers.game;


import me.tpcoffline.blocksurfers.game.commands.TestCommand;
import me.tpcoffline.blocksurfers.game.parkour.ParkourBlockHandler;
import me.tpcoffline.blocksurfers.game.parkour.ParkourManager;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class GameServer {
    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init(new Auth.Online());

        // Instance Oluştur
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        // parkur manager oluştur
        ParkourManager parkourManager = new ParkourManager();

        // Dünya ayarları
        instanceContainer.setChunkSupplier(LightingChunk::new);
        instanceContainer.setBlock(0, 41, 0, Block.DIAMOND_BLOCK.withHandler(new ParkourBlockHandler(parkourManager,0)));
        instanceContainer.setTimeRate(0);
        instanceContainer.setTime(6000);

        // Oyuncu Giriş eventi
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(instanceContainer);
            event.getPlayer().setRespawnPoint(new Pos(0.5, 42, 0.5));
        });



        // test komutu
        MinecraftServer.getCommandManager().register(new TestCommand(parkourManager));


        // TODO: Oyuncu eventlerini düzgünce tek node'a taşı ve başka classa taşı

        // handlerlı bloğa bastığını kontrol etme
        var playerStepBlockNode = EventNode.type("player_step_block", EventFilter.PLAYER);
        playerStepBlockNode.addListener(EventListener.builder(PlayerMoveEvent.class)
                .filter(event -> event.getInstance().getBlock(event.getNewPosition().sub(0,1,0)).handler() instanceof ParkourBlockHandler)
                .handler(event -> {
                    var instance = event.getInstance();
                    var blockPos = event.getNewPosition().sub(0, 1, 0);
                    var block = instance.getBlock(blockPos);

                    ParkourBlockHandler handler = (ParkourBlockHandler) block.handler();
                    assert handler != null;
                    handler.onStep(event.getPlayer(),blockPos, block);

                })
                .build());
        globalEventHandler.addChild(playerStepBlockNode);

        // kaybetme
        var playerLoseNode = EventNode.value("player_lose", EventFilter.PLAYER, Predicate.not(Player::isOnGround));
        playerLoseNode.addListener(EventListener.builder(PlayerMoveEvent.class)
                .filter(event -> ((int) event.getPlayer().getPosition().y()) < 32)
                .handler(event -> parkourManager.reset(event.getInstance(),event.getPlayer()))
                .build());
        globalEventHandler.addChild(playerLoseNode);


        // ölmeyi kapat
        var entityDamageNode = EventNode.type("entity_damaged",EventFilter.ENTITY);
        entityDamageNode.addListener(EventListener.builder(EntityDamageEvent.class).handler(event -> event.setCancelled(true)).build());
        globalEventHandler.addChild(entityDamageNode);


        // --- DEV INFO BOSSBAR  ---

        java.util.concurrent.atomic.AtomicReference<Double> lastMspt = new java.util.concurrent.atomic.AtomicReference<>(0.0);

        globalEventHandler.addListener(net.minestom.server.event.server.ServerTickMonitorEvent.class, event -> {
            lastMspt.set(event.getTickMonitor().getTickTime());
        });

        net.kyori.adventure.bossbar.BossBar devStatsBar = net.kyori.adventure.bossbar.BossBar.bossBar(
                net.kyori.adventure.text.Component.empty(),
                1f,
                net.kyori.adventure.bossbar.BossBar.Color.GREEN,
                net.kyori.adventure.bossbar.BossBar.Overlay.PROGRESS
        );

        globalEventHandler.addListener(net.minestom.server.event.player.PlayerSpawnEvent.class, event -> {
            event.getPlayer().showBossBar(devStatsBar);
        });

        MinecraftServer.getSchedulerManager().buildTask(() -> {
            double mspt = lastMspt.get();
            Runtime runtime = Runtime.getRuntime();
            long usedMemBytes = runtime.totalMemory() - runtime.freeMemory();
            long usedMemMB = usedMemBytes / 1024 / 1024;

            // Yazı formatı: "mspt: 15.20ms - ram usage: 350MB"
            String displayText = String.format(
                    "<gray>mspt:</gray> <green>%.2fms</green> <gray>-</gray> <gray>ram usage:</gray> <light_purple>%dMB</light_purple>",
                    mspt, usedMemMB
            );

            devStatsBar.name(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(displayText));

            float progress = (float) usedMemBytes / runtime.maxMemory();
            devStatsBar.progress(Math.max(0.0f, Math.min(1.0f, progress)));

        }).repeat(java.time.Duration.ofMillis(250)).schedule();



        // Sunucuyu Aç
        minecraftServer.start("0.0.0.0", 25565);
    }
}