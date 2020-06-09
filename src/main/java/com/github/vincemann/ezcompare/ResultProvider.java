package com.github.vincemann.ezcompare;

public interface ResultProvider extends ContinueBridge{
    public RapidEqualsBuilder.Diff getDiff();
    public boolean isEqual();
}
