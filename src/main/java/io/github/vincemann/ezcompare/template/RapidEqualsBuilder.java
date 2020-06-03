package io.github.vincemann.ezcompare.template;

import com.google.common.collect.Lists;
import io.github.vincemann.ezcompare.util.ReflectionUtils;
import lombok.*;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copied and modified from {@link org.mockito.internal.matchers.apachecommons.EqualsBuilder}.
 *
 * @modfiedBy Vincemann
 * @see RapidArgumentMatchers#fullRefEq(Object, String...)
 */
@SuppressWarnings("unchecked")
public class RapidEqualsBuilder {

    private final static Logger log = Logger.getLogger(RapidEqualsBuilder.class.getName());


    /**
     * If the fields tested are equals.
     * The default value is <code>true</code>.
     */
    private Diff diff;
    private CompareConfig config;



    @ToString
    public static class CompareConfig {
        //                                      default config
        protected boolean ignoreNull            =          false;
        protected boolean ignoreNotFound        =          false;
        protected boolean useNullForNotFound    =          false;
        protected Set<String> ignoredProperties =          new HashSet<>();
        protected Class<?> reflectUpToClass     =          null;
        protected boolean minimalDiff           =          false;

        /**
         * Called before compare to apply runtime config parts
         * i.E. included Properties by SuperClass needs class of RootActor to determine all properties in order to determine ignoredProperties
         * @param rootActor
         * @param compareActor
         */
        protected void init(Object rootActor,Object compareActor){}

        public Class<?> getReflectUpToClass() {
            return reflectUpToClass;
        }
        public boolean isMinimalDiff() {
            return minimalDiff;
        }

        public void setReflectUpToClass(Class<?> c) {
            reflectUpToClass = c;
        }

        public void setMinimalDiff(boolean v) {
            minimalDiff=v;
        }

    }
    /**
     * <p>Constructor for RapidEqualsBuilder.</p>
     *
     * <p>Starts off assuming that equals is <code>true</code>.</p>
     *
     * @see Object#equals(Object)
     */
    public RapidEqualsBuilder(CompareConfig config) {
        this.config = config;
        this.diff = Diff.builder()
                .different(false)
                .minimal(config.minimalDiff)
                .build();
    }


    //-------------------------------------------------------------------------

    /**
     * <p>This method uses reflection to determine if the two <code>Object</code>s
     * are equal.</p>
     *
     * <p>It uses <code>AccessibleObject.setAccessible</code> to gain access to private
     * fields. This means that it will throw a security exception if run under
     * a security manager, if the permissions are not set up correctly. It is also
     * not as efficient as testing explicitly.</p>
     *
     * <p>Transient members will be not be tested, as they are likely derived
     * fields, and not part of the value of the Object.</p>
     *
     * <p>Static fields will not be tested. Superclass fields will be included.</p>
     *
     * @param root          <code>this</code> object
     * @param compare       the other object
     * @return <code>true</code> if the two Objects have tested equals.
     */

    public static Diff reflectionEquals(Object root, Object compare) {
        return reflectionEquals(root, compare, new CompareConfig());
    }


    /**
     * <p>This method uses reflection to determine if the two <code>Object</code>s
     * are equal.</p>
     *
     * <p>It uses <code>AccessibleObject.setAccessible</code> to gain access to private
     * fields. This means that it will throw a security exception if run under
     * a security manager, if the permissions are not set up correctly. It is also
     * not as efficient as testing explicitly.</p>
     *
     * <p>If the testTransients parameter is set to <code>true</code>, transient
     * members will be tested, otherwise they are ignored, as they are likely
     * derived fields, and not part of the value of the <code>Object</code>.</p>
     *
     * <p>Static fields will not be included. Superclass fields will be appended
     * up to and including the specified superclass. A null superclass is treated
     * as java.lang.Object.</p>
     *
     * @param root             <code>this</code> object
     * @param compare          the other object
     * @return <code>true</code> if the two Objects have tested equals.
     * @since 2.1.0
     */
    public static Diff reflectionEquals(Object root, Object compare, CompareConfig config) {
        if (config==null){
            throw new IllegalArgumentException("Config must not be null");
        }
        config.init(root,compare);
        
        if (root == null || compare == null) {
            throw new IllegalArgumentException("Comparing Objects must not be null");
        }

        // Find the leaf class since there may be transients in the leaf
        // class or in classes between the leaf and root.
        // If we are not testing transients or a subclass has no ivars,
        // then a subclass can test equals to a superclass.

        //always choose roots class for comparisson
        Class<?> rootClass = root.getClass();
        Class<?> compareClass = compare.getClass();

        RapidEqualsBuilder equalsBuilder = new RapidEqualsBuilder(config);
        
        if (root == compare) {
            //in init state is diff set to 'not different'
            return equalsBuilder.diff;
        }

        try {
            reflectionAppend(root, compare, rootClass, compareClass, equalsBuilder, config);
            while (rootClass.getSuperclass() != null && rootClass != config.reflectUpToClass) {
                rootClass = rootClass.getSuperclass();
                reflectionAppend(root, compare, rootClass, compareClass, equalsBuilder, config);
            }
        } catch (IllegalArgumentException e) {
            log.warning("Objects differ, but not exact property known: In this case, we tried to test a subclass vs. a superclass and\n" +
                    "             the subclass has ivars or the ivars are transient and\n" +
                    "             we are testing transients.\n" +
                    "             If a subclass has ivars that we are trying to test them, we get an\n" +
                    "             exception and we know that the objects are not equal.");

            return Diff.builder()
                    .different(true)
                    .minimal(config.minimalDiff)
                    //rest is unknown at this point
                    .build();
        }
        return equalsBuilder.getDiff();
    }

