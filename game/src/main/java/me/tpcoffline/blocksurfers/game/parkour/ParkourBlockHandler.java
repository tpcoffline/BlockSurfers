package me.tpcoffline.blocksurfers.game.parkour;

import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;

import java.time.Duration;


public class ParkourBlockHandler implements BlockHandler {
    private final ParkourManager generator;
    private final int blockID;

    public ParkourBlockHandler(ParkourManager generator, int blockID) {
        this.generator = generator;
        this.blockID = blockID;
    }


    public void onStep(Player player, Pos blockPos, Block block) {
        Instance instance = player.getInstance();
        // eğer önünde 5 bloktan az blok varsa yeni bir blok oluşturuyor
        if((generator.getBlockCount() - blockID) < 5){
            generator.generateNextBlock(instance);
        }
        // 10 saniye sonra bloğu ve listeden koordinatı siliyor
        final var scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(() -> {
            instance.setBlock(blockPos, Block.AIR);
            generator.removeBlock(blockPos);
        }).delay(Duration.ofSeconds(10)).schedule();
        // bloğun tekrar yeni blok çağırmaması için handlerını kaldırıyor
        instance.setBlock(blockPos, block.withHandler(null));


    }


    @Override
    public Key getKey() {
        return Key.key("blocksurfers:parkour_block");
    }
}
