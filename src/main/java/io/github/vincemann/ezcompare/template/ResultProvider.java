package io.github.vincemann.ezcompare.template;

public interface ResultProvider {
    public RapidEqualsBuilder.MinimalDiff getDiff();
    public boolean isEqual();
}