    /**
     * <p>Appends the fields and values defined by the given object of the
     * given Class.</p>
     *
     * @param root          the left hand object
     * @param compare       the right hand object
     * @param rootClass     the class to append details of
     * @param builder       the builder to append to
     */
    private static void reflectionAppend(
            Object root,
            Object compare,
            Class<?> rootClass,
            Class<?> compareClass,
            RapidEqualsBuilder builder,
            CompareConfig config) {

        Set<String> properties = ReflectionUtils.getProperties(rootClass);
        properties.removeAll(Lists.newArrayList(config.ignoredProperties));


        for (String property : properties) {
            try {
                //cant use ReflectionUtilsBean bc this module is not Spring specific
                Field rootField = FieldUtils.getField(rootClass, property, true);
                Object rootValue = rootField.get(root);

                if (rootValue==null && config.ignoreNull){
                    log.log(Level.INFO,"IgnoreNull enabled, ignoring field: " + rootField.getName());
                    continue;
                }

                Field compareField = FieldUtils.getField(compareClass, property, true);
                Object compareValue;
                if (compareField==null){
                    log.log(Level.INFO,"Did not find property: " + property + " in compare object: " + compare);
                    if (config.ignoreNotFound) {
                        log.log(Level.INFO, "IgnoreNotFound enabled, ignoring field: " + property);
                        continue;
                    }else {
                        if (config.useNullForNotFound) {
                            log.log(Level.INFO, "UseNullForNotFound enabled -> Using null for compares not found field's value.");
                            compareValue = null;
                        }else {
                            throw new PropertyNotFoundException("Property: " + property + " not found in compare object. " +
                                    "Use 'useNullForNotFound' option to use null as default value.");
                        }
                    }
                }else{
                    compareValue = compareField.get(compare);
                }


                builder.append(rootValue, compareValue, property);
            } catch (IllegalAccessException e) {
                //this can't happen. Would get a Security exception instead
                //throw a runtime exception in case the impossible happens.
                throw new InternalError("Unexpected IllegalAccessException");
            }

        }
    }


    /**
     * <p>Adds the result of <code>super.equals()</code> to this builder.</p>
     *
     * @param superEquals the result of calling <code>super.equals()</code>
     * @return RapidEqualsBuilder - used to chain calls.
     * @since 2.1.0
     */
    public RapidEqualsBuilder appendSuper(boolean superEquals) {
        boolean isEquals = isEquals();
        isEquals &= superEquals;
        return this;
    }


    //-------------------------------------------------------------------------

