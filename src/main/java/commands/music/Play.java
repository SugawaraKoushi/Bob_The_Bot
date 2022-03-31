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
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.io.File;
import java.io.FileInputStream;
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
        String[] songs;
        String content;

        if (!memberVoiceState.inAudioChannel()) {
            message = "**You are not in voice channel to do this**";
            event.replyEmbeds(getME()).queue();
            return;
        }

        try {
            for (OptionMapping option : event.getOptions()) {
                switch (option.getName()) {
                    //Воспроизводит рандомный трек из папки
                    case "random-song":
                        songs = new File(Config.get("MUSIC_FOLDER")).list();

                        if (!event.getOption("random-song").getAsString().equals("yes")) {
                            message = "**Something goes wrong**";
                            event.replyEmbeds(getME()).queue();
                        }

                        if (songs == null) {
                            message = "**Nothing to play**";
                            event.replyEmbeds(getME()).queue();
                            return;
                        }

                        message = "**URL:** ".concat(Config.get("MUSIC_FOLDER"));
                        event.replyEmbeds(getME()).queue();
                        playerManager.loadAndPlay(event.getTextChannel(), Config.get("MUSIC_FOLDER") + "\\" + songs[new Random().nextInt(songs.length - 1)]);
                        break;

                    // Воспроизводит все треки из папки
                    case "all-songs":
                        songs = new File(Config.get("MUSIC_FOLDER")).list();

                        if (!event.getOption("all-songs").getAsString().equals("yes")) {
                            message = "**Something goes wrong**";
                            event.replyEmbeds(getME()).queue();
                        }

                        if (songs == null) {
                            message = "**Nothing to play**";
                            event.replyEmbeds(getME()).queue();
                            return;
                        }

                        message = "**URL:** ".concat(Config.get("MUSIC_FOLDER"));
                        event.replyEmbeds(getME()).queue();

                        for (String song : songs)
                            playerManager.loadAndPlay(event.getTextChannel(), Config.get("MUSIC_FOLDER") + "\\" + song);

                        break;

                    // Воспроизводит треки из файла с любимыми треками
                    case "fav-songs":
                        File favSongs = new File(Config.get("FAVOURITES_SONGS"));
                        FileInputStream is = new FileInputStream(favSongs);
                        String buf = new String(is.readAllBytes()).trim();
                        songs = buf.split("\n");

                        message = "**URL: favourite songs**";
                        event.replyEmbeds(getME()).queue();

                        for (String song : songs) {
                            playerManager.loadAndPlay(event.getTextChannel(), song);
                        }

                        break;

                    // Воспроизводит трек по ссылке
                    case "url":
                        content = event.getOption("url").getAsString();
                        message = "**URL:** ".concat(content);
                        event.replyEmbeds(getME()).queue();
                        playerManager.loadAndPlay(event.getTextChannel(), content);
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            while (!queue.isEmpty())
                tracks.add(queue.take());

            if (!tracks.isEmpty()) {
                if (tracks.get(tracks.size() - 1).getIdentifier().equals(Config.get("END_OF_QUEUE_TRACK"))) {
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
                playerManager.loadAndPlay(event.getTextChannel(), Config.get("END_OF_QUEUE_TRACK"));
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