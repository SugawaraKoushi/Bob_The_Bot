package commands.music;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import commands.ICommand;
import commands.Output;
import music.GuildMusicManager;
import music.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Queue implements ICommand {
    private String message;

    @Override
    public void handle(SlashCommandInteractionEvent event) throws InterruptedException {
        final GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();
        StringBuilder builder = new StringBuilder();

        int count = 1;
        int size = queue.size();

        if(memberVoiceState != null) {
            if(!memberVoiceState.inAudioChannel()){
                message = "**You are not in voice channel to do this**";
                event.replyEmbeds(getME()).queue();
                return;
            }
        }

        if (queue.isEmpty()) {
            message = "**Queue is empty**";
            event.replyEmbeds(getME()).queue();
            return;
        }

        if (size > 19)
            message = String.format("**List of %d / %d**\n", 20, size);
        else
            message = String.format("**List of %d / %d**\n", size, size);

        AudioTrack npTrack = musicManager.player.getPlayingTrack();
        builder.append(String.format(":arrow_forward: %s\n", npTrack.getInfo().title));

        for (AudioTrack track : queue) {
            if (queue.size() > 19)
                if (count > 19)
                    break;

            builder.append("**").append(count).append("**").append(".\t")
                    .append(String.format("%s", track.getInfo().title))
                    .append(formatTime(track.getDuration()))
                    .append("\n");
            count++;
        }

        message = builder.toString();
        event.replyEmbeds(getME()).queue();
    }

    @Override
    public MessageEmbed getME() {
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "queue";
    }

    private String formatTime(long timeInMillis){
        final long hours = TimeUnit.MILLISECONDS.toHours(timeInMillis) % 24;
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60;
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
