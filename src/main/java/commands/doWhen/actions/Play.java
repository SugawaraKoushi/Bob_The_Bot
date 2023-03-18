package commands.doWhen.actions;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import commands.doWhen.conditions.Condition;
import music.GuildMusicManager;
import music.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Play implements Action {
    private String url;
    private Condition condition;

    @Override
    public void setQuery(String query) {
        url = query;
    }

    @Override
    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        final PlayerManager playerManager = PlayerManager.getInstance();
        final GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
        boolean isNull;
        long positionOfPlayingTrack = 0;

        BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();
        List<AudioTrack> tracks = new ArrayList<>();

        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (condition.getTruth())
                break;
        }



        try {
            while (!queue.isEmpty()) {
                tracks.add(queue.take());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        AudioTrack playingTrack = musicManager.player.getPlayingTrack();
        isNull = playingTrack == null;

        if (!isNull) {
            musicManager.player.setPaused(true);
            positionOfPlayingTrack = musicManager.player.getPlayingTrack().getPosition();
            musicManager.player.stopTrack();
        }

        playerManager.loadAndPlay(event.getChannel().asTextChannel(), url);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!isNull) queue.add(playingTrack.makeClone());

        musicManager.player.setPaused(false);

        try {
            if (!isNull) {
                long duration = musicManager.player.getPlayingTrack().getDuration();
                long position = musicManager.player.getPlayingTrack().getPosition();
                Thread.sleep(duration - position);

                musicManager.player.setPaused(true);
                musicManager.scheduler.nextTrack();
                musicManager.player.setPaused(false);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!isNull) musicManager.player.getPlayingTrack().setPosition(positionOfPlayingTrack);
        queue.addAll(tracks);
    }

    @Override
    public String getName() {
        return "play";
    }
}
