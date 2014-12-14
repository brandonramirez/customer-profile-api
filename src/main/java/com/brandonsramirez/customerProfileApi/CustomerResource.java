package com.brandonsramirez.customerProfileApi;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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
    //return ServiceLocator.getCustomerProfileService(context).getCustomerById(customerId);
    Customer c = new Customer();
    c.setCustomerId(1);
    c.setFirstName("Brandon");
    c.setLastName("Ramirez");
    return c;
  }
}
