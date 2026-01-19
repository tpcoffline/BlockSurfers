package me.tpcoffline.blocksurfers.game.commands;

import me.tpcoffline.blocksurfers.game.parkour.ParkourGenerator;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

public class TestCommand extends Command {

    private final ParkourGenerator generator;

    public TestCommand(ParkourGenerator generator) {
        super("test");

        // KRİTİK DÜZELTME: Bunu yazmazsan hata verir
        this.generator = generator;

        var mm = MiniMessage.miniMessage();

        setDefaultExecutor((sender, context) -> sender.sendMessage(mm.deserialize("<red>Yanlış Kullanım!")));

        var generateNextBlock = ArgumentType.Literal("generete_next_block");
        var backSpawn = ArgumentType.Literal("back_spawn");

        addSyntax((sender, context) -> {
            if (sender instanceof Player player) {
                this.generator.generateNextBlock(player.getInstance());
            }
        }, generateNextBlock);

        addSyntax((sender, context) -> {
            if(sender instanceof Player player){player.teleport(new Pos(0,42,0));}
        },backSpawn );

    }
}