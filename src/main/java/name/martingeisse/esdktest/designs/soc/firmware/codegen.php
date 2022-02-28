#!/usr/bin/env php
<?php

$sections = array(

    'divrem' => array(
        'udivrem',
        'udiv',
        'urem',
        'div',
    ),

    'term' => array(
        'termInitialize',
        'termPrintString',
        'termPrintChar',
        'termPrintInt',
        'termPrintUnsignedInt',
        'termPrintHexInt',
        'termPrintUnsignedHexInt',
        'termPrintln',
        'termPrintlnString',
        'termPrintlnChar',
        'termPrintlnInt',
        'termPrintlnUnsignedInt',
        'termPrintlnHexInt',
        'termPrintlnUnsignedHexInt',
    ),

    'profiling' => array(
        'profReset',
        'profLog',
        'profDisplay',
    ),

);

$address = 8;

echo '--------------------------------------------------------------------------------------', "\n";
echo 'builtin.S', "\n";
echo '--------------------------------------------------------------------------------------', "\n";
echo "\n";
echo '.option norvc', "\n";
foreach ($sections as $sectionName => $functions) {
    echo "\n";
    echo '///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////', "\n";
    echo '// ', $sectionName, "\n";
    echo '///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////', "\n";
    foreach ($functions as $functionName) {
        echo "\n";
        echo '.globl ', $functionName, "\n";
        echo '.set ', $functionName, ', ', $address, "\n";
        $address += 4;
    }
}

echo "\n";
echo "\n";
echo '--------------------------------------------------------------------------------------', "\n";
echo 'start.S', "\n";
echo '--------------------------------------------------------------------------------------', "\n";
echo "\n";
foreach ($sections as $sectionName => $functions) {
    foreach ($functions as $functionName) {
        echo '    .word ', $functionName, "\n";
    }
}
