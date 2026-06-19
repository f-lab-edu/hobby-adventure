package com.jian.hobbyadventure.domain;

public enum Emotion {

    PROUD("뿌듯"),
    HAPPY("행복"),
    EXCITED("신남"),
    TOUCHED("감동"),
    CALM("평온"),
    BITTERSWEET("아쉬움"),
    DIFFICULT("힘들었어"),
    LONELY("외로움"),
    DISAPPOINTED("실망");

    private final String label;

    Emotion(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
