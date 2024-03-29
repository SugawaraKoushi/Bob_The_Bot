package commands.voicechat;

import commands.ICommand;
import commands.Output;
import main.Config;
import music.PlayerManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class Join implements ICommand {
    String message;

    @Override
    public void handle(SlashCommandInteractionEvent event) throws InterruptedException {
        final PlayerManager playerManager = PlayerManager.getInstance();
        final AudioManager audioManager = event.getGuild().getAudioManager();
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
        final AudioChannelUnion voiceChannel = memberVoiceState.getChannel();
        final Member selfMember = event.getGuild().getSelfMember();

        if(audioManager.isConnected()){
            message = "**Bob is already connected**";
            event.deferReply().queue();
            event.getHook().sendMessageEmbeds(getME()).queue();
            return;
        }

        if (!memberVoiceState.inAudioChannel()) {
            message = "**You are not in voice channel to do this**";
            event.deferReply().queue();
            event.getHook().sendMessageEmbeds(getME()).queue();
            return;
        }

        if(!selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT)){
            message = "**Bob has no permission to join in**";
            event.deferReply().queue();
            event.getHook().sendMessageEmbeds(getME()).queue();
            return;
        }

        if(Config.get("USING_SOUNDS").equals("TRUE"))
            playerManager.loadAndPlay(event.getChannel().asTextChannel(), Config.get("JOIN_TRACK"));

        Thread.sleep(100);

        message = String.format("**Joining:** %s", voiceChannel.getName());
        event.deferReply().queue();
        event.getHook().sendMessageEmbeds(getME()).queue();
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
