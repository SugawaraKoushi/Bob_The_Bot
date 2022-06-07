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

public class Delete implements ICommand {
    String message;

    @Override
    public void handle(SlashCommandInteractionEvent event) throws InterruptedException {
        final PlayerManager playerManager = PlayerManager.getInstance();
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
        final GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        if(memberVoiceState != null) {
            if(!memberVoiceState.inAudioChannel()){
                message = "**You are not in voice channel to do this**";
                event.replyEmbeds(getME()).queue();
                return;
            }
        }

        try{
            String content = event.getOption("position").getAsString();
            final BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();
            List<AudioTrack> tracks = new ArrayList<>();

            try{
                while(!queue.isEmpty()){
                    tracks.add(queue.take());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tracks.remove(Integer.parseInt(content) - 1);

            for(AudioTrack track : tracks)
                queue.put(track);

            message = "**Bob has deleted the track**";
            event.replyEmbeds(getME()).queue();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public MessageEmbed getME() {
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "delete";
    }
}
