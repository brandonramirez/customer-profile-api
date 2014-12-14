package com.brandonsramirez.customerProfileApi;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * JAX-RS resource which acts as endpoint for the /customer resource
 */
@Path("/customers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerResource {
  @Context ServletContext context;

  @GET
  @Path("/{customerId}")
  public Customer getCustomer(@PathParam("customerId") int customerId) {
    Customer c = ServiceLocator.getCustomerProfileService(context).getCustomerById(customerId);
    if (c != null) {
      return c;
    }
    else {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
  }
}
