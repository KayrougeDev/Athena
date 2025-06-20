package fr.kayrouge.athena.paper.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.kayrouge.athena.common.artifact.Artifacts;
import fr.kayrouge.athena.common.command.CArtifactCommand;
import fr.kayrouge.athena.common.command.CFurnacesCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public class PaperCommands {

    public static LiteralCommandNode<CommandSourceStack> constructFurnacesCommand() {
        CFurnacesCommand common = new CFurnacesCommand();
        return Commands.literal("furnaces")
                .requires(ctx -> ctx.getExecutor() instanceof Player)
                .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            common.getFurnacesList().forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            common.execute(context.getSource().getExecutor(), new String[]{context.getArgument("type", String.class)});
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }

    public static LiteralCommandNode<CommandSourceStack> constructArtifactsCommand() {
        return Commands.literal("artifacts")
                .requires(ctx -> ctx.getExecutor() instanceof Player)
                .then(Commands.argument("name", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            Artifacts.getArtifacts().keySet().forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            new CArtifactCommand().giveArtifact((Player)context.getSource().getExecutor(), context.getArgument("name", String.class));
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }

    public static void test(CommandSourceStack stack, String[] args) {
        Entity e = stack.getExecutor();
        BlockDisplay block = (BlockDisplay) e.getWorld().spawnEntity(e.getLocation(), EntityType.BLOCK_DISPLAY);
        block.setBlock(Material.AMETHYST_BLOCK.createBlockData());
        block.addPassenger(e);
        Transformation old = block.getTransformation();
        block.setTransformation(new Transformation(old.getTranslation(), old.getRightRotation(), new Vector3f(1f, 5f, 2f), old.getLeftRotation()));
    }
}
