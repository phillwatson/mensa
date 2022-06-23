package com.hillayes.mensa.payment.resource;

import com.hillayes.mensa.exception.common.MissingParameterException;
import com.hillayes.mensa.payment.domain.Payment;
import com.hillayes.mensa.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

    @POST
    public Response createPayment(Payment payment) {
        if (payment == null) {
            throw new MissingParameterException("payment content");
        }

        log.info("Creating payment [payeeId: {}, memo: {}]", payment.getPayeeId(), payment.getMemo());
        Payment result = paymentService.createPayment(payment);

        log.debug("Created payment [paymentId: {}, payeeId: {}, memo: {}]",
            result.getPaymentId(), result.getPayeeId(), result.getMemo());
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{id}")
    public Response updatePayment(@PathParam("id") UUID id, Payment payment) {
        if (payment == null) {
            throw new MissingParameterException("Payment content");
        }

        log.info("Updating payment [paymentId: {}, payeeId: {}, memo: {}]", id, payment.getPayeeId(), payment.getMemo());
        Payment result = paymentService.updatePayment(id, payment);

        log.debug("Updated payment [paymentId: {}, payeeId: {}, memo: {}]",
            result.getPaymentId(), result.getPayeeId(), result.getMemo());
        return Response.ok(result).build();
    }
}
