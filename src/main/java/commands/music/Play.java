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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Play implements ICommand {
    private String message = "";

    private static String findTrack(String track) {
        try {
            track = track.replace(" ", "+");
            Document document = Jsoup.connect("https://www.google.com/search?q=" + track + "&biw=1920&bih=979&tbm=vid").get();
            Elements elements = document.select("a");

            for (Element element : elements) {
                String url = element.attr("abs:href");
                if (url.contains("https://www.youtube.com/watch?v=")) {
                    return url;
                }
            }

            return "";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) throws InterruptedException {
        final PlayerManager playerManager = PlayerManager.getInstance();
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
        final GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();
        List<AudioTrack> tracks = new ArrayList<>();
        String[] songs;
        String content;
        boolean trackWasFound = true;

        if (!memberVoiceState.inAudioChannel()) {
            message = "**You are not in voice channel to do this**";
            event.deferReply().queue();
            event.getHook().sendMessageEmbeds(getME()).queue();
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
                            event.deferReply().queue();
                            event.getHook().sendMessageEmbeds(getME()).queue();
                        }

                        if (songs == null) {
                            message = "**Nothing to play**";
                            event.deferReply().queue();
                            event.getHook().sendMessageEmbeds(getME()).queue();
                            return;
                        }

                        message = "**URL:** ".concat(Config.get("MUSIC_FOLDER"));
                        event.deferReply().queue();
                        event.getHook().sendMessageEmbeds(getME()).queue();
                        playerManager.loadAndPlay(event.getChannel().asTextChannel(), Config.get("MUSIC_FOLDER") + "\\" + songs[new Random().nextInt(songs.length - 1)]);
                        break;

                    // Воспроизводит все треки из папки
                    case "all-songs":
                        songs = new File(Config.get("MUSIC_FOLDER")).list();

                        if (!event.getOption("all-songs").getAsString().equals("yes")) {
                            message = "**Something goes wrong**";
                            event.deferReply().queue();
                            event.getHook().sendMessageEmbeds(getME()).queue();
                        }

                        if (songs == null) {
                            message = "**Nothing to play**";
                            event.deferReply().queue();
                            event.getHook().sendMessageEmbeds(getME()).queue();
                            return;
                        }

                        message = "**URL:** ".concat(Config.get("MUSIC_FOLDER"));
                        event.deferReply().queue();
                        event.getHook().sendMessageEmbeds(getME()).queue();

                        for (String song : songs)
                            playerManager.loadAndPlay(event.getChannel().asTextChannel(), Config.get("MUSIC_FOLDER") + "\\" + song);

                        break;

                    // Воспроизводит треки из файла с любимыми треками
                    case "fav-songs":
                        File favSongs = new File(Config.get("FAVOURITES_SONGS"));
                        FileInputStream is = new FileInputStream(favSongs);
                        String buf = new String(is.readAllBytes()).trim();
                        songs = buf.split("\n");

                        message = "**URL: favourite songs**";
                        event.deferReply().queue();
                        event.getHook().sendMessageEmbeds(getME()).queue();

                        Arrays.asList(songs).forEach(System.out::println);

                        for (String song : songs) {
                            playerManager.loadAndPlay(event.getChannel().asTextChannel(), song);
                        }

                        is.close();
                        break;

                    // Находит трек на ютубе
                    case "youtube":
                        content = event.getOption("youtube").getAsString();
                        long start = System.currentTimeMillis();
                        content = findTrack(content);

                        if (content.isEmpty()) {
                            message = "**Track was not found**";
                            event.deferReply().queue();
                            event.getHook().sendMessageEmbeds(getME()).queue();
                            trackWasFound = false;
                            break;
                        }

                        message = content;
                        event.deferReply().queue();
                        event.getHook().sendMessage(message).queue();
                        playerManager.loadAndPlay(event.getChannel().asTextChannel(), content);
                        break;

                    // Воспроизводит трек по ссылке
                    case "url":
                        content = event.getOption("url").getAsString();
                        message = content;
                        event.deferReply().queue();
                        event.getHook().sendMessage(message).queue();
                        playerManager.loadAndPlay(event.getChannel().asTextChannel(), content);
                        break;
                }
            }

            if (!Config.get("USING_SOUNDS").equals("TRUE") || !trackWasFound) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            TimeUnit.SECONDS.sleep(1);

            while (!queue.isEmpty())
                tracks.add(queue.take());

            List<String> identifiers = new ArrayList<>();
            tracks.forEach(track -> identifiers.add(track.getIdentifier()));

            if (identifiers.contains(Config.get("END_OF_QUEUE_TRACK"))) {
                tracks.remove(identifiers.indexOf(Config.get("END_OF_QUEUE_TRACK")));
            }

            tracks.forEach(musicManager.scheduler::queue);
            playerManager.loadAndPlay(event.getChannel().asTextChannel(), Config.get("END_OF_QUEUE_TRACK"));
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