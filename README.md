# Ez-Compare
Simple refelction-based **Comparisson-Tool** for comparing two Objects: "Root" & "Compare".  
Highly **configurable**, yet very **simple** to use.  
Human readable **fluent-api-style**.  
Does **not** perform **deep** compare (except for arrays),  
but instead iterates over the **selected** instance variables of both objects and compares them via equal-method.  
-> reflection depth is 1.  
  
## Features  
* Can Compare Objects of **different Types**  
* Only uses Root's Properties/ Fields for Comparisson  
* Can ignore missing Fields of Compare  
* Evaluates and **returns Differences**  
* Can be configured to stop after finding first Difference (**Performence-Mode**)  
* **Full-Compare**-Mode (use all Properties of Root and ignore some if needed)  
* **Partial-Compare**-Mode (explicitly include Properties of Root and only compare theses)  
* **Globally** & Locally configure **Compare-Options**  
* ... see Java Docs for more...  

## Include  
### Maven  
```code
<repositories>  
    <repository>  
        <id>jitpack.io</id>  
        <url>https://jitpack.io</url>  
    </repository>  
</repositories>  
  
<dependency>  
    <groupId>com.github.vincemann</groupId>  
    <artifactId>ez-compare</artifactId>  
    <version>1.1.0</version>  
</dependency>  
```  
 
### Gradle  
   
```code
repositories {  
    jcenter()  
    maven { url "https://jitpack.io" }  
}  
dependencies {  
     implementation 'com.github.vincemann:ez-compare:1.1.0'  
}  
```
 
## Simple Example    
  
  ```java
    @Test
    public void fullCompareEasy(){
        // using default config aka strict mode 
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
                //by default uses only properties of left side (=root) object
                .properties()
                .all()
                .assertEqual();
    }
  ```
  
## Advanced Example  
  
```java
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

```
  
### Check out more showcases in src/test/ShowCaseTests   
