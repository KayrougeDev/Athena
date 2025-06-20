package fr.kayrouge.athena.paper;

import fr.kayrouge.athena.common.CAthena;
import fr.kayrouge.athena.common.command.CAthenaCommand;
import fr.kayrouge.athena.common.util.CPlatform;
import fr.kayrouge.athena.paper.command.PaperCommands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

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
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(PaperCommands.contructFurnacesCommand());
        });
    }

    @Override
    public CPlatform getPlatform() {
        return CPlatform.PAPER;
    }
}
