package main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static JDA jda;
    private static Guild guild;

    public static JDA getJDA() {
        return jda;
    }

    public static Guild getGuild(){
        return guild;
    }

    public static void main(String[] args) throws Exception {
        jda = JDABuilder
                .createDefault(Config.get("TOKEN"))
                .addEventListeners(new Listener())
                .build();
        jda.awaitReady();

        guild = jda.getGuildById(Config.get("SERVER_ID"));
        if(guild == null){
            System.out.println("guild is null");
            return;
        }
/*
        List<OptionData> actions = new ArrayList<>();
        actions.add(new OptionData(
                OptionType.STRING,
                "join-to",
                "ACTION: you in voicechat is required",
                false)
        );
        actions.add(new OptionData(OptionType.STRING,
                "leave",
                "ACTION: you in voicechat is required",
                false)
        );
        actions.add(new OptionData(OptionType.STRING,
                "play",
                "ACTION: URL is required",
                false)
        );

        List<OptionData> conditions = new ArrayList<>();
        conditions.add(new OptionData(
                OptionType.STRING,
                "when-user-in-vc",
                "CONDITION: user name in next format: @{name} is required",
                false)
        );
        conditions.add(new OptionData(OptionType.STRING,
                "when-time-is",
                "CONDITION: time in next format: {hh:mm} is required",
                false)
        );

        guild.upsertCommand("do", "Executes {action} after {condition} is met")
                .addOptions(actions)
                .addOptions(conditions)
                .queue();
         */
        jda.upsertCommand("clear-queue", "Clear the queue").queue();

        jda.upsertCommand("delete", "Delete track with given number")
                .addOption(
                        OptionType.STRING,
                        "position",
                        "put number of position of track here",
                        true)
                .queue();

        jda.upsertCommand("fav-playlist", "Favourite playlist")
                .addOption(
                        OptionType.STRING,
                        "add",
                        "add track or playlist into favourites",
                        true)
                .queue();

        jda.upsertCommand("now-playing", "Return info of playing track").queue();

        jda.upsertCommand("pause", "Pause the track").queue();

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "url", "play track"));
        options.add(new OptionData(OptionType.STRING, "youtube", "find and play track from youtube"));
        options.add(new OptionData(OptionType.STRING, "random-song",
                "play random song from default directory").addChoice("yes", "yes"));
        options.add(new OptionData(OptionType.STRING, "all-songs",
                "play all songs from default directory").addChoice("yes", "yes"));
        options.add(new OptionData(OptionType.STRING, "fav-songs",
                "play all favourite songs").addChoice("yes", "yes"));

        jda.upsertCommand("play", "Play track")
                .addOptions(options)
                .queue();

        jda.upsertCommand("play-next", "Play track the next")
                .addOption(OptionType.STRING, "url", "play track", true).queue();

        jda.upsertCommand("queue", "Get the queue").queue();

        jda.upsertCommand("repeat", "Set repeat")
                .addOption(OptionType.STRING, "times", "put number of repeats here", false)
                .queue();

        jda.upsertCommand("resume", "Resume the track").queue();

        jda.upsertCommand("shuffle", "Shuffle the queue").queue();

        jda.upsertCommand("skip", "Skip the track").queue();

        jda.upsertCommand("stop", "Stop playing").queue();

        jda.upsertCommand("volume", "Set volume")
                .addOption(OptionType.STRING, "level", "put number between 0 and 100 here")
                .queue();

        jda.upsertCommand("join", "Join the voicechat").queue();

        jda.upsertCommand("leave", "Leave the voicechat").queue();

        jda.upsertCommand("restart", "Restart Bob").queue();

        jda.upsertCommand("shutdown", "Turn off Bob").queue();
    }
}
