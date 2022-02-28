package name.martingeisse.esdktest.designs.riscv_compliance;

import name.martingeisse.esdktest.designs.ExternalToolInvocation;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class RiscvComplianceTestMain {

    public static final File COMPLIANCE_TEST_REPO = new File("/home/martin.geisse/git-repos/riscv-compliance");
    public static final String TOOLCHAIN_PREFIX = "/home/martin.geisse/git-repos/riscv32-toolchain-build/bin/riscv32-unknown-elf-";
    public static final String[] ISAS_TO_TEST = new String[]{"rv32i", "rv32im"};

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
            File testSuiteSourceFolder = new File(COMPLIANCE_TEST_REPO, "riscv-test-suite/" + isa);
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

        for (String baseFileName : baseFileNames) {
            File sourceFile = new File(sourceFileFolder, baseFileName + ".S");
            File referenceFile = new File(referenceFileFolder, baseFileName + ".reference_output");
            File testBuildFolder = new File(testSuiteBuildFolder, baseFileName);
            performTest(sourceFile, referenceFile, testBuildFolder);
        }
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
        FileUtils.copyURLToFile(RiscvComplianceTestMain.class.getResource("compliance_io.h"), new File(testBuildFolder, "compliance_io.h"));
        FileUtils.copyURLToFile(RiscvComplianceTestMain.class.getResource("compliance_test.h"), new File(testBuildFolder, "compliance_test.h"));
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

        TODO how to stop the simulation?
        design.simulate();

    }

}

/*

RUN_TARGET=\
    grift-sim $(RISCV_TARGET_FLAGS) --arch=RV32I \
        --mem-dump-begin=begin_signature --mem-dump-end=end_signature \
	--halt-pc=grift_stop_addr \
        $(work_dir_isa)/$< > $(work_dir_isa)/$(*).signature.output 2> $(work_dir_isa)/$@;

 */