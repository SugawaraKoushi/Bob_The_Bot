package main;

import commands.ICommand;
import commands.Output;
import commands.Restart;
import commands.Shutdown;
import commands.doWhen.Do;
import commands.music.*;
import commands.voicechat.Join;
import commands.voicechat.Leave;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class Listener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    private final Map<String, ICommand> commands = new HashMap<>();

    public Listener() {
        addCommand(new Do());

        addCommand(new ClearQueue());
        addCommand(new Delete());
        addCommand(new FavouriteSongs());
        addCommand(new NowPlaying());
        addCommand(new Pause());
        addCommand(new Play());
        addCommand(new PlayNext());
        addCommand(new Queue());
        addCommand(new Repeat());
        addCommand(new Resume());
        addCommand(new Shuffle());
        addCommand(new Skip());
        addCommand(new Stop());
        addCommand(new Volume());

        addCommand(new Join());
        addCommand(new Leave());

        addCommand(new Restart());
        addCommand(new Shutdown());
    }

    private void addCommand(ICommand cmd) {
        if (!commands.containsKey(cmd.getName()))
            commands.put(cmd.getName(), cmd);
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info("{} is ready \n", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getChannel().getId().equals(Config.get("CHANNEL_ID"))) {
            event.replyEmbeds(new Output("**SIKE, THAT IS THE WRONG CHAT**").getME()).queue();
            return;
        }

        commands.forEach((key, value) -> {
            if (commands.get(key).getName().equals(event.getName())) {
                try {
                    commands.get(key).handle(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
