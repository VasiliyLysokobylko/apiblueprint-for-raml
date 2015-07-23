This project provides a API Blueprint to RAML converter

#Installation

Checkout project and run `mvn clean install` on apiblueprint-raml-codegen-core.

#Running the converter

In the `apiblueprint-raml-codegen-core/target` subdirectory you can find a jar with complete dependencies. Run it the following way:
```
java -cp {jarName} launcher.Launcher -input {absolute path to inputFile} -output {absolute path to outputFile}
```
