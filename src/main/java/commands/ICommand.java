package commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface ICommand {
    void handle(SlashCommandInteractionEvent event) throws Exception;
    MessageEmbed getME();
    String getName();
}
