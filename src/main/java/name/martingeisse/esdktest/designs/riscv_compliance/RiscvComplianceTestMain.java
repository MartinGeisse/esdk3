package name.martingeisse.esdktest.designs.riscv_compliance;

import name.martingeisse.esdktest.designs.ExternalToolInvocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO also run the tests from https://github.com/riscv-software-src/riscv-tests
 */
public class RiscvComplianceTestMain {

    public static final File COMPLIANCE_TEST_REPO = new File("/home/martin.geisse/git-repos/riscv-arch-test");
    public static final String TOOLCHAIN_PREFIX = "/home/martin.geisse/git-repos/riscv32-toolchain-build/bin/riscv32-unknown-elf-";
    public static final String[] ISAS_TO_TEST = {"I", "M"};
    public static final String[] EXCLUDED_BASE_NAMES = {"I-ECALL-01", "I-MISALIGN_JMP-01"};

    private static void mkdir(File directory) {
        if (!directory.mkdir()) {
            throw new RuntimeException("failed to mkdir: " + directory);
        }
    }

    public static final File buildRootFolder;
    static {
        File systemTempFolder = new File(System.getProperty("java.io.tmpdir"));
        buildRootFolder = new File(systemTempFolder, "riscv-compliance-" + System.currentTimeMillis());
        mkdir(buildRootFolder);
        buildRootFolder.deleteOnExit();
    }

    public static void main(String[] args) throws Exception {
        for (String isa : ISAS_TO_TEST) {
            File testSuiteSourceFolder = new File(COMPLIANCE_TEST_REPO, "riscv-test-suite/rv32i_m/" + isa);
            File testSuiteBuildFolder = new File(buildRootFolder, isa);
            mkdir(testSuiteBuildFolder);
            runSuite(testSuiteSourceFolder, testSuiteBuildFolder);
        }
    }

    private static void runSuite(File testSuiteSourceFolder, File testSuiteBuildFolder) throws Exception {
        File sourceFileFolder = new File(testSuiteSourceFolder, "src");
        File referenceFileFolder = new File(testSuiteSourceFolder, "references");

        Set<String> baseFileNames = getBaseFileNames(sourceFileFolder, ".S");
        if (!baseFileNames.equals(getBaseFileNames(referenceFileFolder, ".reference_output"))) {
            throw new RuntimeException("mismatch between set of source files and set of reference files: " +
                baseFileNames + ", " + getBaseFileNames(referenceFileFolder, ".reference_output"));
        }

        Set<String> excludedBaseNames = new HashSet<>();
        for (String name : EXCLUDED_BASE_NAMES) {
            excludedBaseNames.add(name);
        }

//        for (String baseFileName : baseFileNames) {
//            if (excludedBaseNames.contains(baseFileName)) {
//                continue;
//            }
//            File sourceFile = new File(sourceFileFolder, baseFileName + ".S");
//            File referenceFile = new File(referenceFileFolder, baseFileName + ".reference_output");
//            File testBuildFolder = new File(testSuiteBuildFolder, baseFileName);
//            performTest(sourceFile, referenceFile, testBuildFolder);
//        }

        String baseFileName = "sh-align-01";
        File sourceFile = new File(sourceFileFolder, baseFileName + ".S");
        File referenceFile = new File(referenceFileFolder, baseFileName + ".reference_output");
        File testBuildFolder = new File(testSuiteBuildFolder, baseFileName);
        performTest(sourceFile, referenceFile, testBuildFolder);

    }

    private static Set<String> getBaseFileNames(File folder, String dotExtension) {
        Set<String> baseFileNames = new HashSet<>();
        for (String fileName : folder.list()) {
            if (!fileName.endsWith(dotExtension)) {
                throw new RuntimeException("file with unexpected extension: " + fileName);
            }
            baseFileNames.add(fileName.substring(0, fileName.length() - dotExtension.length()));
        }
        return baseFileNames;
    }

    private static void performTest(File sourceFileAtOriginalLocation, File referenceFile, File testBuildFolder) throws Exception {
        File sourceFile = new File(testBuildFolder, sourceFileAtOriginalLocation.getName());
        FileUtils.copyFile(sourceFileAtOriginalLocation, sourceFile);
        FileUtils.copyURLToFile(RiscvComplianceTestMain.class.getResource("model_test.h"), new File(testBuildFolder, "model_test.h"));
        FileUtils.copyURLToFile(RiscvComplianceTestMain.class.getResource("linkerscript"), new File(testBuildFolder, "linkerscript"));

        System.out.println("*** performTest");
        System.out.println("  " + sourceFile);
        System.out.println("  " + referenceFile);
        System.out.println("  " + testBuildFolder);

        // gcc
        {
            ExternalToolInvocation invocation = new ExternalToolInvocation();
            invocation.toolExecutable = new File(TOOLCHAIN_PREFIX + "gcc");
            invocation.arguments = new Object[] {
                "-static", "-mcmodel=medany", "-fvisibility=hidden", "-nostdlib", "-nostartfiles",
                "-I" + new File(COMPLIANCE_TEST_REPO, "riscv-test-env"),
                "-T", "linkerscript",
                "-o", "program.elf",
                sourceFile
            };
            invocation.workingDirectory = testBuildFolder;
            invocation.expectedStatusCode = 0;
            // invocation.outputValidator = (statusCode, output) -> output.length() > 50 ? "output too long" : null;
            invocation.invoke();
        }

        // objcopy
        {
            ExternalToolInvocation invocation = new ExternalToolInvocation();
            invocation.toolExecutable = new File(TOOLCHAIN_PREFIX + "objcopy");
            invocation.arguments = new Object[] {
                "-j", ".image", "-I", "elf32-littleriscv", "-O", "binary", "program.elf", "program.bin"
            };
            invocation.workingDirectory = testBuildFolder;
            invocation.expectedStatusCode = 0;
            // invocation.outputValidator = (statusCode, output) -> output.length() > 50 ? "output too long" : null;
            invocation.invoke();
        }

        // run the test
        ComplianceTestingDesign design = new ComplianceTestingDesign();
        design.loadMemoryContents(new File(testBuildFolder, "program.bin"));
        design.simulate();

        // verify the outputs
        int[] output = design.getOutput();
        LineIterator lineIterator = FileUtils.lineIterator(referenceFile);
        for (int actualValue : output) {
            if (!lineIterator.hasNext()) {
                throw new RuntimeException("too many actual output values");
            }
            long expectedValueLong = Long.parseLong(lineIterator.next(), 16);
            if ((expectedValueLong & 0xffffffffL) != expectedValueLong) {
                throw new RuntimeException("expected value has more than 32 bits");
            }
            int expectedValue = (int)expectedValueLong;
            if (actualValue != expectedValue) {
                throw new RuntimeException("expected value " + Long.toHexString(expectedValue & 0xffffffffL) +
                    ", actual value " + Long.toHexString(actualValue & 0xffffffffL));
            }
        }

    }

}
