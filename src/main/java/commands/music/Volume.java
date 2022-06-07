package commands.music;

import commands.ICommand;
import commands.Output;
import music.GuildMusicManager;
import music.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Volume implements ICommand {
    private String message;

    @Override
    public void handle(SlashCommandInteractionEvent event) throws InterruptedException {
        final PlayerManager playerManager = PlayerManager.getInstance();
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
        final GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        String content = "";
        int volume;

        if(memberVoiceState != null) {
            if(!memberVoiceState.inAudioChannel()){
                message = "**You are not in voice channel to do this**";
                event.replyEmbeds(getME()).queue();
                return;
            }
        }

        try {
            content = event.getOption("level").getAsString();
            volume = Integer.parseInt(content);
        } catch (Exception e) {
            if (content.isEmpty()) {
                message = String.format("**Volume:** %d", musicManager.player.getVolume());
            } else {
                message = "**Error**: given query is not integer";
            }
            event.replyEmbeds(getME()).queue();
            e.printStackTrace();
            return;
        }

        if (volume > 100)
            volume = 100;

        if (volume < 0)
            volume = 0;

        musicManager.player.setVolume(volume);
        message = String.format("**Volume:** %d", volume);
        event.replyEmbeds(getME()).queue();
    }

    @Override
    public MessageEmbed getME() {
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "volume";
    }
}
