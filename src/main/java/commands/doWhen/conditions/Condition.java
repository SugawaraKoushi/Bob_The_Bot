package commands.doWhen.conditions;

public interface Condition {
    void setCondition(String condition);
    Boolean getTruth();
    String getName();
}
