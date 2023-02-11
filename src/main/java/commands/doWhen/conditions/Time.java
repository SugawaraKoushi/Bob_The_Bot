package commands.doWhen.conditions;

import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time implements Condition {
    private LocalTime conditionTime;
    private static final Pattern TIME_PATTERN = Pattern.compile("([0-9]{2}):([0-9]{2})");

    @Override
    public void setCondition(String condition) {
        Matcher matcher = TIME_PATTERN.matcher(condition);

        if (condition.matches(TIME_PATTERN.pattern())) {
            if (!matcher.find()) {
                conditionTime = null;
                return;
            }

            int hour = Integer.parseInt(matcher.group(1));
            boolean lessHours = hour < 24 && hour >= 0;

            int minute = Integer.parseInt(matcher.group(2));
            boolean lessMinute = minute < 60 && minute >= 0;

            if (lessHours && lessMinute) {
                conditionTime = LocalTime.of(hour, minute);
            } else {
                conditionTime = null;
            }
        } else {
            conditionTime = null;
        }
    }

    @Override
    public Boolean getTruth() {
        if (conditionTime == null) {
            throw new NullPointerException("Error: condition time is not set");
        }

        LocalTime currentTime = LocalTime.now();

        return currentTime.isAfter(conditionTime);
    }

    @Override
    public String getName() {
        return "when-time-is";
    }
}
