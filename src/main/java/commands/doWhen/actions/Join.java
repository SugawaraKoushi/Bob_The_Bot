package commands.doWhen.actions;

import commands.Output;
import commands.doWhen.conditions.Condition;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import static commands.doWhen.Do.setBusy;

public class Join implements Action {
    private String message;
    private String audioChannelID;
    private Condition condition;

    @Override
    public void setQuery(String query) {
        audioChannelID = query;
    }

    @Override
    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        final AudioManager audioManager = event.getGuild().getAudioManager();
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
        final AudioChannel voiceChannel = memberVoiceState.getChannel();
        final Member selfMember = event.getGuild().getSelfMember();

        if(audioManager.isConnected()){
            message = "**Bob is already connected**";
            event.replyEmbeds(new Output(message).getME()).queue();
            setBusy(false);
            return;
        }

        if(!memberVoiceState.inAudioChannel()){
            message = "**You are not in voice channel to do this**";
            event.replyEmbeds(new Output(message).getME()).queue();
            setBusy(false);
            return;
        }

        if(!selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT)){
            message = "**Bob has no permission to join in**";
            event.replyEmbeds(new Output(message).getME()).queue();
            setBusy(false);
            return;
        }

        while(true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e ) {
                e.printStackTrace();
            }

            if (condition.getTruth()) {
                setBusy(false);
                break;
            }

            message = String.format("**Joining:** %s", voiceChannel.getName());
            event.replyEmbeds(new Output(message).getME()).queue();
            audioManager.openAudioConnection(voiceChannel);
        }
    }

    @Override
    public String getName() {
        return null;
    }
}
