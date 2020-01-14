# OpenAPI

Open API servlet generated dynamically by reflection.

# Server

    gradle appRunWar

Then

    http://localhost:8080/open-api-by-reflection/reflection
    
or
    
    http://localhost:8080/open-api-by-reflection/annotation/swagger.json
    

# CLI

Build

    gradle fatJar

Run

    java -jar build/libs/open-api-by-reflection-all.jar -p com.openapi.controller -o /tmp/openapi.json
    
# Swagger-UI

    docker run -d -p 80:8080 -e SWAGGER_JSON=/foo/openapi.json -v /tmp:/foo swaggerapi/swagger-ui

# Ref

* [SwaggerUI](https://swagger.io/docs/open-source-tools/swagger-ui/usage/installation)

* [JCommander](https://jcommander.org/)