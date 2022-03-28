package commands.doWhen.actions;

import commands.Output;
import commands.doWhen.conditions.Condition;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class Leave implements Action{
    private String message;
    private String query;
    private Condition condition;

    @Override
    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        final AudioManager audioManager = event.getGuild().getAudioManager();
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

        if(!memberVoiceState.inAudioChannel()){
            message = "**You are not in voice channel to do this**";
            event.replyEmbeds(new Output(message).getME()).queue();
            return;
        }

        if(!audioManager.isConnected()){
            message = "**Bob can't leave nothing**";
            event.replyEmbeds(new Output(message).getME()).queue();
            return;
        }

        while(true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (condition.getTruth())
                break;
        }

        audioManager.closeAudioConnection();
    }


    @Override
    public String getName() {
        return null;
    }
}
