package io.github.vincemann.ezcompare;

import io.github.vincemann.ezcompare.configurer.ResultConfigurer;
import io.github.vincemann.ezcompare.domain.IdentifiableEntityImpl;
import io.github.vincemann.ezcompare.util.MethodNameUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.github.vincemann.ezcompare.Comparison.compare;


class CompareTemplateFullCompareTest {


    @BeforeEach
    void setUp() {
        Comparison.globalReset();
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
        RapidEqualsBuilder.Diff diff = fullEqualCompareWithDiff(parent, equalParent);
        //then
        Assertions.assertEquals(
                MethodNameUtil.propertyNameOf(parent::getName),
                diff.getFirstNode().getProperty()
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

        RapidEqualsBuilder.Diff diff = compare(child).with(parent)
                .properties()
                .all()
                .ignore(child::getName)
                .ignore(child::getAddress)
                .ignore(child::getParent)
                .operation()
                //only id remains
                .assertEqual()
                .result()
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

        RapidEqualsBuilder.Diff diff = compare(child).with(parent)
                .options()
                .ignoreNull(true)
                .properties()
                .all()
                .ignore(child::getName)
                //automatically ignored. bc null valued fields are ignored
//                .ignore(child::getAddress)
//                .ignore(child::getParent)
                //only id remains
                .operation()
                .assertEqual()
                .result()
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

        RapidEqualsBuilder.Diff diff = compare(child).with(parent)
                .properties()
                .all()
//                .ignore(child::getName)
                .ignore(child::getAddress)
                .ignore(child::getParent)
                .operation()
                //only id and name remains, name on root side is null + null is not ignored -> not equal
                .assertNotEqual()
                .result()
                .getDiff();

        Assertions.assertEquals(
                MethodNameUtil.propertyNameOf(child::getName),
                diff.getFirstNode().getProperty()
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
                    .operation()
                    .assertNotEqual()
                    .result()
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


        ResultConfigurer resultConfigurer = compare(child).with(parent)
                .options()
                .configureFullCompare(config -> config.setUseNullForNotFound(true))
                .properties()
                .all()
                .ignore(child::getParent)
                .operation()
                .assertNotEqual()
                .result();

        Assertions.assertTrue(resultConfigurer.getDiff().isDifferent());
        Assertions.assertFalse(resultConfigurer.isEqual());
        Assertions.assertEquals(
                MethodNameUtil.propertyNameOf(child::getAddress),
                resultConfigurer.getDiff().getFirstNode().getProperty()
        );
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
                .options()
                .configureFullCompare((config) -> config.setUseNullForNotFound(true))
                .properties()
                .all()
                .ignore(child::getParent)
                .operation()
                .assertEqual()
                .result()
                .isEqual();

        Assertions.assertTrue(equal);
    }

    public boolean fullEqualCompare(Object root, Object compare) {
        return compare(root)
                .with(compare)
                .properties()
                .all()
                .operation()
                .go()
                .result()
                .isEqual();
    }

    public RapidEqualsBuilder.Diff fullEqualCompareWithDiff(Object root, Object compare) {
        return compare(root)
                .with(compare)
                .properties()
                .all()
                .operation()
                .go()
                .result()
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
