package com.brandonsramirez.customerProfileApi;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * JAX-RS resource for identifying duplicate customer profiles
 */
@Path("/duplicates")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DuplicateCustomerResource {
  @Context ServletContext context;

  @GET
  public Response listDuplicates() {
    return Response.ok().entity(getCustomerProfileService().findDuplicates()).build();
  }

  @GET
  @Path("/{customerId}")
  public Response findDuplicatesForProfile(@PathParam("customerId") int customerId) {
    return Response.ok().entity(getCustomerProfileService().findDuplicatesOfProfile(customerId)).build();
  }

  protected CustomerProfileService getCustomerProfileService() {
    return getCustomerProfileService(context);
  }

  protected CustomerProfileService getCustomerProfileService(ServletContext ctx) {
    return ServiceLocator.getCustomerProfileService(ctx);
  }
}