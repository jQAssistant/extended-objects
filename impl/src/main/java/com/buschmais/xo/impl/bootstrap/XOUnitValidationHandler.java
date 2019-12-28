package com.buschmais.xo.impl.bootstrap;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

public class XOUnitValidationHandler implements ValidationEventHandler {

    private final List<String> errorMessages = new ArrayList<>();

    @Override
    public boolean handleEvent(ValidationEvent event) {
        if (event.getSeverity() == ValidationEvent.ERROR || event.getSeverity() == ValidationEvent.FATAL_ERROR) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("line ");
            stringBuilder.append(event.getLocator().getLineNumber());
            stringBuilder.append(": ");
            stringBuilder.append(event.getMessage());
            this.errorMessages.add(stringBuilder.toString());
        }
        return true;
    }

    public boolean isValid() {
        return this.errorMessages.isEmpty();
    }

    public String getValidationMessages() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String errorMessage : errorMessages) {
            stringBuilder.append(errorMessage);
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }
}
