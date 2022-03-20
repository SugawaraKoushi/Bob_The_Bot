package commands.doWhen;

import commands.ICommand;
import commands.Output;
import commands.doWhen.actions.Action;
import commands.doWhen.actions.Play;
import commands.doWhen.conditions.Condition;
import commands.doWhen.conditions.JoinUser;
import commands.doWhen.conditions.Time;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.HashMap;

public class Do implements ICommand {
    private static boolean isBusy = false;
    private String message;
    private final HashMap<String, Action> actions = new HashMap<>();
    private final HashMap<String, Condition> conditions = new HashMap<>();

    public Do() {
        addAction(new Play());

        addCondition(new JoinUser());
        addCondition(new Time());
    }

    void addAction(Action action) {
        actions.put(action.getName(), action);
    }

    void addCondition(Condition condition) {
        conditions.put(condition.getName(), condition);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        Condition cndtn = null;
        Action act = null;

        if (isBusy) {
            message = "**Bob already awaits something to do**";
            event.replyEmbeds(getME()).queue();
            return;
        }

        if (event.getOptions().size() > 3) {
            message = "**Only 2 options should be set**";
            event.replyEmbeds(getME()).queue();
            return;
        }

        int actionsCount = 0;
        int conditionsCount = 0;
        for (OptionMapping option : event.getOptions()) {
            if (actions.containsKey(option.getName())) {
                act = actions.get(option.getName());
                act.setQuery(option.getAsString());
                actionsCount++;
            }

            if (conditions.containsKey(option.getName())) {
                cndtn = conditions.get(option.getName());
                cndtn.setCondition(option.getAsString());
                conditionsCount++;
            }
        }

        if (actionsCount > 1 || conditionsCount > 1 || actionsCount == 0 || conditionsCount == 0) {
            message = "**Only 1 action and 1 conditions should be set**";
            event.replyEmbeds(getME()).queue();
            return;
        }

        act.setCondition(cndtn);
        Action finalAct = act;
        message = String.format("**Do %s:** %s\n**When %s: **%s",
                act.getName(),
                event.getOption(act.getName()).getAsString(),
                cndtn.getName(),
                event.getOption(cndtn.getName()).getAsString()
        );
        event.replyEmbeds(getME()).queue();

        setBusy(true);
        Thread thread = new Thread(() -> finalAct.handle(event));
        thread.start();
    }

    @Override
    public MessageEmbed getME() {
        return new Output(message).getME();
    }

    @Override
    public String getName() {
        return "do";
    }

    public static void setBusy(boolean busy) {
        isBusy = busy;
    }

    public static boolean getBusy() {
        return isBusy;
    }
}
