package com.hillayes.mensa.payment.resource;

import com.hillayes.mensa.exception.common.MissingParameterException;
import com.hillayes.mensa.payment.domain.Payout;
import com.hillayes.mensa.payment.service.PayoutService;
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

@Path("/payouts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@Slf4j
public class PayoutResource {
    private final PayoutService payoutService;

    @POST
    public Response createPayout(Payout payout) {
        if (payout == null) {
            throw new MissingParameterException("payout content");
        }

        log.info("Creating payout [payorId: {}, memo: {}]", payout.getPayoutToPayorId(), payout.getPayoutMemo());
        Payout result = payoutService.createPayout(payout);

        log.debug("Created payout [payoutId: {}, payeeId: {}, memo: {}]",
            result.getPayoutId(), result.getPayoutToPayorId(), result.getPayoutMemo());
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{id}")
    public Response updatePayout(@PathParam("id") UUID id, Payout payout) {
        if (payout == null) {
            throw new MissingParameterException("payout content");
        }

        log.info("Updating payout [payoutId: {}, payorId: {}, memo: {}]", id, payout.getPayoutToPayorId(), payout.getPayoutMemo());
        Payout result = payoutService.updatePayout(id, payout);

        log.debug("Updated payout [payoutId: {}, payeeId: {}, memo: {}]",
            result.getPayoutId(), result.getPayoutToPayorId(), result.getPayoutMemo());
        return Response.ok(result).build();
    }
}
