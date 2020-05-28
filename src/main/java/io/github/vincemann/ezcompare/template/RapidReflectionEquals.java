package io.github.vincemann.ezcompare.template;

import lombok.Getter;
import lombok.Setter;
import org.mockito.ArgumentMatcher;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @see RapidArgumentMatchers#fullRefEq(Object, String...)
 */
public class RapidReflectionEquals implements ArgumentMatcher<Object>, Serializable {

    private final static Logger log = Logger.getLogger(RapidReflectionEquals.class.getName());

    private final Object wanted;
    @Getter
    private RapidEqualsBuilder.MinimalDiff minimalDiff;
    @Setter
    private RapidEqualsBuilder.CompareConfig config;

//    public RapidReflectionEquals(Object wanted, String... ignoredProperties) {
//        this(
//                wanted,
//                CompareTemplate.FullCompareConfig.builder()
//                        .ignoredProperties(Sets.newHashSet(ignoredProperties))
//                        .build()
//                        .convert()
//        );
//    }
//
//    public RapidReflectionEquals(Object wanted, boolean ignoreNull, String... ignoredProperties) {
//        this(
//                wanted,
//                CompareTemplate.FullCompareConfig.builder()
//                        .ignoreNull(ignoreNull)
//                        .ignoredProperties(Sets.newHashSet(ignoredProperties))
//                        .build()
//                        .convert()
//        );
//    }

    public RapidReflectionEquals(Object wanted, RapidEqualsBuilder.CompareConfig config) {
        this.wanted = wanted;
        this.config = config;
        log.log(Level.INFO,"Using CompareConfig: " + this.config.toString());
    }


    public boolean matches(Object actual) {
        minimalDiff = RapidEqualsBuilder.reflectionEquals(wanted, actual, config);
        if (minimalDiff.isDifferent()) {
            log.log(Level.INFO, "Wanted: " + wanted + "and actual: " + actual + " differ:");
            log.log(Level.INFO, minimalDiff.toString());
        }
        return minimalDiff.isEmpty();
    }

    public String toString() {
        return "rapidRefEq(" + wanted + ")";
    }
}
