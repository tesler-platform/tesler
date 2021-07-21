import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// Detect node npx runner
def launcher = System.properties['os.name'].toLowerCase().contains('windows') ? "npx.cmd" : "npx"

// Run create-react-app with latest @tesler-ui/cra-template-typescript
def cmd = ["${launcher}", "create-react-app", "${request.outputDirectory}/${request.artifactId}/ui", "--template", "@tesler-ui/cra-template-typescript@0.3.0-alpha3"]
def proc = cmd.execute(null, new File(request.outputDirectory))
proc.consumeProcessOutput(System.out, System.err)
proc.waitFor()

def cmd2 = ["mvn install -f ${artifactId}/pom.xml -P UI"]
def proc2 = cmd2.execute(null, new File(request.outputDirectory))
proc2.consumeProcessOutput(System.out, System.err)
proc2.waitFor()