# URL Lookup Service
This service maintains database of Malware URLs. HTTP proxy communicates with this service to check if the resource being requested is known to contain malware.

## Assumptions
1. This service is simple implementation for validating malicious URL's with malware database that we have. Though I have provided bare minimum implementation to update the malware database using POST method, it is only for testing purpose. I assumed that there will be different service which updates malware database.
2. The URL is malicious if combination of hostname and port (represented as domain in implementation) is present in malware database. Original resource path and query string are not used at **service** layer to determine if the input URL is valid OR not.   
## Implementation
### Technologies
* Spring Boot - It is open source Java based framework to create micro services.
* Redis - Since we are dealing with time sensitive service which is helping HTTP proxy to determine malicious URL's, in memory data store like redis is important to minimize the latency. Redis being key-value (NoSQL) database can scale well for future improvements to service using Replication (master-slave) and Clustering solution 
* Jedis - It is a Redis client for Java.
* JUnit, Mockito and Hamcrest - Unit and Integration testing.
* Lombok - It is Java library to build getter, setter and constructors using annotations.
* Slf4j - Application logging.
* Maven - Dependency management and build tool for Java project.
### Approach
Spring being MVC framework, I have divided source structure into 3 layers.

It can be visualized as follows
```
.
└── com
    └── cisco
        └── exercise
            ├── Main.java
            ├── config
            │   └── RedisConfiguration.java
            ├── domain
            │   ├── entity
            │   ├── exception
            │   └── repository
            ├── service
            │   └── MalwareURLService.java
            └── web
                └── MalwareURLController.java
``` 
* **Web** - This layer has REST controller for application. As I understand from the given GET API endpoint, `/urlinfo/1` represents API version. There are different methods used for REST api versioning. I am using URI path versioning. This is still extensible because version mapping is used at method level and not class. Please refer to [MalwareURLController.java](src/main/java/com/cisco/exercise/web/MalwareURLController.java).
* **Service** - This layer has business logic to validate if the input URL is malicious or not. Currently the combination of hostname and port i.e. domain is used to determine the validity of URL even if there are changes with respect to original path and query parameters. We should be able to modify this logic to include other properties of URL. Please refer to [MalwareURLService.java](src/main/java/com/cisco/exercise/service/MalwareURLService.java).
* **Data (Domain)** - This includes data access layer for Spring framework to interact with Redis. It includes Entity and respective Repository. Please refer to [MalwareURL.java](src/main/java/com/cisco/exercise/domain/entity/MalwareURL.java) and [MalwareURLRepository.java](src/main/java/com/cisco/exercise/domain/repository/MalwareURLRepository.java). This also includes custom exception to throw in case we find malicious URL in our database.

### API Endpoints

```
GET /urlinfo/1/{hostname_and_port}/{original_path_and_query_string}
Response
200 - If the requested domain is not present in the malware database
403 - If the requested domain is present in the malware database. Reason can also be included in response body if available in database.

```
Below endpoint can be used to add data to malware database. This is only for testing purpose.
```
POST /urlinfo/1/{hostname_and_port}/{original_path_and_query_string}
```
For both the endpoints I have considered following things

* hostname_and_port - This path variable is mandatory. Port can be added using : as delimiter.
e.g. www.malware.com OR www.malware.com:8443
* original_path_and_query_string - Original resource path and query string is optional.

### Testing
I have written unit test case for service and integration test for Controller. Please refer to  [MalwareURLServiceTest.java](src/test/java/com/cisco/exercise/service/MalwareURLServiceTest.java) and [MalwareURLControllerIntegrationTest.java](src/test/java/com/cisco/exercise/web/MalwareURLControllerIntegrationTest.java) 
<details><summary>Test Output</summary>
<p>

