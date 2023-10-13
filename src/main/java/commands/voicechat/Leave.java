package commands.voicechat;

import commands.ICommand;
import commands.Output;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Objects;

public class Leave implements ICommand {
    String message;

    @Override
    public void handle(SlashCommandInteractionEvent event) throws InterruptedException {
        final AudioManager audioManager = Objects.requireNonNull(event.getGuild()).getAudioManager();
        final GuildVoiceState memberVoiceState = Objects.requireNonNull(event.getMember()).getVoiceState();

        if(memberVoiceState != null) {
            if(!memberVoiceState.inAudioChannel()){
                message = "**You are not in voice channel to do this**";
                event.deferReply().queue();
                event.getHook().sendMessageEmbeds(getME()).queue();
                return;
            }
        }

        if(!audioManager.isConnected()){
            message = "**Bob can't leave nothing**";
            event.deferReply().queue();
            event.getHook().sendMessageEmbeds(getME()).queue();
            return;
        }

        message = "**Bob have left voice channel**";
        event.deferReply().queue();
        event.getHook().sendMessageEmbeds(getME()).queue();
        audioManager.closeAudioConnection();
    }

    @Override
    public MessageEmbed getME() {
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "leave";
    }
}
