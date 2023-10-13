package commands.music;

import commands.ICommand;
import commands.Output;
import main.Config;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class FavouriteSongs implements ICommand {
    private String message;
    private File playlist = new File(Config.get("FAVOURITES_SONGS"));
    private static final Pattern PLAYLIST_PATTERN = Pattern.compile("https://[w]{0,3}\\.?youtube\\.com/playlist\\?list=.+");
    private static final Pattern YOUTUBE_PATTERN_1 = Pattern.compile("https://youtu\\.be/.+");
    private static final Pattern YOUTUBE_PATTERN_2 = Pattern.compile("https://[w]{0,3}\\.?youtube\\.com/watch\\?v=.+");


    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        FileOutputStream os = new FileOutputStream(playlist, true);
        String url = event.getOption("add").getAsString();

        if (!isUTubeURL(url)) {
            message = String.format("**URL: %s is not YouTube link**", url);
            event.deferReply().queue();
            event.getHook().sendMessageEmbeds(getME()).queue();
            return;
        }

        switch (isAlreadyAdded(url)) {
            case -1:
                message = "File has no found";
                event.deferReply().queue();
                event.getHook().sendMessageEmbeds(getME()).queue();
                break;

            case 0:
                byte[] bytes = "\n".concat(url).getBytes(StandardCharsets.UTF_8);
                os.write(bytes);
                message = String.format("**URL: %s has been added**", url);
                event.deferReply().queue();
                event.getHook().sendMessageEmbeds(getME()).queue();
                break;

            case 1:
                message = String.format("**URL: %s is already added**", url);
                event.deferReply().queue();
                event.getHook().sendMessageEmbeds(getME()).queue();
                break;
        }
    }

    @Override
    public MessageEmbed getME() {
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "fav-playlist";
    }

    private boolean isUTubeURL(String url) {
        return url.matches(PLAYLIST_PATTERN.pattern()) || url.matches(YOUTUBE_PATTERN_1.pattern()) || url.matches(YOUTUBE_PATTERN_2.pattern());
    }

    private int isAlreadyAdded(String url) {
        try {
            FileInputStream is = new FileInputStream(playlist);
            byte[] bytes = is.readAllBytes();
            String s = new String(bytes);

            System.out.println(s);

            return s.contains(url) ? 1 : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}