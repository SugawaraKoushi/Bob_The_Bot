package commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import commands.ICommand;
import commands.Output;
import main.Main;
import music.GuildMusicManager;
import music.PlayerManager;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Resume implements ICommand {
    private String message;

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        final PlayerManager playerManager = PlayerManager.getInstance();
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
        final GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        if(memberVoiceState != null) {
            if(!memberVoiceState.inAudioChannel()){
                message = "**You are not in voice channel to do this**";
                event.deferReply().queue();
                event.getHook().sendMessageEmbeds(getME()).queue();
                return;
            }
        }

        if(musicManager.player.getPlayingTrack() == null){
            message = "**Bob is not playing anything**";
            event.deferReply().queue();
            event.getHook().sendMessageEmbeds(getME()).queue();
            return;
        }

        musicManager.player.setPaused(false);

        message = "**Pause:** off";
        event.replyEmbeds(getME()).queue();
    }

    @Override
    public MessageEmbed getME() {
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "resume";
    }
}
