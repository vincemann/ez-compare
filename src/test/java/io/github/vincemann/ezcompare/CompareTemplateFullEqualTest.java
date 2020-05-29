package io.github.vincemann.ezcompare;

import io.github.vincemann.ezcompare.template.PropertyNotFoundException;
import io.github.vincemann.ezcompare.template.RapidEqualsBuilder;
import io.github.vincemann.ezcompare.template.CompareTemplate;
import io.github.vincemann.ezcompare.template.ResultProvider;
import io.github.vincemann.ezcompare.util.MethodNameUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.github.vincemann.ezcompare.template.CompareTemplate.compare;


class CompareTemplateFullEqualTest {


    @BeforeEach
    void setUp() {
        Assertions.assertNull(CompareTemplate.GLOBAL_PARTIAL_COMPARE_CONFIG);
    }

    @Test
    void allValuesSame_onlyNullEntityProperties_shouldBeEqual() throws Exception {
        //given
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("Meier");
        parent.setId(24L);

        Parent equalParent = new Parent(parent);
        //when
        boolean equal = fullEqualCompare(parent, equalParent);
        //then
        Assertions.assertTrue(equal);
    }

    @Test
    void allValuesSame_exceptOne_shouldNotBeEqual() throws Exception {
        //given
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("Meier");
        parent.setId(24L);

        Parent equalParent = new Parent(parent);
        equalParent.setName(parent.getName() + "MOD");
        //when
        RapidEqualsBuilder.MinimalDiff diff = fullEqualCompareWithDiff(parent, equalParent);
        //then
        Assertions.assertEquals(
                MethodNameUtil.propertyNameOf(parent::getName),
                diff.getProperty()
        );
    }

    @Test
    void allValuesSame_exceptEntity_thatIsOnlyEqualById_shouldBeEqual() throws Exception {
        Child child = new Child();
        child.setId(42L);
        child.setName("child");
        //given
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("Meier");
        parent.setId(24L);

        Parent equalParent = new Parent(parent);
        parent.setChild(child);
        Child onlyEqualById = new Child();
        onlyEqualById.setId(child.getId());
        equalParent.setChild(onlyEqualById);

        //when
        boolean equal = fullEqualCompare(parent, equalParent);
        //then
        Assertions.assertTrue(equal);
    }

    @Test
    void allSame_exceptIgnored_shouldBeEqual() {
        Child child = new Child();
        child.setId(42L);
        child.setName("meier");

        Parent parent = new Parent();
        parent.setName("not meier");
        parent.setId(42L);

        RapidEqualsBuilder.MinimalDiff diff = compare(child).with(parent)
                .properties()
                .all()
                .ignore(child::getName)
                .ignore(child::getAddress)
                .ignore(child::getParent)
                //only id remains
                .assertEqual()
                .getDiff();

        Assertions.assertFalse(diff.isDifferent());

    }

    @Test
    void allSame_exceptIgnored_andNullFields_butIgnoringNull_shouldBeEqual() {
        Child child = new Child();
        child.setId(42L);
        child.setName("meier");

        Parent parent = new Parent();
        parent.setName("not meier");
        parent.setId(42L);

        RapidEqualsBuilder.MinimalDiff diff = compare(child).with(parent)
                .ignoreNull(true)
                .properties()
                .all()
                .ignore(child::getName)
                //automatically ignored. bc null valued fields are ignored
//                .ignore(child::getAddress)
//                .ignore(child::getParent)
                //only id remains
                .assertEqual()
                .getDiff();

        Assertions.assertTrue(diff.isEmpty());

    }

    @Test
    void allSame_butNullFieldsDiffer_notIgnoringNull_shouldNotBeEqual() {
        Child child = new Child();
        child.setId(42L);
        child.setName(null);

        Parent parent = new Parent();
        parent.setName("same");
        parent.setId(42L);

        RapidEqualsBuilder.MinimalDiff diff = compare(child).with(parent)
                .properties()
                .all()
//                .ignore(child::getName)
                .ignore(child::getAddress)
                .ignore(child::getParent)
                //only id and name remains, name on root side is null + null is not ignored -> not equal
                .assertNotEqual()
                .getDiff();

        Assertions.assertEquals(
                MethodNameUtil.propertyNameOf(child::getName),
                diff.getProperty()
        );
    }

    @Test
    void allSame_butRootHasFieldThatCompareDoesNotHave_shouldThrowException() {
        Child child = new Child();
        child.setName("same");
        child.setId(42L);
        child.setAddress("i have more fields");

        Parent parent = new Parent();
        parent.setName("same");
        parent.setId(42L);

        Assertions.assertThrows(PropertyNotFoundException.class, () -> {
            compare(child).with(parent)
                    .properties()
                    .all()
                    .ignore(child::getParent)
                    .assertNotEqual()
                    .getDiff();
        });
    }

    @Test
    void allSame_butRootHasFieldThatCompareDoesNotHave_withValue_useNullForNotFoundEnabled_shouldNotBeEqual() {
        Child child = new Child();
        child.setName("same");
        child.setId(42L);
        child.setAddress("i have more fields");

        Parent parent = new Parent();
        parent.setName("same");
        parent.setId(42L);


        ResultProvider resultProvider = compare(child).with(parent)
                .configureFullCompare(config -> config.setUseNullForNotFound(true))
                .properties()
                .all()
                .ignore(child::getParent)
                .assertNotEqual();

        Assertions.assertFalse(resultProvider.getDiff().isEmpty());
        Assertions.assertFalse(resultProvider.isEqual());
        Assertions.assertEquals(MethodNameUtil.propertyNameOf(child::getAddress),resultProvider.getDiff().getProperty());
    }

    @Test
    void allSame_butRootHasFieldThatCompareDoesNotHave_withoutValue_useNullForNotFoundEnabled_shouldBeEqual() {
        Child child = new Child();
        child.setName("same");
        child.setId(42L);
//        child.setAddress(null);

        Parent parent = new Parent();
        parent.setName("same");
        parent.setId(42L);



        boolean equal = compare(child).with(parent)
                .configureFullCompare((config) -> config.setUseNullForNotFound(true))
                .properties()
                .all()
                .ignore(child::getParent)
                .assertEqual()
                .isEqual();

        Assertions.assertTrue(equal);
    }

    public boolean fullEqualCompare(Object root, Object compare) {
        return compare(root)
                .with(compare)
                .properties()
                .all()
                .go()
                .isEqual();
    }

    public RapidEqualsBuilder.MinimalDiff fullEqualCompareWithDiff(Object root, Object compare) {
        return compare(root)
                .with(compare)
                .properties()
                .all()
                .go()
                .getDiff();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    class Parent extends IdentifiableEntityImpl<Long> {
        String name;
        int age;
        Child child;
        Set<Child> childSet;


        public Parent(Parent copy) {
            this(copy.name, copy.age, copy.child, copy.childSet);
            setId(copy.getId());
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    class Child extends IdentifiableEntityImpl<Long> {
        String name;
        String address;
        Parent parent;
    }
}
