package io.github.vincemann.ezcompare;

import com.github.hervian.reflection.Types;
import io.github.vincemann.ezcompare.domain.IdentifiableEntityImpl;
import io.github.vincemann.ezcompare.template.CompareTemplate;
import lombok.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.github.vincemann.ezcompare.template.CompareTemplate.compare;


class CompareTemplatePartialCompareTest {



    @Getter
    @Setter
    @AllArgsConstructor
    class Parent  extends IdentifiableEntityImpl<Long> {

        String name;
        int age;
        CompareTemplateFullCompareTest.Child child;
        Set<CompareTemplateFullCompareTest.Child> childSet;

        public Parent() {
        }

        public Parent(CompareTemplateFullCompareTest.Parent copy){
            this(copy.name,copy.age,copy.child,copy.childSet);
            setId(copy.getId());
        }
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    class NoEntity{
        private String name;
        private int age;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    class NoEntityTwo{
        private String name;
        private int age;
        private String description;
    }



    @Getter
    @Setter
    class Child extends IdentifiableEntityImpl<Long>{
        String name;
        String address;
        CompareTemplateFullCompareTest.Parent parent;

        public Child() {
        }
    }

    @BeforeEach
    void setUp() {
        //no global config that could interfere with test
        Assertions.assertNull(CompareTemplate.GLOBAL_PARTIAL_COMPARE_CONFIG);
    }

    @AfterEach
    void tearDown() {
        CompareTemplate.GLOBAL_PARTIAL_COMPARE_CONFIG=null;
    }

    @Test
    public void entity_onlyCheckedPropertyEqual_onlyCheckThis_shouldBeEqual_sameType(){
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("meier");

        Parent second = new Parent();
        second.setAge(42);
        second.setName("junker");

        boolean equal = compareSingleProperty(parent,second,parent::getAge);

        Assertions.assertTrue(equal);
    }

    @Test
    public void entity_selectedPropertiesNotEqual_sameType_shouldBeConsideredNotEqual(){
        Parent parent = new Parent();
        parent.setAge(43);
        parent.setName("meier");

        Parent second = new Parent();
        second.setAge(42);
        second.setName("meier");

        boolean equal = compareSingleProperty(parent,second,parent::getAge);

        Assertions.assertFalse(equal);
    }

    @Test
    public void entity_selectedPropertiesEqual_diffType_shouldBeEqual(){
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("meier");

        Child child = new Child();
        child.setName("meier");

        boolean equal = compareSingleProperty(parent,child,parent::getName);
        Assertions.assertTrue(equal);
    }

    @Test
    public void entity_selectedPropertiesNotEqual_diffType_shouldBeConsideredNotEqual(){
        Parent parent = new Parent();
        //parent.setId(1L);
        parent.setAge(42);
        parent.setName("meierDiff");

        Child child = new Child();
        //child.setId(2L);
        child.setName("meier");

        boolean equal = compareSingleProperty(parent,child,parent::getName);


        Assertions.assertFalse(equal);
    }

    @Test
    public void entity_selectedPropertyNotEqual_idsEqual_diffType_shouldBeConsideredNotEqual(){
        Parent parent = new Parent();
        parent.setId(1L);
        parent.setAge(42);
        parent.setName("meierDiff");

        Child child = new Child();
        child.setId(1L);
        child.setName("meier");

        boolean equal = compareSingleProperty(parent,child,parent::getName);

        Assertions.assertFalse(equal);
    }

    @Test
    public void selectedPropertiesNotEqual_diffType_shouldBeConsideredNotEqual(){
        NoEntity parent = new NoEntity();
        parent.setAge(42);
        parent.setName("meierDiff");

        NoEntityTwo child = new NoEntityTwo();
        child.setName("meier");
        child.setAge(42);

        boolean equal = compareSingleProperty(parent,child,parent::getName);

        Assertions.assertFalse(equal);
    }

    @Test
    public void selectedPropertiesNotEqual_sameType_shouldBeConsideredNotEqual(){
        NoEntity parent = new NoEntity();
        parent.setAge(42);
        parent.setName("meierDiff");

        NoEntity child = new NoEntity();
        child.setName("meier");
        child.setAge(42);

        boolean equal = compareSingleProperty(parent,child,parent::getAge);


        Assertions.assertTrue(equal);
    }

    @Test
    public void selectedPropertiesEqual_diffType_shouldBeEqual(){
        NoEntity parent = new NoEntity();
        parent.setAge(42);
        parent.setName("meierDiff");

        NoEntityTwo child = new NoEntityTwo();
        child.setName("meier");
        child.setAge(42);

        boolean equal = compareSingleProperty(parent,child,parent::getAge);

        Assertions.assertTrue(equal);
    }



    @Test
    public void onlyCheckedProperty_isNotEqual_shouldBeNotEqual(){
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("meier");

        Parent second = new Parent();
        second.setAge(42);
        second.setName("junker");

        boolean equal = compareSingleProperty(parent,second,parent::getName);

        Assertions.assertFalse(equal);
    }

    @Test
    public void onlyCheckedProperty_isNullForBoth_shouldBeEqual(){
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName(null);

        Parent second = new Parent();
        second.setAge(42);
        second.setName(null);

        boolean equal = compareSingleProperty(parent, second, parent::getName);

        Assertions.assertTrue(equal);
    }

    @Test
    public void onlyCheckedProperty_oneIsNull_shouldBeNotEqual(){
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName(null);

        Parent second = new Parent();
        second.setAge(42);
        second.setName("not null");

        boolean equal = compareSingleProperty(parent, second, parent::getName);

        Assertions.assertFalse(equal);
    }
    
    

    private boolean compareSingleProperty(Object root, Object compare, Types.Supplier<?> getter){
        return compare(root)
                .with(compare)
                .properties()
                .include(getter)
                .go()
                .isEqual();
    }
}

