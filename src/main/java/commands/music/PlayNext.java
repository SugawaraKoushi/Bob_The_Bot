package commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import commands.ICommand;
import commands.Output;
import music.GuildMusicManager;
import music.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class PlayNext implements ICommand {
    private String message;

    @Override
    public void handle(SlashCommandInteractionEvent event) throws InterruptedException {
        final PlayerManager playerManager = PlayerManager.getInstance();
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
        final GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();
        String content;
        List<AudioTrack> tracks = new ArrayList<>();
        event.deferReply().queue();

        if (!memberVoiceState.inAudioChannel()) {
            message = "**You are not in voice channel to do this**";
            event.replyEmbeds(getME()).queue();
            return;
        }

        try {
            content = event.getOption("url").getAsString();
        } catch (NullPointerException e) {
            message = e.getMessage();
            event.replyEmbeds(getME()).queue();
            return;
        }

        try {
            while (!queue.isEmpty()) {
                tracks.add(queue.take());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        playerManager.loadAndPlay(event.getTextChannel(), content);

        new Thread();
        Thread.sleep(1000);

        for (AudioTrack track : tracks) {
            musicManager.scheduler.queue(track);
        }
        message = "**Processing...**";
        event.replyEmbeds(getME()).queue();
    }

    @Override
    public MessageEmbed getME() {
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "playnext";
    }
}
