package me.tpcoffline.blocksurfers.game.parkour;

import me.tpcoffline.blocksurfers.game.GameState;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;



public class ParkourBlockHandler implements BlockHandler {
    private final ParkourManager generator;
    private final int blockID;

    public ParkourBlockHandler(ParkourManager generator, int blockID) {
        this.generator = generator;
        this.blockID = blockID;
    }

    public void onStep(Player player, Pos blockPos, Block block) {
        Instance instance = player.getInstance();

        if(generator.getState() == GameState.WAITING && !(player.getInstance().getBlock(player.getPosition().sub(0,1,0)).compare(Block.DIAMOND_BLOCK))){
            generator.startGame(instance);
        }

        // eğer önünde 5 bloktan az blok varsa yeni bir blok oluşturuyor
        int frontBlockCount = generator.getBlockCount() - blockID;
        while(frontBlockCount < 5){
            generator.generateNextBlock(instance);
            frontBlockCount++;
        }

        // bloğun tekrar yeni blok çağırmaması için handlerını kaldırıyor
        instance.setBlock(blockPos, block.withHandler(null));
    }


    @Override
    public Key getKey() {
        return Key.key("blocksurfers:parkour_block");
    }
}
