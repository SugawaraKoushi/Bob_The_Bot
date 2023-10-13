package commands.music;

import commands.ICommand;
import commands.Output;
import main.Main;
import music.GuildMusicManager;
import music.PlayerManager;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Pause implements ICommand {
    private String message;

    @Override
    public void handle(SlashCommandInteractionEvent event) throws InterruptedException {
        final PlayerManager playerManager = PlayerManager.getInstance();
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
        final GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        if(memberVoiceState != null) {
            if(!memberVoiceState.inAudioChannel()){
                message = "**You are not in voice channel to do this**";
                event.deferReply().queue();
                event.getHook().sendMessageEmbeds(getME()).queue();
                return;
            }
        }

        if(musicManager.player.getPlayingTrack() == null){
            message = "**Bob is not playing anything**";
            event.deferReply().queue();
            event.getHook().sendMessageEmbeds(getME()).queue();
            return;
        }

        playerManager.getGuildMusicManager(event.getGuild()).player.setPaused(true);
        message = "**Pause:** on";
        event.deferReply().queue();
        event.getHook().sendMessageEmbeds(getME()).queue();
    }

    @Override
    public MessageEmbed getME() {
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "pause";
    }
}
