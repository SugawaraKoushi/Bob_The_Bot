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
import java.util.concurrent.TimeUnit;

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

        if(memberVoiceState != null) {
            if(!memberVoiceState.inAudioChannel()){
                message = "**You are not in voice channel to do this**";
                event.deferReply().queue();
                event.getHook().sendMessageEmbeds(getME()).queue();
                return;
            }
        }

        try {
            content = event.getOption("url").getAsString();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return;
        }

        try {
            while (!queue.isEmpty()) {
                tracks.add(queue.take());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        playerManager.loadAndPlay(event.getChannel().asTextChannel(), content);

        TimeUnit.SECONDS.sleep(1);

        for (AudioTrack track : tracks) {
            musicManager.scheduler.queue(track);
        }
        message = content;
        event.deferReply().queue();
        event.getHook().sendMessage(message).queue();
    }

    @Override
    public MessageEmbed getME() {
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "play-next";
    }
}
