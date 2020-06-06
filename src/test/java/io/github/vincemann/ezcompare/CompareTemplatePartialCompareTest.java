package io.github.vincemann.ezcompare;

import com.github.hervian.reflection.Types;
import io.github.vincemann.ezcompare.domain.IdentifiableEntityImpl;
import lombok.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.github.vincemann.ezcompare.Comparison.compare;


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
        Comparison.globalReset();
    }


    @Test
    public void onlyCheckedPropertyEqual_onlyCheckThis_shouldBeEqual(){
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("diff");

        Parent second = new Parent();
        second.setAge(42);
        second.setName("veryDiff");

        boolean equal = compareSingleProperty(parent,second,parent::getAge);

        Assertions.assertTrue(equal);
    }

    @Test
    public void onlyCheckedPropertiesEqual_onlyCheckThis_shouldBeEqual(){
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("same");

        Parent second = new Parent();
        second.setAge(42);
        second.setName("same");

        boolean equal = compareSingleProperty(parent,second,parent::getAge);

        Assertions.assertTrue(equal);
    }
    
    

    @Test
    public void selectedProperties_notEqual_shouldBeConsideredNotEqual(){
        Parent parent = new Parent();
        parent.setAge(69);
        parent.setName("same");

        Parent second = new Parent();
        second.setAge(42);
        second.setName("same");

        boolean equal = compareSingleProperty(parent,second,parent::getAge);

        Assertions.assertFalse(equal);
    }

    @Test
    public void selectedProperties_Equal_diffType_shouldBeEqual(){
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("same");

        Child child = new Child();
        child.setName("same");

        boolean equal = compareSingleProperty(parent,child,parent::getName);
        Assertions.assertTrue(equal);
    }

    @Test
    public void selectedProperties_NotEqual_diffType_shouldBeConsideredNotEqual(){
        Parent parent = new Parent();
        //parent.setId(1L);
        parent.setAge(42);
        parent.setName("diff");

        Child child = new Child();
        //child.setId(2L);
        child.setName("veryDiff");

        boolean equal = compareSingleProperty(parent,child,parent::getName);


        Assertions.assertFalse(equal);
    }

    @Test
    public void selectedPropertyNotEqual_idsEqual_diffType_shouldBeConsideredNotEqual(){
        Parent parent = new Parent();
        parent.setId(42L);
        parent.setAge(69);
        parent.setName("diff");

        Child child = new Child();
        child.setId(42L);
        child.setName("veryDiff");

        boolean equal = compareSingleProperty(parent,child,parent::getName);

        Assertions.assertFalse(equal);
    }

    @Test
    public void selectedPropertiesNotEqual_diffType_shouldBeConsideredNotEqual(){
        NoEntity parent = new NoEntity();
        parent.setAge(42);
        parent.setName("diff");

        NoEntityTwo child = new NoEntityTwo();
        child.setName("veryDiff");
        child.setAge(42);

        boolean equal = compareSingleProperty(parent,child,parent::getName);

        Assertions.assertFalse(equal);
    }

    @Test
    public void selectedPropertiesNotEqual_sameType_shouldBeConsideredNotEqual(){
        NoEntity parent = new NoEntity();
        parent.setAge(42);
        parent.setName("diff");

        NoEntity child = new NoEntity();
        child.setName("veryDiff");
        child.setAge(42);

        boolean equal = compareSingleProperty(parent,child,parent::getAge);


        Assertions.assertTrue(equal);
    }

    @Test
    public void selectedPropertiesEqual_diffType_shouldBeEqual(){
        NoEntity parent = new NoEntity();
        parent.setAge(42);
        parent.setName("diff");

        NoEntityTwo child = new NoEntityTwo();
        child.setName("veryDiff");
        child.setAge(42);

        boolean equal = compareSingleProperty(parent,child,parent::getAge);

        Assertions.assertTrue(equal);
    }



    @Test
    public void onlyCheckedProperty_isNotEqual_shouldBeNotEqual(){
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("diff");

        Parent second = new Parent();
        second.setAge(42);
        second.setName("veryDiff");

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
                .operation()
                .go()
                .result()
                .isEqual();
    }
}

