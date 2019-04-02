# OAS3 API Documentation

The API documentation is provided in the /doc/document-api.yml file and can be viewed/edited using Swagger-Editor.
The Swagger-Editor is available as a container:
<pre>
:~ docker pull swaggerapi/swagger-editor
:~ docker run -d -p 8080:8080 swaggerapi/swagger-editor
</pre>
Just browse to the localhost:8080 and open the /doc/dokuti-api.yml file.

## Generate the server code from the API documentation

The [OpenAPITools maven plugin has been installed](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-maven-plugin)
Run the plugin by calling:
```mvn generate-sources```

Note that code generation also occurs when you run:
```mvn clean package```

By default the output of this generation can be found in the folder ```${project.basedir}/srcgen```
Configuration of the code generation defaults is in the ```pom.xml``` file for the maven plugin.

When working with an IDE (such as Eclipse) you can add the ```${project.basedir}/srcgen``` as a build path. Then all generated classes will be a part of your project and you can code against them.