# spa-archetype
==
Single page application (SPA) Java servlet Maven archetype. This archetype creates a Maven war project that serves static files. CORS filter is added by default. Includes tomcat7-maven-plugin to assist page hosting locally.

## Usage

1. Use Maven's `archetype:generate` goal to generate your project.

  ```
mvn archetype:generate -DgroupId=mygroup -DartifactId=myspa -Dversion=0.0.1-SNAPSHOT -DpackageName=my.package.spa -DarchetypeGroupId=io.azam.spa -DarchetypeArtifactId=spa-archetype -DarchetypeVersion=1.0.1 -DinteractiveMode=false
```
or simply use the interactive archetype generation and navigate through the questions.

  ```
mvn archetype:generate
```
2. Compile/package your war file.

  ```
mvn clean package
```
3. (Optional) Run Tomcat 7 from maven.

  ```
mvn tomcat7:run-war
```

## Settings

* All static files are served from `src/main/webapp/web` directory by default. Custom directory may be used by changing the `webPath` parameter on `web.xml` to any class resource directory.
