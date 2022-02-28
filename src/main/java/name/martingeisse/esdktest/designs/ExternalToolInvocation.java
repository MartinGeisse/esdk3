package name.martingeisse.esdktest.designs;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * This class merged stdout and stderr of the tool process, intermixing their output. However, the timing of these
 * outputs should make the result predictable and even opens up the order between the two for validation.
 */
public final class ExternalToolInvocation {

    // must be set
    public File toolExecutable = null;

    // null elements will be skipped -- this makes it easier to add elements conditionally
    public Object[] arguments = {};

    // must be set. This is either absolute or relative to this Java process's working directory.
    // All other directories are absolute or relative to this one.
    public File workingDirectory = null;

    // set to null for "don't care"
    public Integer expectedStatusCode = 0;

    // change if non-ASCII output is expected
    public Charset outputEncoding = StandardCharsets.US_ASCII;

    // change if output is expected. May perform other checks, such as if an expected output file exists.
    // Note that (statusCode == expectedStatusCode) is validated independently outside this validator.
    public OutputValidator outputValidator = (statusCode, output) -> output.isBlank() ? null : "unexpected output";

    public interface OutputValidator {
        String validateOutput(int statusCode, String output);
    }

    public void validate() {
        if (toolExecutable == null) {
            throw new IllegalStateException("toolExecutable is not set");
        }
        if (arguments == null) {
            throw new IllegalStateException("arguments is not set");
        }
        if (workingDirectory == null) {
            throw new IllegalStateException("workingDirectory is not set");
        }
        if (!workingDirectory.isDirectory()) {
            throw new IllegalStateException("toolExecutable does not exist or is not a directory");
        }
    }

    // returns a new list each time
    public List<String> assembleCommandLine() {
        List<String> commandLine = new ArrayList<>();
        commandLine.add(toolExecutable.toString());
        for (Object argument : arguments) {
            if (argument != null) {
                commandLine.add(argument.toString());
            }
        }
        return commandLine;
    }

    public void invoke() throws IOException, InterruptedException {
        validate();

        List<String> commandLine = assembleCommandLine();
        ProcessBuilder builder = new ProcessBuilder(commandLine);
        builder.directory(workingDirectory);
        builder.redirectErrorStream(true);

        Process process = builder.start();
        int statusCode = process.waitFor();
        byte[] outputBytes = IOUtils.toByteArray(process.getInputStream());
        String outputIso88591 = new String(outputBytes, StandardCharsets.ISO_8859_1);

        String primaryErrorMessage = validate(statusCode, outputBytes);
        if (primaryErrorMessage != null) {
            System.err.println();
            System.err.println("***********************************");
            System.err.println("*** ERROR RUNNING EXTERNAL TOOL ***");
            System.err.println("***********************************");
            System.err.println();
            System.err.println("reason: " + primaryErrorMessage);
            System.err.println("working directory: " + workingDirectory);
            System.err.println("command line: " + commandLine);
            System.err.println("status code: " + statusCode + " (expected: " + expectedStatusCode + ")");
            System.err.println();
            System.err.println("--- tool output ----------------------------------------");
            System.err.println(outputIso88591);
            System.err.println("--- end of tool output ---------------------------------");
            System.err.flush();
            System.exit(1);
        }
    }

    private String validate(int statusCode, byte[] outputBytes) {
        if (statusCode != expectedStatusCode) {
            return "unexpected status code";
        }
        String output;
        try {
            output = new String(outputBytes, outputEncoding);
        } catch (Exception e) {
            return "could not decode output according to expected output encoding " + outputEncoding;
        }
        return outputValidator.validateOutput(statusCode, output);
    }

}
