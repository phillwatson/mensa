package com.hillayes.mensa.payment.resource;

import com.hillayes.mensa.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/payments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@Slf4j
public class PaymentResource {
    private final PaymentService paymentService;

    @GET
    @Path("/audit/{id}")
    public Response getPaymentAuditEvent(@PathParam("id") UUID id) {
        log.info("Looking for payment-audit-event [id: {}]", id);
        return paymentService.uploadPaymentEvent(id)
            .map(event -> Response.ok(event).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}
