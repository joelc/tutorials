package com.baeldung.cxf.jaxrs.implementation;

import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

@Path("poc")
@Produces("text/json")
public class Resource {
    private static String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/1111/jcaplin3";

    private SqsClient syncSqs;
    private SqsAsyncClient asyncSqs;

    public Resource() {
        syncSqs = SqsClient.create();
        asyncSqs = SqsAsyncClient.create();
    }

    @GET
    @Path("/async")
    public void workAsync(@Suspended AsyncResponse ar) {
        asyncSqs.sendMessage(b -> b.queueUrl(QUEUE_URL)
                                   .messageBody("BODY"))
                .thenAccept(resp -> {
                    ar.resume("SENT ASYNC!");
                })
                .exceptionally(t -> {
                    t.printStackTrace();

                    ar.resume("EXCEPTION ASYNC!");

                    return null;
                });
    }

    @GET
    @Path("/sync")
    public String workSync() {
        try {
            syncSqs.sendMessage(b -> b.queueUrl(QUEUE_URL)
                                      .messageBody("BODY"));
        } catch (RuntimeException e) {
            e.printStackTrace();

            return "EXCEPTION SYNC!";
        }

        return "SENT SYNC!";
    }
}
