package com.brandonsramirez.customerProfileApi;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the JAX-RS end points
 */
public class TestCustomerResource {
  private CustomerProfileService service;
  private CustomerResource endpoint;

  @Before
  public void setUp() {
    service = CommonTestUtils.makeCustomerProfileService();
    endpoint = CommonTestUtils.makeCustomerResourceWithService(service);
    try {
      service.createCustomer(CommonTestUtils.makeCustomer("Bruce", "Wayne"));
    }
    catch (InvalidCustomerProfileException e) {
      throw new RuntimeException(e);  // not great error handling, but this shouldn't happen within a test like this.
    }
  }

  @Test
  public void getCustomerReturnsValidCustomer() {
    Customer c = endpoint.getCustomer(1);
    assertNotNull("Unable t find known-good customer profile.", c);
    assertEquals(1, c.getCustomerId());
    assertEquals("Bruce", c.getFirstName());
    assertEquals("Wayne", c.getLastName());
  }

  @Test
  public void getCustomerThrowsWebAppExceptionForNonExistentCustomer() {
    try {
      endpoint.getCustomer(2);
      fail("Finding a non-existent profile should have failed.");
    }
    catch (WebApplicationException e) {
      // excellent!
      // I'd like to validate the HTTP response status is 404 here, but that doesn't seem possible to extract from the exception object.
    }
  }

  @Test
  public void deleteRemovesCustomerFromPersistence() {
    endpoint.deleteCustomer(1);
    Customer customer = service.getCustomerById(1);
    assertNull(customer);
  }

  @Test
  public void deleteNonExistentCustomerThrowsWebAppException() {
    try {
      endpoint.deleteCustomer(321);
      fail("Able to delete a non-existent customer profile");
    }
    catch (WebApplicationException e) {
      // test passes
    }
  }
}
