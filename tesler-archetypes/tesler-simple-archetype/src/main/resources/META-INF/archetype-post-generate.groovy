import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// Detect node npx runner
def launcher = System.properties['os.name'].toLowerCase().contains('windows') ? "npx.cmd" : "npx"

// Run create-react-app with latest @tesler-ui/cra-template-typescript
def cmd = "${launcher} create-react-app ${request.outputDirectory}/${request.artifactId}/ui --template @tesler-ui/cra-template-typescript@0.3.0-alpha1"
def proc = cmd.execute(null, new File(request.outputDirectory))
proc.consumeProcessOutput(System.out, System.err)
proc.waitFor()
