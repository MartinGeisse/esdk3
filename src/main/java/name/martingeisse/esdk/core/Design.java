/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core;

import name.martingeisse.esdk.core.library.signal.BitConstant;
import name.martingeisse.esdk.core.tools.validation.DesignValidationResult;
import name.martingeisse.esdk.core.tools.validation.DesignValidator;
import name.martingeisse.esdk.core.tools.validation.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 */
public class Design {

    private final List<DesignItem> items = new ArrayList<>();
    private boolean constructionFinalized;
    private Simulation simulation;

    /**
     * Note: This constructor sets this design to be used as the implicit design for new items.
     */
    public Design() {
        ImplicitGlobalDesign.set(this);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // item management
    // ----------------------------------------------------------------------------------------------------------------

    void registerDesignItem(DesignItem item) {
        items.add(item);
    }

    public final Iterable<DesignItem> getItems() {
        return items;
    }

    public final <T extends DesignItem> List<T> getItems(Class<T> itemClass) {
        List<T> result = new ArrayList<>();
        for (DesignItem item : items) {
            if (itemClass.isInstance(item)) {
                result.add(itemClass.cast(item));
            }
        }
        return result;
    }

    public final <T extends DesignItem> List<T> findItemsByNameSubstring(Class<T> itemClass, String nameSubstring) {
        List<T> result = getItems(itemClass);
        result.removeIf(item -> item.getName() == null || !item.getName().contains(nameSubstring));
        return result;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // construction finalization and validation
    // ----------------------------------------------------------------------------------------------------------------

    public final void finalizeConstruction() {
        finalizeConstruction(result -> {
        });
    }

    public final void finalizeConstruction(Consumer<DesignValidationResult> validationResultConsumer) {
        if (constructionFinalized) {
            return;
        }
        for (DesignItem item : items) {
            item.finalizeConstructionBeforeValidation();
        }
        DesignValidator validator = new DesignValidator(this);
        DesignValidationResult validationResult = validator.validate();
        validationResultConsumer.accept(validationResult);
        if (!validationResult.isValid(true)) {
            throw new ValidationException(validationResult);
        }
        for (DesignItem item : items) {
            item.finalizeConstructionAfterValidation();
        }
        constructionFinalized = true;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // simulation
    // ----------------------------------------------------------------------------------------------------------------

    public final void simulate() {
        prepareSimulation();
        continueSimulation();
    }

    public final void prepareSimulation() {
        if (simulation != null) {
            throw new IllegalStateException("simulation already prepared");
        }
        finalizeConstruction();
        simulation = new Simulation();
        for (DesignItem item : items) {
            item.initializeSimulation();
        }
    }

    public final void continueSimulation() {
        needSimulation();
        simulation.run();
    }

    public final void stopSimulation() {
        needSimulation();
        simulation.stop();
    }

    public final void fire(Runnable eventCallback, long ticks) {
        needSimulation();
        simulation.fire(eventCallback, ticks);
    }

    private void needSimulation() {
        if (simulation == null) {
            throw new IllegalStateException("simulation not prepared");
        }
    }

}
