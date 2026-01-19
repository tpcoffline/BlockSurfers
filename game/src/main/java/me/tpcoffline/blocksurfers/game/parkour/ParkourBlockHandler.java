package me.tpcoffline.blocksurfers.game.parkour;

import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


public class ParkourBlockHandler implements BlockHandler {
    private final ParkourGenerator generator;
    private final int blockID;

    public ParkourBlockHandler(ParkourGenerator generator, int blockID) {
        this.generator = generator;
        this.blockID = blockID;
    }


    public void onStep(Player player, Pos blockPos, Block block) {
        Instance instance = player.getInstance();
        if((ParkourGenerator.getBlockCount() - blockID) < 5){
            generator.generateNextBlock(instance);
        }
        final var scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(() -> instance.setBlock(blockPos, Block.AIR)).delay(Duration.ofSeconds(10)).schedule();
        instance.setBlock(blockPos, block.withHandler(null));


    }


    @Override
    public Key getKey() {
        return Key.key("blocksurfers:parkour_block");
    }
}
