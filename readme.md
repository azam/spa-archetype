# spa-archetype
Single page application (SPA) Java servlet Maven archetype. This archetype creates a Maven war project that serves static files. CORS filter is added by default. Includes tomcat7-maven-plugin to assist page hosting locally.

# Usage
1. Fork this repository and install this archetype locally. (Note: We will not be needing this if this archetype is released to a public Maven repository.)
```
mvn clean install
```
2. Use Maven's `archetype:generate goal` to generate your project.
```
mvn archetype:generate -DgroupId=mygroup -DartifactId=myspa -Dversion=0.0.1-SNAPSHOT -DpackageName=my.package.spa -DarchetypeGroupId=io.azam.spa -DarchetypeArtifactId=spa-archetype -DarchetypeVersion=0.0.1-SNAPSHOT -DinteractiveMode=false
```
3. Compile/package your war file.
```
mvn clean package
```
4. (Optional) Run Tomcat 7 from maven.
```
mvn tomcat7:run-war
```
