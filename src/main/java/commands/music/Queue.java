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

public class Queue implements ICommand {
    private String message;

    @Override
    public void handle(SlashCommandInteractionEvent event) throws InterruptedException {
        final GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();

        int count = 0;
        int size = queue.size();

        if (!memberVoiceState.inAudioChannel()) {
            message = "**You are not in voice channel to do this**";
            event.replyEmbeds(getME()).queue();
            return;
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

        AudioTrack np_track = musicManager.player.getPlayingTrack();
        message += String.format(":arrow_forward: %s - %s\n", np_track.getInfo().author, np_track.getInfo().title);

        for (AudioTrack track : queue) {
            if (queue.size() > 19)
                if (count > 19)
                    break;

            message += "**" + (count + 1) + "**" + ". " + String.format("%s - %s\n", track.getInfo().author, track.getInfo().title);
            count++;
        }

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
}
