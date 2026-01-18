package me.tpcoffline.blocksurfers.game;


import me.tpcoffline.blocksurfers.game.commands.TestCommand;
import me.tpcoffline.blocksurfers.game.parkour.ParkourGenerator;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;

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


        // Sunucuyu Aç
        minecraftServer.start("0.0.0.0", 25565);
    }
}