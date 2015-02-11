# About
Entitypedia Games Spring MVC Doclet is a Javadoc Doclet which generates Swagger 1.2 specification to document APIs
following Entitypedia Games Framework conventions.

# Usage
This doclet can be used with ```javadoc``` tool from command line. Another way is to embed it in Maven build:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <executions>
        <execution>
            <phase>prepare-package</phase>
            <goals>
                <goal>javadoc</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <doclet>org.entitypedia.games.sdoclet.SpringMVCDoclet</doclet>
        <docletArtifact>
            <groupId>org.entitypedia.games</groupId>
            <artifactId>sdoclet</artifactId>
            <version>1.0.0</version>
        </docletArtifact>
        <!-- For the project-reports page-->
        <name>Swagger Spring MVC Doclet</name>
        <description>Swagger documentation.</description>
        <outputDirectory>${project.build.directory}/${project.build.finalName}/apidocs</outputDirectory>
        <reportOutputDirectory>${project.build.directory}/${project.build.finalName}/apidocs</reportOutputDirectory>
        <show>private</show>
        <charset>${project.build.sourceEncoding}</charset>
        <includeDependencySources>true</includeDependencySources>
        <includeTransitiveDependencySources>true</includeTransitiveDependencySources>
        <dependencySourceIncludes>
            <dependencySourceInclude>org.entitypedia.games:*</dependencySourceInclude>
        </dependencySourceIncludes>
        <subpackages>org.entitypedia.games.YOUR_GAME_NAME</subpackages>
        <verbose>false</verbose>
        <debug>false</debug>

        <additionalparam>
            -apiBasePath /YOUR_GAME_NAME/webapi/
            -docBasePath /YOUR_GAME_NAME/apidocs/
            -apiVersion ${project.dependencies[1].version}
            -apiPackage org.entitypedia.games.YOUR_GAME_NAME.common.api
            -controllerPackage org.entitypedia.games.YOUR_GAME_NAME.api.controller
        </additionalparam>
        <useStandardDocletOptions>false</useStandardDocletOptions>
    </configuration>
</plugin>
```

# Acknowledgements
Inspired by https://github.com/ryankennedy/swagger-jaxrs-doclet