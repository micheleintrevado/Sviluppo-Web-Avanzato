package org.univaq.swa.template.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class RESTWebApplicationException extends WebApplicationException {

    public RESTWebApplicationException() {
        super(Response.serverError().build());
    }

    public RESTWebApplicationException(String message) {
        super(Response.serverError()
                .entity(message)
                .type("text/plain")
                .build());
    }

    public RESTWebApplicationException(int status, String message) {
        super(Response.status(status)
                .entity(message)
                .type("text/plain")
                .build());
    }

    public RESTWebApplicationException(Exception ex) {
        super(Response.status(500)
                .entity(ex.getMessage())
                .type("text/plain")
                .build());
    }
}
