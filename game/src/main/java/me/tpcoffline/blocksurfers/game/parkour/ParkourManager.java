package me.tpcoffline.blocksurfers.game.parkour;

import me.tpcoffline.blocksurfers.game.GameState;
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

    private static final int maxY = 50;
    private static final int minY = 35;
    private static final int blockCooldownSeconds = 5;

    private Pos lastPosition = new Pos(0,41,0);
    private int blockCount;
    private final Random random = new Random();
    private final List<Pos> placedBlocks = new ArrayList<>();
    private GameState state = GameState.WAITING;



    public void generateNextBlock(Instance instance) {

        // tüm yönlerden rastgele al
        int nextX = random.nextInt(-3, 4);
        int nextY = random.nextInt(-1,2);
        int nextZ = random.nextInt(2, 4);

        // y koordinatını sınırla
        Pos newPosition = lastPosition.add(nextX, nextY, nextZ);
        if(((int)(newPosition.y())) > maxY ){newPosition = newPosition.withY(maxY);}
        else if(((int)(newPosition.y())) < minY ){newPosition = newPosition.withY(minY);}

        // yeni bloğu koy ve koordinatı kaydet
        blockCount++;
        instance.setBlock(newPosition, Block.STONE.withHandler(new ParkourBlockHandler(this,blockCount)));
        placedBlocks.add(newPosition);
        lastPosition = newPosition;
        final Pos blockPos = newPosition;

        // belli süre sonra bloğu ve listeden koordinatı siliyor
        if(state == GameState.RUNNING){scheduleBlockRemoval(instance, newPosition);}


    }

    public void startGame(Instance instance){
        state = GameState.RUNNING;
        for (Pos pos : new ArrayList<>(placedBlocks)) {
            scheduleBlockRemoval(instance, pos);
        }

    }

    private void scheduleBlockRemoval(Instance instance, Pos blockPos) {
        MinecraftServer.getSchedulerManager().buildTask(() -> {
                    instance.setBlock(blockPos, Block.AIR);
                    this.removeBlock(blockPos);
                })
                .delay(Duration.ofSeconds(blockCooldownSeconds))
                .schedule();
    }

    public int getBlockCount() {
        return this.blockCount;
    }

    public void reset(Instance instance, Player player) {
        // durumu waiting yap
        state = GameState.WAITING;

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

    public GameState getState() {
        return state;
    }






}

