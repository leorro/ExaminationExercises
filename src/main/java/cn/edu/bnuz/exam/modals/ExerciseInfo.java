package cn.edu.bnuz.exam.modals;

import java.io.Serializable;

public class ExerciseInfo implements Serializable {
    private String type;
    private String topic;
    private String[] options;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }
}
