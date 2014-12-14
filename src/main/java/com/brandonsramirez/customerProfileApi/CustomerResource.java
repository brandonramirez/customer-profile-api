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
    Customer c = getCustomerProfileService().getCustomerById(customerId);
    if (c != null) {
      return c;
    }
    else {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
  }

  @DELETE
  @Path("/{customerId}")
  public void deleteCustomer(@PathParam("customerId") int customerId) {
    try {
      getCustomerProfileService().deleteCustomer(customerId);
    }
    catch (NonExistentCustomerException e) {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
  }

  protected CustomerProfileService getCustomerProfileService() {
    return getCustomerProfileService(context);
  }

  protected CustomerProfileService getCustomerProfileService(ServletContext ctx) {
    return ServiceLocator.getCustomerProfileService(ctx);
  }
}
