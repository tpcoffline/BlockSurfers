package me.tpcoffline.blocksurfers.game.parkour;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParkourManager {


    private Pos lastPosition = new Pos(0,41,0);
    private int blockCount;
    private final Random random = new Random();
    private final List<Pos> placedBlocks = new ArrayList<>();


    public void generateNextBlock(Instance instance) {

        // tüm yönlerden rastgele al
        int nextX = random.nextInt(-3, 4);
        int nextY = random.nextInt(-1,2);
        int nextZ = random.nextInt(2, 4);

        // y koordinatını sınırla
        Pos newPosition = lastPosition.add(nextX, nextY, nextZ);
        if(((int)(newPosition.y())) > 50 ){newPosition = newPosition.withY(50);}
        else if(((int)(newPosition.y())) < 35 ){newPosition = newPosition.withY(32);}

        // yeni bloğu koy ve koordinatı kaydet
        blockCount++;
        instance.setBlock(newPosition, Block.STONE.withHandler(new ParkourBlockHandler(this,blockCount)));
        placedBlocks.add(newPosition);
        lastPosition = newPosition;
        final Pos blockPos = newPosition;

        // 10 saniye sonra bloğu ve listeden koordinatı siliyor
        final var scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(() -> {
            instance.setBlock(blockPos, Block.AIR);
            this.removeBlock(blockPos);
        }).delay(Duration.ofSeconds(10)).schedule();

    }

    public int getBlockCount() {
        return this.blockCount;
    }

    public void reset(Instance instance, Player player) {
        // listedeki koordinatlardaki blokları sil sonra listeyi temizle
        for (Pos pos : new ArrayList<>(placedBlocks)) {
            instance.setBlock(pos, Block.AIR);
        }
        placedBlocks.clear();

        // Ayarları sıfırla
        this.lastPosition = new Pos(0, 41, 0);
        this.blockCount = 0;

        // elmas bloğu handlerla koy
        instance.setBlock(0, 41, 0, Block.DIAMOND_BLOCK.withHandler(new ParkourBlockHandler(this,0)));

        // oyuncuyu başa döndür
        player.teleport(new Pos(0.5,42,0.5));

    }

    public void removeBlock(Pos pos) {
        placedBlocks.remove(pos);
    }






}

