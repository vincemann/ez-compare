//package com.github.vincemann.ezcompare;
//
//import com.github.vincemann.ezcompare.domain.IdentifiableEntityImpl;
//import com.google.common.collect.Sets;
//import lombok.Builder;
//import lombok.Getter;
//import org.junit.jupiter.api.Test;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import static com.github.vincemann.ezcompare.Comparator.compare;
//
//public class ConverterShowCaseTests {
//
//    @Builder
//    @Getter
//    static class Person extends IdentifiableEntityImpl<Long> {
//        private String name;
//        private String address;
//        private Long tel;
//        private Integer age;
//        private String creatorIp;
//
//        @Override
//        public String toString() {
//            return "Person{" +
//                    "name='" + name + '\'' +
//                    '}';
//        }
//    }
//
//    @Builder
//    @Getter
//    static class PersonDTO extends IdentifiableEntityImpl<Long>{
//        private String name;
//        private String address;
//        private Long tel;
//        private Integer age;
//
//        @Override
//        public String toString() {
//            return "PersonDTO{" +
//                    "name='" + name + '\'' +
//                    '}';
//        }
//    }
//
//    @Builder
//    @Getter
//    static class Company extends IdentifiableEntityImpl<Long>{
//        private String name;
//
//        @Override
//        public String toString() {
//            return "Company{" +
//                    "name='" + name + '\'' +
//                    '}';
//        }
//    }
//
//
//
//    @Builder
//    @Getter
//    static class School extends IdentifiableEntityImpl<Long> {
//        private String name;
//        private Set<Person> persons = new HashSet<>();
//        private Company company;
//
//        @Override
//        public String toString() {
//            return "School{" +
//                    "name='" + name + '\'' +
//                    ", persons=" + persons +
//                    ", company=" + company +
//                    '}';
//        }
//    }
//
//    @Builder
//    @Getter
//    static class SchoolDto extends IdentifiableEntityImpl<Long> {
//        private String name;
//        private Set<Long> personIds = new HashSet<>();
//        private Long companyId;
//
//        @Override
//        public String toString() {
//            return "SchoolDto{" +
//                    "name='" + name + '\'' +
//                    ", personIds=" + personIds +
//                    ", companyId=" + companyId +
//                    '}';
//        }
//    }
//
//
//    @Test
//    public void testIdConverter_fullUpdate(){
//        Long personId = 42L;
//        Long person2Id = 49L;
//
//        Person p1 = Person.builder()
//                .name("max1")
//                .address("adr1")
//                .age(13)
//                .build();
//        p1.setId(personId);
//
//        Person p2 = Person.builder()
//                .name("max1")
//                .address("adr1")
//                .age(13)
//                .build();
//        p2.setId(person2Id);
//
////        PersonDTO dto = PersonDTO.builder()
////                .age(42)
////                .tel(42L)
////                .address("same")
////                .name("same")
////                .build();
////        dto.setId(personId);
//
//        School school = School.builder()
//                .name("ovgu")
//                .persons(Sets.newHashSet(p1,p2))
//                .build();
//
//        SchoolDto schoolDto = SchoolDto.builder()
//                .name("ovgu")
//                .personIds(Sets.newHashSet(personId, person2Id))
//                .build();
//
//        compare(school).with(schoolDto)
//                .useConverter(new IdConverter())
//                .
//
//    }
//
//}
