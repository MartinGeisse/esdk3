#!/usr/bin/env php
<?php

define('TOOL', 'riscv32-unknown-elf-');

$objectFiles = array();

function buildFile($inputPath, $outputPath) {
    global $objectFiles;
    $objectFiles[] = $outputPath;

    $dotPosition = strrpos($inputPath, '.');
    if ($dotPosition === FALSE) {
        die('no dot in input filename');
    }
    $extension = substr($inputPath, $dotPosition + 1);
    $allowedExtensions = array('c', 'cpp', 'S');
    if (!in_array($extension, $allowedExtensions)) {
        die('unknown input file extension: ' . $extension);
    }

    $cppFlags = ($extension == '.cpp' ? ' -fno-rtti ' : '');
    if ($extension != 'S') {
        system(TOOL . 'gcc -msmall-data-limit=100000 -march=rv32im -mabi=ilp32 -fno-exceptions ' . $cppFlags .
            ' -Wall -S -O3 -fno-tree-loop-distribute-patterns -o ' . $outputPath . '.S ' . $inputPath);
    }
    system(TOOL . 'gcc -msmall-data-limit=100000 -march=rv32im -mabi=ilp32 -fno-exceptions ' . $cppFlags .
        ' -Wall -c -O3 -fno-tree-loop-distribute-patterns -o ' . $outputPath . ' ' . $inputPath);
}

function linkFiles() {
    global $objectFiles;
    $objectFilesList = implode(' ', $objectFiles);
    system(TOOL . 'ld -T src/linkerscript -Map=build/program.map -A rv32im -o build/program.elf ' . $objectFilesList);
}

function convertExecutable() {
    system(TOOL . 'objcopy -j .image -I elf32-littleriscv -O binary build/program.elf build/program.bin');
}

//
// --------------------------------------------------------------------------------------------------------------------
//

system('rm -rf build');
system('mkdir build');

$paths = array(
    'src/start.S' => 'build/start.o',
    'src/memmap.S' => 'build/memmap.o',
    'src/main.c' => 'build/main.o',
    'src/divrem.c' => 'build/divrem.o',
    'src/delay.S' => 'build/delay.o',
    'src/term.c' => 'build/term.o',
    'src/profiling.c' => 'build/profiling.o',
);
foreach ($paths as $inputPath => $outputPath) {
    buildFile($inputPath, $outputPath);
}
linkFiles();
convertExecutable();
