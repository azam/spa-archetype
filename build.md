# Build and Release notes

## Build

* Always run `mvn clean install archetype:integration-test` before release

## Release

1. (First time only) Set up GnuPG. Ref: [Working with PGP Signatures](http://central.sonatype.org/pages/working-with-pgp-signatures.html)

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
mvn clean package gpg:sign deploy -DaltDeploymentRepository=oss-sonatype::default::https://oss.sonatype.org/service/local/staging/deploy/maven2 -s settings.xml
```
4. Browse to [Staging Repositories](https://oss.sonatype.org/#stagingRepositories).
5. Close and release the repository.
