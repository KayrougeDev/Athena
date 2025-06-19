package fr.kayrouge.athena.paper.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.kayrouge.athena.common.command.CFurnacesCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class PaperCommands {

    public static LiteralCommandNode<CommandSourceStack> contructFurnacesCommand() {
        CFurnacesCommand common = new CFurnacesCommand();
        return Commands.literal("furnaces")
                .requires(ctx -> ctx.getExecutor() instanceof Player)
                .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            common.getFurnacesList().forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            common.execute(context.getSource().getSender(), new String[]{context.getArgument("type", String.class)});
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }

}
