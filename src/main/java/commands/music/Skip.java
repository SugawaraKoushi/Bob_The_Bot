package commands.music;

import commands.ICommand;
import commands.Output;
import music.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Skip implements ICommand {
    private String message;

    @Override
    public void handle(SlashCommandInteractionEvent event) throws InterruptedException {
        final PlayerManager playerManager = PlayerManager.getInstance();
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

        if (memberVoiceState != null) {
            if (!memberVoiceState.inAudioChannel()) {
                message = "**You are not in voice channel to do this**";
                event.replyEmbeds(getME()).queue();
                return;
            }
        }

        if (playerManager.getGuildMusicManager(event.getGuild()).scheduler == null) {
            message = "**Bob can't skip nothing**";
            event.deferReply().queue();
            event.getHook().sendMessageEmbeds(getME()).queue();
            return;
        }

        message = "**Skipped**";
        event.deferReply().queue();
        event.getHook().sendMessageEmbeds(getME()).queue();
        playerManager.getGuildMusicManager(event.getGuild()).scheduler.nextTrack();
    }

    @Override
    public MessageEmbed getME() {
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "skip";
    }
}
