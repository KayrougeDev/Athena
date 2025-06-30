package fr.kayrouge.athena.paper;

import fr.kayrouge.athena.common.CAthena;
import fr.kayrouge.athena.common.command.CAthenaCommand;
import fr.kayrouge.athena.common.command.CDevWorldCommand;
import fr.kayrouge.athena.common.util.CPlatform;
import fr.kayrouge.athena.paper.command.PaperCommands;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import java.awt.print.Paper;
import java.util.Collections;

public class PaperAthena extends CAthena {

    public static PaperAthena INSTANCE;

    public PaperAthena() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        registerCommands();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    protected void registerEvents() {
        super.registerEvents();
    }

    private void registerCommands() {
        registerCommand("athena", (stack, strings) -> new CAthenaCommand().sendMessage(stack.getSender()));
        registerCommand("papertest", PaperCommands::test);

        registerCommand("devworld",PaperCommands.checkIsPlayerAndExecute(new CDevWorldCommand()::join));

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(PaperCommands.constructFurnacesCommand());
            commands.registrar().register(PaperCommands.constructArtifactsCommand());
            commands.registrar().register(PaperCommands.constructHomeCommand());
            commands.registrar().register(PaperCommands.constructHomesCommand());
        });
    }



    @Override
    public CPlatform getPlatform() {
        return CPlatform.PAPER;
    }
}
