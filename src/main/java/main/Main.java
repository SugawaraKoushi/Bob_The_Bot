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


        List<OptionData> actions = new ArrayList<>();
        actions.add(new OptionData(OptionType.STRING, "join-to", "ACTION: you in voicechat is required", false));
        actions.add(new OptionData(OptionType.STRING, "leave", "ACTION: you in voicechat is required", false));
        actions.add(new OptionData(OptionType.STRING, "play", "ACTION: URL is required", false));

        List<OptionData> conditions = new ArrayList<>();
        conditions.add(new OptionData(OptionType.STRING, "when-user-in-vc", "CONDITION: user name in next format: @{name} is required", false));
        conditions.add(new OptionData(OptionType.STRING, "when-time-is", "CONDITION: time in next format: {hh:mm} is required", false));

        guild.upsertCommand("do", "Executes {action} after {condition} is met")
                .addOptions(actions)
                .addOptions(conditions)
                .queue();

        guild.upsertCommand("clearqueue", "Clear the queue").queue();

        guild.upsertCommand("delete", "Delete track with given number")
                .addOption(OptionType.STRING, "position", "put number of position of track here", true).queue();

        guild.upsertCommand("now playing", "Return info of playing track").queue();

        guild.upsertCommand("pause", "Pause the track").queue();

        guild.upsertCommand("play", "Play track")
                .addOption(OptionType.STRING, "url", "play track").queue();

        guild.upsertCommand("playlist", "Add playlist")
                .addOption(OptionType.STRING, "add", "add playlist to playlist list", true).queue();

        guild.upsertCommand("play next", "Play track the next")
                .addOption(OptionType.STRING, "url", "play track", true).queue();

        guild.upsertCommand("queue", "Get the queue").queue();

        guild.upsertCommand("repeat", "Set repeat")
                .addOption(OptionType.STRING, "times", "put number of repeats here", false).queue();

        guild.upsertCommand("resume", "Resume the track").queue();

        guild.upsertCommand("shuffle", "Shuffle the queue").queue();

        guild.upsertCommand("skip", "Skip the track").queue();

        guild.upsertCommand("stop", "Stop playing").queue();

        guild.upsertCommand("volume", "Set volume")
                        .addOption(OptionType.STRING, "number", "put number between 0 and 100 here").queue();


        guild.upsertCommand("test", "test command").queue();

        guild.upsertCommand("join", "Join the voicechat").queue();

        guild.upsertCommand("leave", "Leave the voicechat").queue();

        guild.upsertCommand("restart", "Restart Bob").queue();

        guild.upsertCommand("shutdown", "Turn off Bob").queue();
    }
}
