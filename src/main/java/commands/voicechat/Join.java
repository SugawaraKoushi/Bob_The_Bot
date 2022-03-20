package commands.voicechat;

import commands.ICommand;
import commands.Output;
import main.Config;
import music.PlayerManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class Join implements ICommand {
    String message;

    @Override
    public void handle(SlashCommandInteractionEvent event) throws InterruptedException {
        final PlayerManager playerManager = PlayerManager.getInstance();
        final AudioManager audioManager = event.getGuild().getAudioManager();
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
        final AudioChannel voiceChannel = memberVoiceState.getChannel();
        final Member selfMember = event.getGuild().getSelfMember();

        if(audioManager.isConnected()){
            message = "**Bob is already connected**";
            event.replyEmbeds(getME()).queue();
            return;
        }

        if(!memberVoiceState.inAudioChannel()){
            message = "**You are not in voice channel to do this**";
            event.replyEmbeds(getME()).queue();
            return;
        }

        if(!selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT)){
            message = "**Bob has no permission to join in**";
            event.replyEmbeds(getME()).queue();
            return;
        }

        playerManager.loadAndPlay(event.getTextChannel(), Config.get("SOUNDS_FOLDER") + "Bob, do something.ogg");

        Thread.sleep(100);

        message = String.format("**Joining:** %s", voiceChannel.getName());
        event.replyEmbeds(getME()).queue();
        audioManager.openAudioConnection(voiceChannel);
    }

    @Override
    public MessageEmbed getME(){
        return new Output(message).getME();
    }

    @Override
    public String getName(){
        return "join";
    }
}
