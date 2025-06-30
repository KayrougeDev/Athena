package fr.kayrouge.athena.paper.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.kayrouge.athena.common.artifact.Artifacts;
import fr.kayrouge.athena.common.command.CArtifactCommand;
import fr.kayrouge.athena.common.command.CFurnacesCommand;
import fr.kayrouge.athena.common.command.CHomeCommand;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.function.Consumer;
import java.util.function.Supplier;

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

    public static LiteralCommandNode<CommandSourceStack> constructHomeCommand() {
        CHomeCommand common = new CHomeCommand();
        return Commands.literal("home")
                .requires(ctx -> ctx.getExecutor() instanceof Player)
                .then(Commands.argument("name", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            common.getHomeList((Player) context.getSource().getExecutor()).forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            common.teleport((Player) context.getSource().getExecutor(), context.getArgument("name", String.class));
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }

    public static LiteralCommandNode<CommandSourceStack> constructHomesCommand() {
        CHomeCommand common = new CHomeCommand();
        return Commands.literal("homes")
                .requires(ctx -> ctx.getExecutor() instanceof Player)
                .then(Commands.literal("set")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    common.setHome((Player) ctx.getSource().getExecutor(), ctx.getArgument("name", String.class));
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("del")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    common.getHomeList((Player) context.getSource().getExecutor()).forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    common.delHome((Player) ctx.getSource().getExecutor(), ctx.getArgument("name", String.class));
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("info")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    common.getHomeList((Player) context.getSource().getExecutor()).forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    common.info((Player) ctx.getSource().getExecutor(), ctx.getArgument("name", String.class));
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
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

    public static BasicCommand checkIsPlayerAndExecute(Consumer<Player> command) {
        return checkIsPlayerAndExecute(command, false);
    }

    public static BasicCommand checkIsPlayerAndExecute(Consumer<Player> command, boolean message) {
        return (commandSourceStack, args) -> {
            if(commandSourceStack.getExecutor() instanceof Player player) {
                command.accept(player);
            } else if (message) {
                commandSourceStack.getExecutor().sendMessage(Component.text("Seul les joueurs peuvent faire ca !"));
            }
        };
    }
}