```java
➜ urllookup git:(master) ✗ mvn test
[INFO] Scanning for projects...
[INFO]
[INFO] -------------------< com.cisco.exercise:url-lookup >--------------------
[INFO] Building url-lookup 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- maven-resources-plugin:3.1.0:resources (default-resources) @ url-lookup ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 1 resource
[INFO] Copying 0 resource
[INFO]
[INFO] --- maven-compiler-plugin:3.8.0:compile (default-compile) @ url-lookup ---
[INFO] Nothing to compile - all classes are up to date
[INFO]
[INFO] --- maven-resources-plugin:3.1.0:testResources (default-testResources) @ url-lookup ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 1 resource
[INFO]
[INFO] --- maven-compiler-plugin:3.8.0:testCompile (default-testCompile) @ url-lookup ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 3 source files to /Users/mayurbarge/urllookup/target/test-classes
[INFO]
[INFO] --- maven-surefire-plugin:2.22.1:test (default-test) @ url-lookup ---
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.cisco.exercise.web.MalwareURLControllerIntegrationTest
00:35:37.112 [main] DEBUG org.springframework.test.context.junit4.SpringJUnit4ClassRunner - SpringJUnit4ClassRunner constructor called with [class com.cisco.exercise.web.MalwareURLControllerIntegrationTest]
00:35:37.116 [main] DEBUG org.springframework.test.context.BootstrapUtils - Instantiating CacheAwareContextLoaderDelegate from class [org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate]
00:35:37.122 [main] DEBUG org.springframework.test.context.BootstrapUtils - Instantiating BootstrapContext using constructor [public org.springframework.test.context.support.DefaultBootstrapContext(java.lang.Class,org.springframework.test.context.CacheAwareContextLoaderDelegate)]
00:35:37.144 [main] DEBUG org.springframework.test.context.BootstrapUtils - Instantiating TestContextBootstrapper for test class [com.cisco.exercise.web.MalwareURLControllerIntegrationTest] from class [org.springframework.boot.test.context.SpringBootTestContextBootstrapper]
00:35:37.154 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Neither @ContextConfiguration nor @ContextHierarchy found for test class [com.cisco.exercise.web.MalwareURLControllerIntegrationTest], using SpringBootContextLoader
00:35:37.157 [main] DEBUG org.springframework.test.context.support.AbstractContextLoader - Did not detect default resource location for test class [com.cisco.exercise.web.MalwareURLControllerIntegrationTest]: class path resource [com/cisco/exercise/web/MalwareURLControllerIntegrationTest-context.xml] does not exist
00:35:37.157 [main] DEBUG org.springframework.test.context.support.AbstractContextLoader - Did not detect default resource location for test class [com.cisco.exercise.web.MalwareURLControllerIntegrationTest]: class path resource [com/cisco/exercise/web/MalwareURLControllerIntegrationTestContext.groovy] does not exist
00:35:37.157 [main] INFO org.springframework.test.context.support.AbstractContextLoader - Could not detect default resource locations for test class [com.cisco.exercise.web.MalwareURLControllerIntegrationTest]: no resource found for suffixes {-context.xml, Context.groovy}.
00:35:37.205 [main] DEBUG org.springframework.test.context.support.ActiveProfilesUtils - Could not find an 'annotation declaring class' for annotation type [org.springframework.test.context.ActiveProfiles] and class [com.cisco.exercise.web.MalwareURLControllerIntegrationTest]
00:35:37.278 [main] DEBUG org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider - Identified candidate component class: file [/Users/mayurbarge/urllookup/target/classes/com/cisco/exercise/Main.class]
00:35:37.279 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Found @SpringBootConfiguration com.cisco.exercise.Main for test class com.cisco.exercise.web.MalwareURLControllerIntegrationTest
00:35:37.354 [main] DEBUG org.springframework.boot.test.context.SpringBootTestContextBootstrapper - @TestExecutionListeners is not present for class [com.cisco.exercise.web.MalwareURLControllerIntegrationTest]: using defaults.
00:35:37.355 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Loaded default TestExecutionListener class names from location [META-INF/spring.factories]: [org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener, org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener, org.springframework.boot.test.autoconfigure.restdocs.RestDocsTestExecutionListener, org.springframework.boot.test.autoconfigure.web.client.MockRestServiceServerResetTestExecutionListener, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrintOnlyOnFailureTestExecutionListener, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverTestExecutionListener, org.springframework.test.context.web.ServletTestExecutionListener, org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener, org.springframework.test.context.support.DependencyInjectionTestExecutionListener, org.springframework.test.context.support.DirtiesContextTestExecutionListener, org.springframework.test.context.transaction.TransactionalTestExecutionListener, org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener]
00:35:37.367 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Using TestExecutionListeners: [org.springframework.test.context.web.ServletTestExecutionListener@49e53c76, org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener@351d00c0, org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener@2a3b5b47, org.springframework.boot.test.autoconfigure.SpringBootDependencyInjectionTestExecutionListener@55b699ef, org.springframework.test.context.support.DirtiesContextTestExecutionListener@35d019a3, org.springframework.test.context.transaction.TransactionalTestExecutionListener@689604d9, org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener@18078bef, org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener@799f10e1, org.springframework.boot.test.autoconfigure.restdocs.RestDocsTestExecutionListener@4c371370, org.springframework.boot.test.autoconfigure.web.client.MockRestServiceServerResetTestExecutionListener@145f66e3, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrintOnlyOnFailureTestExecutionListener@3023df74, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverTestExecutionListener@313ac989]
00:35:37.369 [main] DEBUG org.springframework.test.annotation.ProfileValueUtils - Retrieved @ProfileValueSourceConfiguration [null] for test class [com.cisco.exercise.web.MalwareURLControllerIntegrationTest]
00:35:37.369 [main] DEBUG org.springframework.test.annotation.ProfileValueUtils - Retrieved ProfileValueSource type [class org.springframework.test.annotation.SystemProfileValueSource] for class [com.cisco.exercise.web.MalwareURLControllerIntegrationTest]
00:35:37.371 [main] DEBUG org.springframework.test.annotation.ProfileValueUtils - Retrieved @ProfileValueSourceConfiguration [null] for test class [com.cisco.exercise.web.MalwareURLControllerIntegrationTest]
00:35:37.371 [main] DEBUG org.springframework.test.annotation.ProfileValueUtils - Retrieved ProfileValueSource type [class org.springframework.test.annotation.SystemProfileValueSource] for class [com.cisco.exercise.web.MalwareURLControllerIntegrationTest]
00:35:37.371 [main] DEBUG org.springframework.test.annotation.ProfileValueUtils - Retrieved @ProfileValueSourceConfiguration [null] for test class [com.cisco.exercise.web.MalwareURLControllerIntegrationTest]
00:35:37.371 [main] DEBUG org.springframework.test.annotation.ProfileValueUtils - Retrieved ProfileValueSource type [class org.springframework.test.annotation.SystemProfileValueSource] for class [com.cisco.exercise.web.MalwareURLControllerIntegrationTest]
00:35:37.376 [main] DEBUG org.springframework.test.context.support.AbstractDirtiesContextTestExecutionListener - Before test class: context [DefaultTestContext@65fb9ffc testClass = MalwareURLControllerIntegrationTest, testInstance = [null], testMethod = [null], testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@3e694b3f testClass = MalwareURLControllerIntegrationTest, locations = '{}', classes = '{class com.cisco.exercise.Main, class com.cisco.exercise.config.TestRedisConfiguration, class com.cisco.exercise.config.TestRedisConfiguration}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[[ImportsContextCustomizer@1bb5a082 key = [org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcSecurityAutoConfiguration, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcWebClientAutoConfiguration, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcWebDriverAutoConfiguration]], org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@569cfc36, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@64f6106c, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@18bf3d14, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@e7e8512, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@67b467e9], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true]], class annotated with @DirtiesContext [false] with mode [null].
00:35:37.377 [main] DEBUG org.springframework.test.annotation.ProfileValueUtils - Retrieved @ProfileValueSourceConfiguration [null] for test class [com.cisco.exercise.web.MalwareURLControllerIntegrationTest]
00:35:37.377 [main] DEBUG org.springframework.test.annotation.ProfileValueUtils - Retrieved ProfileValueSource type [class org.springframework.test.annotation.SystemProfileValueSource] for class [com.cisco.exercise.web.MalwareURLControllerIntegrationTest]
00:35:37.393 [main] DEBUG org.springframework.test.context.support.TestPropertySourceUtils - Adding inlined properties to environment: {spring.jmx.enabled=false, org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true, server.port=-1}

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.1.3.RELEASE)

2020-04-07 00:35:37.610  INFO 12701 --- [           main] .e.w.MalwareURLControllerIntegrationTest : Starting MalwareURLControllerIntegrationTest on Mayurs-MacBook-Pro.local with PID 12701 (started by mayurbarge in /Users/mayurbarge/urllookup)
2020-04-07 00:35:37.611  INFO 12701 --- [           main] .e.w.MalwareURLControllerIntegrationTest : No active profile set, falling back to default profiles: default
2020-04-07 00:35:37.935  INFO 12701 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode!
2020-04-07 00:35:37.935  INFO 12701 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data repositories in DEFAULT mode.
2020-04-07 00:35:37.952  INFO 12701 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 5ms. Found 0 repository interfaces.
2020-04-07 00:35:38.061  INFO 12701 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode!
2020-04-07 00:35:38.062  INFO 12701 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data repositories in DEFAULT mode.
2020-04-07 00:35:38.113  INFO 12701 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 51ms. Found 1 repository interfaces.
2020-04-07 00:35:39.237  INFO 12701 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
2020-04-07 00:35:39.501  INFO 12701 --- [           main] o.s.b.t.m.w.SpringBootMockServletContext : Initializing Spring TestDispatcherServlet ''
2020-04-07 00:35:39.502  INFO 12701 --- [           main] o.s.t.web.servlet.TestDispatcherServlet  : Initializing Servlet ''
2020-04-07 00:35:39.512  INFO 12701 --- [           main] o.s.t.web.servlet.TestDispatcherServlet  : Completed initialization in 10 ms
2020-04-07 00:35:39.535  INFO 12701 --- [           main] .e.w.MalwareURLControllerIntegrationTest : Started MalwareURLControllerIntegrationTest in 2.134 seconds (JVM running for 2.709)
2020-04-07 00:35:39.798  INFO 12701 --- [           main] c.c.exercise.web.MalwareURLController    : Domain: www.malware.com:8443 and Resource Path: bad
2020-04-07 00:35:39.805  INFO 12701 --- [           main] c.c.exercise.service.MalwareURLService   : Input URL with domain www.malware.com:8443 is present in database

MockHttpServletRequest:
      HTTP Method = GET
      Request URI = /urlinfo/1/www.malware.com:8443/bad
       Parameters = {}
          Headers = []
             Body = null
    Session Attrs = {}

Handler:
             Type = com.cisco.exercise.web.MalwareURLController
           Method = public org.springframework.http.ResponseEntity com.cisco.exercise.web.MalwareURLController.validateURL(java.lang.String,java.lang.String,org.springframework.util.MultiValueMap<java.lang.String, java.lang.String>)

Async:
    Async started = false
     Async result = null

Resolved Exception:
             Type = com.cisco.exercise.domain.exception.InvalidURLException

ModelAndView:
        View name = null
             View = null
            Model = null

FlashMap:
       Attributes = null

MockHttpServletResponse:
           Status = 403
    Error message = null
          Headers = []
     Content type = null
             Body =
    Forwarded URL = null
   Redirected URL = null
          Cookies = []
2020-04-07 00:35:39.846  INFO 12701 --- [           main] c.c.exercise.web.MalwareURLController    : Domain: www.google.com and Resource Path: null

MockHttpServletRequest:
      HTTP Method = GET
      Request URI = /urlinfo/1/www.google.com
       Parameters = {}
          Headers = []
             Body = null
    Session Attrs = {}

Handler:
             Type = com.cisco.exercise.web.MalwareURLController
           Method = public org.springframework.http.ResponseEntity com.cisco.exercise.web.MalwareURLController.validateURL(java.lang.String,java.lang.String,org.springframework.util.MultiValueMap<java.lang.String, java.lang.String>)

Async:
    Async started = false
     Async result = null

Resolved Exception:
             Type = null

ModelAndView:
        View name = null
             View = null
            Model = null

FlashMap:
       Attributes = null

MockHttpServletResponse:
           Status = 200
    Error message = null
          Headers = []
     Content type = null
             Body =
    Forwarded URL = null
   Redirected URL = null
          Cookies = []
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.844 s - in com.cisco.exercise.web.MalwareURLControllerIntegrationTest
[INFO] Running com.cisco.exercise.service.MalwareURLServiceTest
2020-04-07 00:35:40.117  INFO 12701 --- [           main] c.c.exercise.service.MalwareURLService   : Input URL with domain www.test.com:443 is present in database
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.228 s - in com.cisco.exercise.service.MalwareURLServiceTest
2020-04-07 00:35:40.125  INFO 12701 --- [       Thread-3] o.s.s.concurrent.ThreadPoolTaskExecutor  : Shutting down ExecutorService 'applicationTaskExecutor'
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.554 s
[INFO] Finished at: 2020-04-07T00:35:40-07:00
[INFO] ------------------------------------------------------------------------
➜ urllookup git:(master) ✗
```

