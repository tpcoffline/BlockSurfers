package me.tpcoffline.blocksurfers.game;


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

        // 2. Dünya (Instance) Oluştur
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        // Işıklandırma ve Zemin Ayarı (Düz taş zemin)
        instanceContainer.setChunkSupplier(LightingChunk::new);
        instanceContainer.setGenerator(unit -> {});
        instanceContainer.setBlock(0, 41, 0, Block.DIAMOND_BLOCK);

        // 3. Oyuncu Giriş Olayı
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(instanceContainer);
            event.getPlayer().setRespawnPoint(new Pos(0.5, 42, 0.5));
        });

        // 4. Sunucuyu Aç
        minecraftServer.start("0.0.0.0", 25565);
    }
}