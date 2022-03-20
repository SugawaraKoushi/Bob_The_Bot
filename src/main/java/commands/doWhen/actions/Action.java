package commands.doWhen.actions;

import commands.doWhen.conditions.Condition;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface Action {
    void setQuery(String query);
    void setCondition(Condition condition);
    void handle(SlashCommandInteractionEvent event);
    String getName();
}
