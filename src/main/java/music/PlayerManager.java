package music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;
import commands.Output;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.*;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private PlayerManager(){
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.playerManager);
        AudioSourceManagers.registerLocalSource(this.playerManager);
    }

    public synchronized GuildMusicManager getGuildMusicManager(Guild guild){
        long guildID = guild.getOwnerIdLong();
        GuildMusicManager musicManager = musicManagers.get(guildID);

        if(musicManager == null){
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildID, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public void loadAndPlay(TextChannel channel, String url){
        GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessageEmbeds(new Output(String.format("**Adding to queue:**\n%s - %s", track.getInfo().author, track.getInfo().title)).getME()).queue();
                play(musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for(AudioTrack track : playlist.getTracks()){
                    play(musicManager, track);
                }
                channel.sendMessageEmbeds(new Output(String.format("**Playlist** %s **was added**", playlist.getName())).getME()).queue();
            }

            @Override
            public void noMatches() {
                new Output(String.format("**Nothing found by:** %s", url));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessageEmbeds(new Output(String.format("**Could not play:** %s", exception.getMessage())).getME()).queue();
            }
        });
    }

    private void play(GuildMusicManager musicManager, AudioTrack track){
        musicManager.scheduler.queue(track);
    }

    public static synchronized PlayerManager getInstance(){
        if(INSTANCE == null)
            INSTANCE = new PlayerManager();

        return INSTANCE;
    }
}