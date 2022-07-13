package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Random;

public class Output{
    private MessageEmbed messageEmbed;

    public Output(String string) {
        Random color = new Random();
        int r = color.nextInt(256);
        int g = color.nextInt(256);
        int b = color.nextInt(256);

        messageEmbed = new EmbedBuilder()
                .setColor(new Color(r, g, b))
                .setDescription(string)
                .build();
    }

    public MessageEmbed getME(){
        return messageEmbed;
    }
}
