package com.brandonsramirez.customerProfileApi;

import javax.ws.rs.*;
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
    Response res = endpoint.getCustomer(1);
    assertNotNull("Unable to find known-good customer profile.", res);
    assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());
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
  public void creatingValidCustomerPersists() {
    Customer c = CommonTestUtils.makeCustomer("Clark", "Kent");
    Response res = endpoint.createCustomer(c);

    assertNotNull(res);
    assertEquals(Response.Status.CREATED.getStatusCode(), res.getStatus());

    Customer persisted = service.getCustomerById(2);
    assertNotNull(persisted);
    assertEquals("Clark", persisted.getFirstName());
    assertEquals("Kent", persisted.getLastName());
  }

  @Test
  public void createInvalidCustomerThrowsWebAppException() {
    Customer c = CommonTestUtils.makeCustomer("Clark", null);
    Response res = endpoint.createCustomer(c);

    assertNotNull(res);
    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), res.getStatus());
  }

  @Test
  public void validUpdatePersists() {
    Customer revised = CommonTestUtils.makeCustomer("John", "Wayne");
    Response res = endpoint.updateCustomer(1, revised);

    assertNotNull(res);
    assertEquals(Response.Status.NO_CONTENT.getStatusCode(), res.getStatus());

    Customer persisted = service.getCustomerById(1);
    assertEquals("John", persisted.getFirstName());
  }

  @Test
  public void invalidateUpdateReturnsErrorCode() {
    Customer revised = CommonTestUtils.makeCustomer("John", "");
    Response res = endpoint.updateCustomer(1, revised);

    assertNotNull(res);
    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), res.getStatus());

    Customer persisted = service.getCustomerById(1);
    assertNotEquals("An invalid update went through.", "John", persisted.getFirstName());
  }

  @Test
  public void updatingNonExistentCustomerReturnsNotFoundError() {
    Customer revised = CommonTestUtils.makeCustomer("John", "Wayne");
    Response res = endpoint.updateCustomer(831, revised);

    assertNotNull(res);
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), res.getStatus());
  }

  @Test
  public void deleteRemovesCustomerFromPersistence() {
    Response res = endpoint.deleteCustomer(1);
    Customer customer = service.getCustomerById(1);

    assertNull(customer);
    assertNotNull(res);
    assertEquals(Response.Status.NO_CONTENT.getStatusCode(), res.getStatus());
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
