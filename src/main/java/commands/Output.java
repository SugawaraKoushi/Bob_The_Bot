package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.awt.*;

public class Output{
    private MessageEmbed messageEmbed;

    public Output(String string) {
        messageEmbed = new EmbedBuilder()
                .setColor(new Color(174, 92, 250))
                .setDescription(string)
                .build();
    }

    public MessageEmbed getME(){
        return messageEmbed;
    }
}
