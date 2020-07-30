import java.nio.file.Files
import java.nio.file.Path

// Detect node npx runner
def launcher = System.properties['os.name'].toLowerCase().contains('windows') ? "npx.cmd" : "npx"

// Run create-react-app with latest @tesler-ui/cra-template-typescript
def cmd = "${launcher} create-react-app ${request.outputDirectory}/${request.artifactId}/${request.artifactId}-ui --template @tesler-ui/cra-template-typescript"
def proc = cmd.execute(null, new File(request.outputDirectory))
proc.consumeProcessOutput(System.out, System.err)
proc.waitFor()

// Move UI pom from archetype to CRA-generated folder
String pomLocation = "${request.outputDirectory}/${request.artifactId}/${request.artifactId}-ui-template"
Path pom = Path.of("${pomLocation}/pom.xml")
Path ui = Path.of("${request.outputDirectory}/${request.artifactId}/${request.artifactId}-ui/pom.xml")
Files.copy(pom, ui)
Files.delete(pom)
Files.delete(Path.of(pomLocation))

println "\nDone. Exit value: ${proc.exitValue()}"
