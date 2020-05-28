package io.github.vincemann.ezcompare;

import java.io.Serializable;

public interface IdentifiableEntity<Id extends Serializable> extends Serializable {

    public Id getId();

    public void setId(Id id);
}
