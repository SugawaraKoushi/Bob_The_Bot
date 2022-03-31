package commands;

import main.Config;
import music.GuildMusicManager;
import music.PlayerManager;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Shutdown implements ICommand {
    String message = "**Shutting down**";

    @Override
    public void handle(SlashCommandInteractionEvent event) throws InterruptedException {
        final PlayerManager playerManager = PlayerManager.getInstance();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());
        event.deferReply().queue();

        musicManager.scheduler.getQueue().clear();
        musicManager.player.stopTrack();
        playerManager.loadAndPlay(event.getTextChannel(), Config.get("SHUTDOWN_TRACK"));

        event.getHook().sendMessageEmbeds(getME()).queue();

        event.getGuild().getAudioManager().closeAudioConnection();
        new Thread();
        Thread.sleep(1000);

        event.getJDA().shutdown();
        System.exit(0);
    }

    @Override
    public MessageEmbed getME(){
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "shutdown";
    }
}
