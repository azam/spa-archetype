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

# Release
1. Set up GnuPG. (First time only) 
[Working with PGP Signatures](http://central.sonatype.org/pages/working-with-pgp-signatures.html)
```
gpg --gen-key      (Use defaults)
gpg --list-keys    (Get ID for the pub key)
gpg --keyserver hkp://pool.sks-keyservers.net --send-keys %KEY_ID%
```
2. Create `settings.xml`.
```
<settings>
	<servers>
		<server>
			<id>oss-sonatype</id>
			<username>your-sonatype-username</username>
			<password>your-sonatype-password</password>
		</server>
	</servers>
<settings>
```
3. Clean, package, sign and deploy to Sonatype staging using maven-gpg-plugin. Ref: [Manual Staging Bundle Creation and Deployment](http://central.sonatype.org/pages/manual-staging-bundle-creation-and-deployment.html)
```
mvn gpg:sign-and-deploy-file -s settings.xml -DrepositoryId=oss-sonatype -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2 -DpomFile=spa-archetype-1.0.0.pom -Dfile=spa-archetype-1.0.0.jar
```
Useful note: Deploy without signing:
```
mvn clean package gpg:sign deploy -DaltDeploymentRepository=oss-sonatype::default::https://oss.sonatype.org/service/local/staging/deploy/maven2 -s settings.xml
```
4. Browse to [Staging Repositories](https://oss.sonatype.org/#stagingRepositories).
5. Close and release the repository.