# spa-archetype
Single page application (SPA) servlet Maven archetype, that creates a Maven project with a war output that serves static files. CORS filter is added by default. Includes tomcat7-maven-plugin to assist page hosting locally.

# Usage
Fork this repository and install this archetype locally. We will not be needing this if this archetype is released to a public Maven repository.
```
mvn clean install
```
Use Maven's `archetype:generate goal` to generate your project.
```
mvn archetype:generate -DgroupId=mygroup -DartifactId=myspa -Dversion=0.0.1-SNAPSHOT -DpackageName=my.package.spa -DarchetypeGroupId=io.azam.spa -DarchetypeArtifactId=spa-archetype -DarchetypeVersion=0.0.1-SNAPSHOT -DinteractiveMode=false
```
Compile/package your war file as usual.
```
mvn clean package
```
Run Tomcat 7.0 from maven.
```
mvn tomcat7:run-war
```
