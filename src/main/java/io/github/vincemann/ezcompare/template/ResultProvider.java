package io.github.vincemann.ezcompare.template;

public interface ResultProvider {
    public RapidEqualsBuilder.Diff getDiff();
    public boolean isEqual();
}
