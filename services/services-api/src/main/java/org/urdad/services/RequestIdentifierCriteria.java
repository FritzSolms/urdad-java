package org.urdad.services;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RequestIdentifierCriteria extends Criteria {

    @NotNull
    @Size(min = 1)
    private String requestIdentifier;

    public String getRequestIdentifier() {
        return requestIdentifier;
    }

    public void setRequestIdentifier(String requestIdentifier) {
        this.requestIdentifier = requestIdentifier;
    }

    public RequestIdentifierCriteria() {
    }

    public RequestIdentifierCriteria(String requestIdentifier) {

        this.requestIdentifier = requestIdentifier;
    }
}
