package com.github.vincemann.ezcompare;

import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.ezcompare.util.MethodNameUtil.propertyNameOf;

public class ShowCaseTests {

    @Builder
    @Getter
    static class Person{
        private String name;
        private String address;
        private Long tel;
        private Integer age;
        private String creatorIp;

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @Builder
    @Getter
    static class PersonDTO{
        private String name;
        private String address;
        private Long tel;
        private Integer age;

        @Override
        public String toString() {
            return "PersonDTO{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @Test
    public void fullCompareEasy(){
        Person p = Person.builder()
                .name("same")
                .address("same")
                .age(42)
                .tel(42L)
                .creatorIp("8.8.8.8")
                .build();

        PersonDTO dto = PersonDTO.builder()
                .age(42)
                .tel(42L)
                .address("same")
                .name("same")
                .build();

        // normal way
        Assertions.assertEquals(dto.age,p.age);
        Assertions.assertEquals(dto.address,p.address);
        Assertions.assertEquals(dto.tel,p.tel);
        Assertions.assertEquals(dto.name,p.name);

        //ez compare way
        compare(dto).with(p)
                //by default uses only properties of left side object
                .properties()
                .all()
                .assertEqual();

        //normal way
        Assertions.assertEquals(p.age,dto.age);
        Assertions.assertEquals(p.address,dto.address);
        Assertions.assertEquals(p.tel,dto.tel);
        Assertions.assertEquals(p.name,dto.name);

        //ez compare way
        compare(p).with(dto)
                .properties()
                .all()
                .ignore(p::getCreatorIp)
                .assertEqual();
    }

    @Test
    public void fullCompareEasy_ignoreNull() {
        Person p = Person.builder()
                .name("same")
                .address("same")
                .age(42)
                .tel(42L)
                .creatorIp("8.8.8.8")
                .build();

        PersonDTO dto = PersonDTO.builder()
                .age(42)
                .tel(42L)
                //address and name is irrelevant, all other properties must be equal
                .address(null)
                .name(null)
                .build();


        //normal way
        Assertions.assertEquals(p.age,dto.age);
        Assertions.assertEquals(p.tel,dto.tel);


        //ez compare way
        compare(dto).with(p)
                .ignoreNull(true)
                .properties()
                .all()
                .assertEqual();
    }

    @Test
    public void fullCompareEasy_getDiff(){
        Person p = Person.builder()
                .name("unimportant")
                .address("veryDiff")
                .age(42)
                .tel(null)
                .creatorIp("unimportant")
                .build();

        PersonDTO dto = PersonDTO.builder()
                .age(42)
                .tel(42L)
                //address and name is irrelevant, all other properties must be equal
                .address("diff")
                .name(null)
                .build();

        //only properties that are not null and present in both compare objects are relevant
        RapidEqualsBuilder.Diff diff = compare(p).with(dto)
                .ignoreNotFound(true)       //creatorIp will be ignored
                .ignoreNull(true)           //tel       will be ignored
                .properties()
                .all()
                //we dont care about name neither
                .ignore(p::getName)
                .assertNotEqual()
                .getDiff();

        //just to showcase it worked the way it is expected
        Assertions.assertEquals(1,diff.getDiffNodes().size());
        Assertions.assertEquals(propertyNameOf(p::getAddress),diff.getFirstNode().getProperty());
    }

    @Test
    public void fullCompareEasy_globalConfig(){
        Person p = Person.builder()
                .name("always ignored")
                .creatorIp("always ignored")
                .address("same")
                .age(42)
                .build();

        PersonDTO dto = PersonDTO.builder()
                .name("am I really ignored?")
                .age(42)
                .address("same")
                .build();

        //i.E. Name is always diff in database compared to dto + ip irrelevant in dev env
        // -> creating Global Dev Compare Config
        //this could be initialized once in an abstract test ect. and would survive between tests
        Comparator.getFullCompareGlobalConfig()
                .modify()
                .ignoreProperty(p::getCreatorIp)
                .ignoreProperty("name")
                .ignoreNotFound(true)
                .ignoreNull(true);



        //only properties that are not null and present in both compare objects are relevant
        compare(p).with(dto)
                .properties()
                .all()
                .assertEqual();
    }

    @Test
    public void partialCompareEasy(){
        Person p = Person.builder()
                .name("diff")
                .address("diff")
                .age(42)
                .tel(42L)
                .creatorIp("8.8.8.8")
                .build();

        PersonDTO dto = PersonDTO.builder()
                .age(42)
                .tel(42L)
                .address("veryDiff")
                .name("veryDiff")
                .build();

        //normal way
        Assertions.assertNotEquals(p.name,dto.name);
        Assertions.assertNotEquals(p.address,dto.address);
        Assertions.assertEquals(p.age,dto.age);
        Assertions.assertEquals(p.tel,dto.tel);

        //ez compare way
        compare(p).with(dto)
                .properties()
                .include(p::getName,
                         p::getAddress)
                .assertNotEqual()
                .and()
                .properties()
                .include(p::getAge,
                         p::getTel)
                .assertEqual();
    }
}