</p>
</details>

## Instructions
Since this is Java application, you should have JRE and Maven installed to run this application

Here is what I have 
```
➜ urllookup git:(master) ✗ java -version
java version "1.8.0_181"
Java(TM) SE Runtime Environment (build 1.8.0_181-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.181-b13, mixed mode)
➜ urllookup git:(master) ✗
➜ urllookup git:(master) ✗
➜ urllookup git:(master) ✗ mvn --version
Apache Maven 3.6.1 (d66c9c0b3152b2e69ee9bac180bb8fcc8e6af555; 2019-04-04T12:00:29-07:00)
Maven home: /usr/local/Cellar/maven/3.6.1/libexec
Java version: 1.8.0_181, vendor: Oracle Corporation, runtime: /Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "10.15.1", arch: "x86_64", family: "mac"
➜ urllookup git:(master) ✗
```
Since I am using Redis as database, you need to have redis server available on machine. The default PORT for Redis is 6379.
If your redis server is running on different port then you need to update PORT value in `src/main/resources/application.yml`
NOTE - I am using 6370 PORT for embedded redis to eliminate dependency on redis for running unit and integration tests. 
Avoid using 6370 port for native server installation.

To install and start redis as service on macOS use following commands
```
brew install redis
brew services start redis
```

Use following steps to run the application
```
# Download the source code from git (You should have git installed on machine)
# Otherwise you could download zip file from https://github.com/bargemb/url-lookup/archive/master.zip
git clone https://github.com/bargemb/url-lookup.git

cd url-lookup

# Run following command to install and package your appliction
# This should also run tests.
mvn clean install

# Start the application using following commands
# This should start spring boot application on 8081 PORT
java -jar target/url-lookup-1.0-SNAPSHOT.jar

# To update Malware URL database use following command
➜ ~ curl -I --request POST --url 'http://localhost:8081/urlinfo/1/www.malware.com:8444/bad-resource'
HTTP/1.1 201
Content-Length: 0
Date: Tue, 07 Apr 2020 11:05:55 GMT

# To check whether URL is present in Malware database use following command
➜ ~ curl -I --request GET --url 'http://localhost:8081/urlinfo/1/www.malware.com:8444/bad-resource'
HTTP/1.1 403
Content-Length: 0
Date: Tue, 07 Apr 2020 11:06:17 GMT
```
## Improvements
* The size of the URL list could grow infinitely, how might you scale this beyond the memory capacity of this VM?
> This micro service can be horizontally scaled as URL list could grow infinitely. Load balancer can be added to 
> distribute the traffic across multiple backend servers hosting this service. Load balancer should route requests to 
> healthy backend servers hence we need to make sure that "Monitoring" is in place. Since we are dealing with time 
> sensitive service, I believe "Least Response Time" method for load balancing would be useful here. We also need to 
> make sure that Redis is configured in cluster mode to replicate the data. 
* The number of requests may exceed the capacity of this VM, how might you solve that?
> Horizontal scaling as discussed above is also useful in this case. Load balancer should be able to distribute load across
> the backend servers. We should have Monitoring solutions like Prometheus to monitor load with respect to number of request
> and VM capacity. Based on inputs from this monitoring service we should scale up/scale down our services. If the services
> are deployed on container orchestration solution like Kubernetes then this could be managed easily.
> Also, by responding with Forbidden HTTP status code (403) we are avoiding same requests.
* What are some strategies you might use to update the service with new URLs? Updates may be as much as 5 thousand URLs a day with updates arriving every 10 minutes.
> As per my assumption, there will be different micro service to update our malware database. This should help to scale 
> both services independently as per the requirements. This WRITE service would get list of malware URL's along with 
> additional properties(if any) from file. This could be scheduled operation every 10 minutes to update malware database.
> We should setup Redis in cluster mode with master-slave architecture for the replication purpose.
> This service could be optimized to use "Redis Pipelining" feature (https://redis.io/topics/pipelining)
* What would you want to add before deploying this service into a production environment that you would be  responsible for operating? What does getting a service ready for production involve? What deployment strategy would you use?
> Following things are important to get this service into production
> 1. Source code versioning - This is the first and foremost requirement for anything in production :-)
> 2. We should have Build, Test, Deploy stages figured out. Continuous Integration should be in place as system grows.
> 3. Configuration for the service may vary between multiple versions of deploys. This configuration should be separate from application code.
> 4. There would be different environments development, staging and production. There should be parity between all these environments.
> 5. Monitoring and alerting should be in place for health checks for the system.
> 6. Logging is necessary to figure out issues as well as analysis.
> 7. Use various methodologies for Redis persistence (https://redis.io/topics/persistence). This will come under backing up services.
> 8. For the deployment, I would deploy services into pre-prod/ UAT kind of environments for rigorous testing. Automated deployments can be then used to deploy into production. 
* What else would you consider when thinking about how this service might need to be evolved over time?
> Currently there are limited properties associated with Malware URL. We can make use of additional properties like 
> severity to fine tune our decision. Malware URL's can also be categorized and business logic could be improved to 
> make best decision. Log analysis can be used to learn more about Malicious URL patterns and improve write service which
> updates database.    
