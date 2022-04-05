package commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import commands.ICommand;
import commands.Output;
import music.GuildMusicManager;
import music.PlayerManager;
import music.TrackScheduler;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Shuffle implements ICommand {
    private String message;

    @Override
    public void handle(SlashCommandInteractionEvent event) throws InterruptedException {
        final PlayerManager manager = PlayerManager.getInstance();
        final GuildMusicManager musicManager = manager.getGuildMusicManager(event.getGuild());
        final TrackScheduler scheduler = musicManager.scheduler;
        final BlockingQueue<AudioTrack> queue = scheduler.getQueue();
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

        List<AudioTrack> tracks = new ArrayList<>();
        AudioTrack end;

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

        try {
            while(!queue.isEmpty())
                tracks.add(queue.take());
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        end = tracks.get(tracks.size() - 1).makeClone();
        tracks.remove(tracks.size() - 1);
        Collections.shuffle(tracks);
        tracks.add(end);

        tracks.forEach(scheduler::queue);

        message = "**Queue have been shuffled**";
        event.replyEmbeds(getME()).queue();
    }

    @Override
    public MessageEmbed getME() {
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "shuffle";
    }
}
