package me.tpcoffline.blocksurfers.game.parkour;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.Random;

public class ParkourGenerator {


    private static Pos lastPosition = new Pos(0,41,0);
    private static int blockCount;    private final Random random = new Random();


    public void generateNextBlock(Instance instance) {

        // tüm yönlerden rastgele al
        int nextX = random.nextInt(-3, 4);
        int nextY = random.nextInt(2);
        int nextZ = random.nextInt(2, 4);

        // yeni bloğu koy ve koordinatı kaydet
        Pos newPosition = lastPosition.add(nextX, nextY, nextZ);
        blockCount++;
        instance.setBlock(newPosition, Block.STONE.withHandler(new ParkourBlockHandler(this,blockCount)));
        lastPosition = newPosition;

    }

    public static int getBlockCount() {
        return blockCount;
    }




}

