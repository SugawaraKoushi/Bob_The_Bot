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

public class Repeat implements ICommand {
    private String message;

    @Override
    public void handle(SlashCommandInteractionEvent event) throws InterruptedException {
        final GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

        if(memberVoiceState != null) {
            if(!memberVoiceState.inAudioChannel()){
                message = "**You are not in voice channel to do this**";
                event.replyEmbeds(getME()).queue();
                return;
            }
        }

        String content;
        int count = 0;

        boolean repeat = !musicManager.scheduler.repeat;

        try{
            content = event.getOption("query").getAsString();
            count = Integer.parseInt(content);

            if(count > 0){
                final BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();
                List<AudioTrack> tracks = new ArrayList<>();

                while(!queue.isEmpty())
                    tracks.add(queue.take());

                for(int i = 0; i < count - 1; i++)
                    queue.add(musicManager.player.getPlayingTrack().makeClone());

                message = String.format("**Repeat:** %d **times**", count);
                queue.addAll(tracks);
                event.replyEmbeds(getME()).queue();
            }
        } catch(NullPointerException e){
            musicManager.scheduler.repeat = repeat;

            if(repeat) message = "**Repeat:** on";
            else message = "**Repeat:** off";

            event.replyEmbeds(getME()).queue();
        }
    }

    @Override
    public MessageEmbed getME() {
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "repeat";
    }
}
