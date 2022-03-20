package commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import commands.ICommand;
import commands.Output;
import music.GuildMusicManager;
import music.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.concurrent.TimeUnit;

public class NowPlaying implements ICommand {
    private String message;

    private String formatTime(long timeInMillis){
        final long hours = TimeUnit.MILLISECONDS.toHours(timeInMillis) % 24;
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60;
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) throws InterruptedException {
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());

        if(!memberVoiceState.inAudioChannel()){
            message = "**You are not in voice channel to do this**";
            event.replyEmbeds(getME()).queue();
            return;
        }

        if(musicManager.player.getPlayingTrack() == null){
            message = "**Bob is not playing anything**";
            event.replyEmbeds(getME()).queue();
            return;
        }

        AudioTrackInfo info = musicManager.player.getPlayingTrack().getInfo();
        message = String.format("**Bob is playing:** %s - %s\n%s - %s",
                info.author,
                info.title,
                formatTime(musicManager.player.getPlayingTrack().getPosition()),
                formatTime(musicManager.player.getPlayingTrack().getDuration()));
        event.replyEmbeds(getME()).queue();
    }

    @Override
    public MessageEmbed getME() {
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "nowplaying";
    }
}
