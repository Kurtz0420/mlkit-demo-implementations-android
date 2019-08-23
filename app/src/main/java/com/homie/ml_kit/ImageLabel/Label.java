package com.homie.ml_kit.ImageLabel;

public class Label {

    private String text;
    private String entity_id;
    private float confidence;

    @Override
    public String toString() {
        return "Label{" +
                "text='" + text + '\'' +
                ", entity_id='" + entity_id + '\'' +
                ", confidence='" + confidence + '\'' +
                '}';
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public Label() {
    }

    public Label(String text, String entity_id, float confidence) {
        this.text = text;
        this.entity_id = entity_id;
        this.confidence = confidence;
    }
}
