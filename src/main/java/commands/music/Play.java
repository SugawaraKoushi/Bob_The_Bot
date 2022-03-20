package commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import commands.ICommand;
import commands.Output;
import main.Config;
import music.GuildMusicManager;
import music.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.File;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class Play implements ICommand {
    private String message = "";

    @Override
    public void handle(SlashCommandInteractionEvent event) throws InterruptedException {
        final PlayerManager playerManager = PlayerManager.getInstance();
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
        final GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();
        List<AudioTrack> tracks = new ArrayList<>();
        AudioTrack end = null;
        String[] songs = new File(Config.get("MUSIC_FOLDER")).list();
        String content;

        if (!memberVoiceState.inAudioChannel()) {
            message = "**You are not in voice channel to do this**";
            event.replyEmbeds(getME()).queue();
            return;
        }

        try {
            content = event.getOption("url").getAsString();

            /* Воспроизведение всех треков из папки с музыкой по умолчанию */
            if (content.equals("music")) {
                if (songs == null) {
                    message = "**Nothing to play**";
                    event.replyEmbeds(getME()).queue();
                    return;
                }

                message = "**URL:** ".concat(Config.get("MUSIC_FOLDER"));
                event.replyEmbeds(getME()).queue();

                for (String song : songs)
                    playerManager.loadAndPlay(event.getTextChannel(), Config.get("MUSIC_FOLDER") + "\\" + song);

                /* Воспроизведение трека по ссылке */
            } else {
                message = "**URL:** ".concat(content);
                event.replyEmbeds(getME()).queue();
                playerManager.loadAndPlay(event.getTextChannel(), content);
            }

        } catch (Exception e) {
            /* Воспроизведение рандомного трека из стандартной директории */
            if (songs == null) {
                message = "**Nothing to play**";
                event.replyEmbeds(getME()).queue();
                return;
            }

            message = "**URL:** ".concat(Config.get("MUSIC_FOLDER"));
            event.replyEmbeds(getME()).queue();
            playerManager.loadAndPlay(event.getTextChannel(), Config.get("MUSIC_FOLDER") + "\\" + songs[new Random().nextInt(songs.length - 1)]);

            ///////////////////////////////////////////////////////////////////////////////////////////////////////////

            while (!queue.isEmpty())
                tracks.add(queue.take());

            if (!tracks.isEmpty()) {
                if (tracks.get(tracks.size() - 1).getIdentifier().equals(Config.get("SOUNDS_FOLDER") + "bob_dynamite.ogg")) {
                    end = tracks.get(tracks.size() - 1);
                    for (int i = 0; i < tracks.size() - 1; i++)
                        musicManager.scheduler.queue(tracks.get(i));
                }
            }

            if (end != null) {
                new Thread();
                Thread.sleep(100);
                musicManager.scheduler.queue(end);
            } else {
                playerManager.loadAndPlay(event.getTextChannel(), Config.get("SOUNDS_FOLDER") + "bob_dynamite.ogg");
            }
        }
    }

    @Override
    public MessageEmbed getME() {
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "play";
    }
}