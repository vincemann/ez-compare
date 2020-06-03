package io.github.vincemann.ezcompare.template;

import lombok.Getter;
import lombok.Setter;
import org.mockito.ArgumentMatcher;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Config-based Reflection object comparison tool, that creates {@link io.github.vincemann.ezcompare.template.RapidEqualsBuilder.Diff}
 * and also logs it, if objects differ.
 * Only takes non static fields from root (expected) object class into consideration.
 * Does not perform deep compare -> only scans fields from root and performs equal on all of those (depth = 1).
 * Does not care about type -> only field names must match.
 */
public class RapidReflectionEquals implements ArgumentMatcher<Object>, Serializable {

    private final static Logger log = Logger.getLogger(RapidReflectionEquals.class.getName());

    private final Object compare;
    @Getter
    private RapidEqualsBuilder.Diff diff;
    @Setter
    private RapidEqualsBuilder.CompareConfig config;


    public RapidReflectionEquals(Object compare, RapidEqualsBuilder.CompareConfig config) {
        this.compare = compare;
        this.config = config;
        log.log(Level.INFO,"Using CompareConfig: " + this.config.toString());
    }



    public boolean matches(Object root) {
        diff = RapidEqualsBuilder.reflectionEquals(compare, root, config);
        if (diff.isDifferent()) {
            log.log(Level.INFO, "Compare: " + compare + "and root: " + root + " differ:");
            log.log(Level.INFO, diff.toString());
        }
        return !diff.isDifferent();
    }

    public String toString() {
        return "rapidRefEq(" + compare + ")";
    }
}
