package name.martingeisse.esdk.core.tools.validation;

import name.martingeisse.esdk.core.DesignItem;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public final class ValidationException extends RuntimeException {

    private final DesignValidationResult validationResult;

    public ValidationException(DesignValidationResult validationResult) {
        super(buildMessage(validationResult));
        this.validationResult = validationResult;
    }

    private static String buildMessage(DesignValidationResult validationResult) {
        Pair<DesignItem, ItemValidationResult> sample = validationResult.getSampleError(true);
        if (sample == null) {
            throw new IllegalArgumentException("trying to build a ValidationException for a clean validation result");
        }
        List<String> messages = new ArrayList<>();
        messages.addAll(sample.getRight().getErrors());
        messages.addAll(sample.getRight().getWarnings());
        return "validation failed with errors for item " + sample.getLeft() + ": " + messages;
    }

    public DesignValidationResult getValidationResult() {
        return validationResult;
    }

}