    /**
     * <p>Test if two <code>Object</code>s are equal using their
     * <code>equals</code> method.</p>
     *
     * @param rootProperty    the left hand object
     * @param compareProperty the right hand object
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(Object rootProperty, Object compareProperty, String propertyName) {
        if (done()) {
            return this;
        }
        if (rootProperty == compareProperty) {
            return this;
        }
        if (rootProperty == null || compareProperty == null) {
            this.addDiff(propertyName, rootProperty, compareProperty);
            return this;
        }
        Class<?> lhsClass = rootProperty.getClass();
        if (!lhsClass.isArray()) {
            if (rootProperty instanceof java.math.BigDecimal && compareProperty instanceof java.math.BigDecimal) {
                boolean isEquals = (((java.math.BigDecimal) rootProperty).compareTo((java.math.BigDecimal) compareProperty) == 0);
                if (!isEquals)
                    addDiff(propertyName, rootProperty, compareProperty);
            } else {
                // The simple case, not an array, just test the element
                boolean isEquals = rootProperty.equals(compareProperty);
                if (!isEquals)
                    addDiff(propertyName, rootProperty, compareProperty);
            }
        } else if (rootProperty.getClass() != compareProperty.getClass()) {
            // Here when we compare different dimensions, for example: a boolean[][] to a boolean[]
            this.addDiff(propertyName, rootProperty, compareProperty);

            // 'Switch' on type of array, to dispatch to the correct handler
            // This handles multi dimensional arrays of the same depth
        } else if (rootProperty instanceof long[]) {
            append((long[]) rootProperty, (long[]) compareProperty, propertyName);
        } else if (rootProperty instanceof int[]) {
            append((int[]) rootProperty, (int[]) compareProperty, propertyName);
        } else if (rootProperty instanceof short[]) {
            append((short[]) rootProperty, (short[]) compareProperty, propertyName);
        } else if (rootProperty instanceof char[]) {
            append((char[]) rootProperty, (char[]) compareProperty, propertyName);
        } else if (rootProperty instanceof byte[]) {
            append((byte[]) rootProperty, (byte[]) compareProperty, propertyName);
        } else if (rootProperty instanceof double[]) {
            append((double[]) rootProperty, (double[]) compareProperty, propertyName);
        } else if (rootProperty instanceof float[]) {
            append((float[]) rootProperty, (float[]) compareProperty, propertyName);
        } else if (rootProperty instanceof boolean[]) {
            append((boolean[]) rootProperty, (boolean[]) compareProperty, propertyName);
        } else {
            // Not an array of primitives
            append((Object[]) rootProperty, (Object[]) compareProperty, propertyName);
        }
        return this;
    }

    //-------------------------------------------------------------------------

    /**
     * <p>
     * Test if two <code>long</code> s are equal.
     * </p>
     *
     * @param lhs the left hand <code>long</code>
     * @param rhs the right hand <code>long</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(long lhs, long rhs) {
        boolean isEquals = isEquals();
        isEquals &= (lhs == rhs);
        return this;
    }

    /**
     * <p>Test if two <code>int</code>s are equal.</p>
     *
     * @param lhs the left hand <code>int</code>
     * @param rhs the right hand <code>int</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(int lhs, int rhs) {
        boolean isEquals = isEquals();
        isEquals &= (lhs == rhs);
        return this;
    }

    /**
     * <p>Test if two <code>short</code>s are equal.</p>
     *
     * @param lhs the left hand <code>short</code>
     * @param rhs the right hand <code>short</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(short lhs, short rhs) {
        boolean isEquals = isEquals();
        isEquals &= (lhs == rhs);
        return this;
    }

    /**
     * <p>Test if two <code>char</code>s are equal.</p>
     *
     * @param lhs the left hand <code>char</code>
     * @param rhs the right hand <code>char</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(char lhs, char rhs) {
        boolean isEquals = isEquals();
        isEquals &= (lhs == rhs);
        return this;
    }

    /**
     * <p>Test if two <code>byte</code>s are equal.</p>
     *
     * @param lhs the left hand <code>byte</code>
     * @param rhs the right hand <code>byte</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(byte lhs, byte rhs) {
        boolean isEquals = isEquals();
        isEquals &= (lhs == rhs);
        return this;
    }

    /**
     * <p>Test if two <code>double</code>s are equal by testing that the
     * pattern of bits returned by <code>doubleToLong</code> are equal.</p>
     *
     * <p>This handles NaNs, Infinities, and <code>-0.0</code>.</p>
     *
     * <p>It is compatible with the hash code generated by
     * <code>HashCodeBuilder</code>.</p>
     *
     * @param lhs the left hand <code>double</code>
     * @param rhs the right hand <code>double</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(double lhs, double rhs) {
        if (!isEquals()) {
            return this;
        }
        return append(Double.doubleToLongBits(lhs), Double.doubleToLongBits(rhs));
    }

    /**
     * <p>Test if two <code>float</code>s are equal byt testing that the
     * pattern of bits returned by doubleToLong are equal.</p>
     *
     * <p>This handles NaNs, Infinities, and <code>-0.0</code>.</p>
     *
     * <p>It is compatible with the hash code generated by
     * <code>HashCodeBuilder</code>.</p>
     *
     * @param lhs the left hand <code>float</code>
     * @param rhs the right hand <code>float</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(float lhs, float rhs) {
        if (done()) {
            return this;
        }
        return append(Float.floatToIntBits(lhs), Float.floatToIntBits(rhs));
    }

    /**
     * <p>Test if two <code>booleans</code>s are equal.</p>
     *
     * @param lhs the left hand <code>boolean</code>
     * @param rhs the right hand <code>boolean</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(boolean lhs, boolean rhs) {
        boolean isEquals = isEquals();
        isEquals &= (lhs == rhs);
        return this;
    }

    /**
     * <p>Performs a deep comparison of two <code>Object</code> arrays.</p>
     *
     * <p>This also will be called for the top level of
     * multi-dimensional, ragged, and multi-typed arrays.</p>
     *
     * @param lhs the left hand <code>Object[]</code>
     * @param rhs the right hand <code>Object[]</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(Object[] lhs, Object[] rhs, String property) {
        if (done()) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals(); ++i) {
            append(lhs[i], rhs[i], property);
        }
        return this;
    }

    /**
     * <p>Deep comparison of array of <code>long</code>. Length and all
     * values are compared.</p>
     *
     * <p>The method {@link #append(long, long)} is used.</p>
     *
     * @param lhs the left hand <code>long[]</code>
     * @param rhs the right hand <code>long[]</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(long[] lhs, long[] rhs, String property) {
        if (done()) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        for (int i = 0; i < lhs.length && !done(); ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * <p>Deep comparison of array of <code>int</code>. Length and all
     * values are compared.</p>
     *
     * <p>The method {@link #append(int, int)} is used.</p>
     *
     * @param lhs the left hand <code>int[]</code>
     * @param rhs the right hand <code>int[]</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(int[] lhs, int[] rhs, String property) {
        if (done()) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals(); ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * <p>Deep comparison of array of <code>short</code>. Length and all
     * values are compared.</p>
     *
     * <p>The method {@link #append(short, short)} is used.</p>
     *
     * @param lhs the left hand <code>short[]</code>
     * @param rhs the right hand <code>short[]</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(short[] lhs, short[] rhs, String property) {
        if (done()) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        for (int i = 0; i < lhs.length && !done(); ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * <p>Deep comparison of array of <code>char</code>. Length and all
     * values are compared.</p>
     *
     * <p>The method {@link #append(char, char)} is used.</p>
     *
     * @param lhs the left hand <code>char[]</code>
     * @param rhs the right hand <code>char[]</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(char[] lhs, char[] rhs, String property) {
        if (done()) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        for (int i = 0; i < lhs.length && !done(); ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * <p>Deep comparison of array of <code>byte</code>. Length and all
     * values are compared.</p>
     *
     * <p>The method {@link #append(byte, byte)} is used.</p>
     *
     * @param lhs the left hand <code>byte[]</code>
     * @param rhs the right hand <code>byte[]</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(byte[] lhs, byte[] rhs, String property) {
        if (done()) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        for (int i = 0; i < lhs.length && !done(); ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * <p>Deep comparison of array of <code>double</code>. Length and all
     * values are compared.</p>
     *
     * <p>The method {@link #append(double, double)} is used.</p>
     *
     * @param lhs the left hand <code>double[]</code>
     * @param rhs the right hand <code>double[]</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(double[] lhs, double[] rhs, String property) {
        if (done()) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        for (int i = 0; i < lhs.length && !done(); ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * <p>Deep comparison of array of <code>float</code>. Length and all
     * values are compared.</p>
     *
     * <p>The method {@link #append(float, float)} is used.</p>
     *
     * @param lhs the left hand <code>float[]</code>
     * @param rhs the right hand <code>float[]</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(float[] lhs, float[] rhs, String property) {
        if (done()) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        for (int i = 0; i < lhs.length && !done(); ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * <p>Deep comparison of array of <code>boolean</code>. Length and all
     * values are compared.</p>
     *
     * <p>The method {@link #append(boolean, boolean)} is used.</p>
     *
     * @param lhs the left hand <code>boolean[]</code>
     * @param rhs the right hand <code>boolean[]</code>
     * @return RapidEqualsBuilder - used to chain calls.
     */
    public RapidEqualsBuilder append(boolean[] lhs, boolean[] rhs, String property) {
        if (done()) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.addDiff(property, lhs, rhs);
            return this;
        }
        for (int i = 0; i < lhs.length && !done(); ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    public Diff getDiff() {
        return this.diff;
    }

    /**
     * <p>Returns <code>true</code> if the fields that have been checked
     * are all equal.</p>
     *
     * @return boolean
     */
    public boolean isEquals() {
        return !this.diff.isDifferent();
    }
    
    public boolean minimalDiff(){
        return this.config.minimalDiff;
    }

    /**
     * Indicates that it is already clear that objects differ and only minimal diff is required
     * -> algorithm can stop and return result.
     * @return
     */
    public boolean done(){
        return !isEquals() && minimalDiff();
    }

    /**
     * Sets the <code>minimalDiff</code> value.
     *
     * @since 2.1
     */
    protected void addDiff(String property, Object rootValue, Object compareValue) {
        this.diff = Diff.builder()
                .minimal(config.minimalDiff)
                .property(property)
                .rootValue(rootValue)
                .compareValue(compareValue)
                .different(true)
                .build();
    }

    @Getter
    @ToString
    @Builder(access = AccessLevel.PROTECTED)
    public static class Diff {
        private String property;
        private Object rootValue;
        private Object compareValue;
        private Boolean different = Boolean.FALSE;
        private Boolean minimal = null;

        public boolean isDifferent() {
            return this.different;
        }

        public boolean isEmpty() {
            return !different;
        }
    }
}
