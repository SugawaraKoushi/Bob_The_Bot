package commands;

import main.Config;
import music.GuildMusicManager;
import music.PlayerManager;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class Restart implements ICommand{
    private String message = "**Restarting...**";

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        musicManager.scheduler.getQueue().clear();
        musicManager.player.stopTrack();
        playerManager.loadAndPlay(event.getTextChannel(), Config.get("SHUTDOWN_TRACK"));
        event.getGuild().getAudioManager().closeAudioConnection();

        new Thread();
        Thread.sleep(500);
        event.replyEmbeds(getME()).queue();
        event.getJDA().shutdown();

        new Thread();
        Thread.sleep(2000);

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            System.out.println("Restarting...");
            Runtime.getRuntime().exec("cmd /c start cmd.exe /C\"" + Config.get("EXE_PATH") + "\"");
            System.exit(0);
        } else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            System.out.println("Restarting...");
            System.exit(0);
            Runtime.getRuntime().exec("java -jar \"" + Config.get("EXE_PATH") + "\"");
        }
    }

    @Override
    public MessageEmbed getME() {
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "restart";
    }
}
