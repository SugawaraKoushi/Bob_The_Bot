package commands.doWhen.conditions;

import main.Main;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;

public class JoinUser implements Condition {
    private Member member;

    @Override
    public void setCondition(String condition) {
        long id = Long.parseLong(condition.substring(3, condition.length() - 1));
        member = Main.getGuild().getMemberById(id);
    }

    @Override
    public Boolean getTruth() {
        if (member == null || !member.getVoiceState().inAudioChannel()) {
            return false;
        }

        AudioChannelUnion botVoiceChannel = Main.getGuild().getSelfMember().getVoiceState().getChannel();
        AudioChannelUnion memberVoiceChannel = member.getVoiceState().getChannel();

        if (botVoiceChannel == null) {
            return member.getVoiceState().inAudioChannel();
        }

        return botVoiceChannel.compareTo(memberVoiceChannel) == 0;
    }

    @Override
    public String getName() {
        return "when-user-in-vc";
    }
}
