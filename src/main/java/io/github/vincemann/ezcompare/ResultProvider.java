package io.github.vincemann.ezcompare;

public interface ResultProvider {
    public RapidEqualsBuilder.Diff getDiff();
    public boolean isEqual();
    public ActorBridge and();
}
