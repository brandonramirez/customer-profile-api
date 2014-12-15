package com.brandonsramirez.customerProfileApi;

import java.net.URI;
import java.net.URISyntaxException;

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
  public Response getCustomer(@PathParam("customerId") int customerId) {
    Customer c = getCustomerProfileService().getCustomerById(customerId);
    if (c != null) {
      return Response.ok().entity(c).build();
    }
    else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @POST
  public Response createCustomer(Customer newCustomer) {
    try {
      int id = getCustomerProfileService().createCustomer(newCustomer);
      newCustomer.setCustomerId(id);
      try {
        return Response.created(new URI("customers/" + id)).entity(newCustomer).build();
      }
      catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }
    }
    catch (InvalidCustomerProfileException e) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
  }

  @PUT
  @Path("/{customerId}")
  public Response updateCustomer(@PathParam("customerId") int customerId, Customer updatedCustomerDetails) {
    updatedCustomerDetails.setCustomerId(customerId);

    try {
      getCustomerProfileService().updateCustomer(updatedCustomerDetails);
      return Response.noContent().build();
    }
    catch (NonExistentCustomerException e) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    catch (InvalidCustomerProfileException e) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
  }

  @DELETE
  @Path("/{customerId}")
  public Response deleteCustomer(@PathParam("customerId") int customerId) {
    try {
      getCustomerProfileService().deleteCustomer(customerId);
      return Response.noContent().build();
    }
    catch (NonExistentCustomerException e) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  protected CustomerProfileService getCustomerProfileService() {
    return getCustomerProfileService(context);
  }

  protected CustomerProfileService getCustomerProfileService(ServletContext ctx) {
    return ServiceLocator.getCustomerProfileService(ctx);
  }
}
