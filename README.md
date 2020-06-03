# ez-compare
Simple refelction-based fluent-api-style Comparisson-Tool for comparing two Objects: "Root" & "Compare".  
Highly configurable, yet very simple to use.  
Human readable fluent-api-style.  
Ships preconfigured in strict Mode.  
  
## features  
* Can Compare Objects of different Types  
* Only uses Root's Properties/ Fields for Comparisson  
* Can ignore missing Fields of Compare  
* Evaluates and returns Differences  
* Can be configured to stop after finding first Difference (Performence-Mode)  
* Full-Compare-Mode (use all Properties of Root and ignore some if needed)  
* Partial-Compare-Mode (explicitly include Properties of Root and only compare theses)  
* Globally & Locally configure Compare-Options  
* ... see Java Docs for more...  
  
## simple example    
  
  ```java
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
                //by default uses only properties of left side (=root) object
                .properties()
                .all()
                .assertEqual();
    }
  ```
  
## advanced example  
  
``java
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
        Comparison.modFullCompareGlobalConfig()
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

```
  
## view more show cases in src/test/ShowCaseTests   
