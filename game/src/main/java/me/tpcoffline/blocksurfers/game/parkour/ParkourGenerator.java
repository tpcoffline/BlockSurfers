package me.tpcoffline.blocksurfers.game.parkour;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.Random;

public class ParkourGenerator {
    protected Pos lastPosition = new Pos(0,41,0);
    private final Random random = new Random();
    private int nextX;
    private int nextY;
    private int nextZ;

    public void generateNextBlock(Instance instance) {

        // tüm yönlerden rastgele al
        nextX = random.nextInt(-3,4);
        nextY = random.nextInt(2);
        nextZ = random.nextInt(2,4);

        // yeni bloğu koy ve koordinatı kaydet
        Pos newPosition = lastPosition.add(nextX,nextY,nextZ);
        instance.setBlock(newPosition, Block.STONE);
        this.lastPosition = newPosition;


    }


}

