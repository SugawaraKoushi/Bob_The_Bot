package commands.doWhen.actions;

import commands.Output;
import commands.doWhen.conditions.Condition;
import main.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class Join implements Action {
    private String message;
    private Member member;
    private Condition condition;

    @Override
    public void setQuery(String query) {
        long id = Long.parseLong(query.substring(3, query.length() - 1));
        member = Main.getGuild().getMemberById(id);
    }

    @Override
    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        final AudioManager audioManager = event.getGuild().getAudioManager();
        final GuildVoiceState memberVoiceState = member.getVoiceState();
        final AudioChannel audioChannel = memberVoiceState.getChannel();
        final Member selfMember = event.getGuild().getSelfMember();

        if (member == null || !member.getVoiceState().inAudioChannel()) {
            message = "**Member is not in voicechat**";
            event.replyEmbeds(new Output(message).getME()).queue();
            return;
        }

        if (audioManager.isConnected()) {
            message = "**Bob is already connected**";
            event.replyEmbeds(new Output(message).getME()).queue();
            return;
        }

        if (!memberVoiceState.inAudioChannel()) {
            message = "**You are not in voice channel to do this**";
            event.replyEmbeds(new Output(message).getME()).queue();
            return;
        }

        if (!selfMember.hasPermission(audioChannel, Permission.VOICE_CONNECT)) {
            message = "**Bob has no permission to join in**";
            event.replyEmbeds(new Output(message).getME()).queue();
            return;
        }

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (condition.getTruth()) {
                break;
            }
        }

        message = String.format("**Joining:** %s", audioChannel.getName());
        event.replyEmbeds(new Output(message).getME()).queue();
        audioManager.openAudioConnection(audioChannel);
    }

    @Override
    public String getName() {
        return "join-to";
    }
}
