/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.tools.synthesis.verilog;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.library.signal.Signal;

/**
 *
 */
public interface SynthesisPreparationContext {

    /**
     * This method should be used if something other than a signal uses a fixed name.
     * The Verilog generation framework will make sure that no collision with that
     * name occurs.
     *
     * For signals, declareFixedNameSignal() is more appropriate because it allows to
     * have a declaration and/or assignment generated for the caller.
     */
    void assignFixedName(String name, DesignItem item);

    /**
     * Assigns a Verilog name to the specified item and returns it. The name is stored internally in the
     * Verilog generation framework. During output generation, the methods of the VerilogWriter
     * allow access to the name, so there is normally no need to store the return value.
     */
    String assignGeneratedName(DesignItem item);

    /**
     * Informs the Verilog generation framework about a signal with a fixed name. Other than
     * the fixed name, this method behaves like
     * {@link #declareSignal(Signal, VerilogSignalDeclarationKeyword, boolean)}. Have a look there
     * for details.
     */
    void declareFixedNameSignal(Signal signal,
                                String name,
                                VerilogSignalDeclarationKeyword keyword,
                                boolean generateAssignment);

    /**
     * Informs the Verilog generation framework about a signal. A name will be generated for the signal.
     *
     * The declaration keyword determines whether no declaration, or a wire declaration, or a reg
     * declaration gets emitted to the output for the signal.
     *
     * The generateAssignment flag determines whether the implementation part generates an assignment
     * for the signal, using its name and its implementing expression.
     */
    String declareSignal(Signal signal,
                         VerilogSignalDeclarationKeyword keyword,
                         boolean generateAssignment);

    /**
     * Gets the auxiliary file factory that can be used to write files accompanying the verilog
     * output, such as MIF files for memory initialization.
     */
    AuxiliaryFileFactory getAuxiliaryFileFactory();

}
