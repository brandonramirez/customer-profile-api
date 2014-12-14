package com.brandonsramirez.customerProfileApi;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class TestCustomerProfileService {
  private CustomerProfileService service;

  @Before
  public void setUp() {
    this.service = new CustomerProfileService(new MemoryCustomerDao() {
      // Over-ride the in-memory map with our own map so that we have some control
      // over the underlying data store.
      private Map<Integer, Customer> customers = new HashMap<Integer, Customer>() {{
        put(1, CommonTestUtils.makeCustomer(1, "Brandon", "Ramirez"));
        put(2, CommonTestUtils.makeCustomer(2, "John", "Doe"));
      }};

      @Override
      protected Map<Integer, Customer> getCustomerMap() {
        return customers;
      }
    });
  }

  @Test
  public void getCustomerByIdFindsAnItem() {
    Customer c = service.getCustomerById(1);
    assertNotNull("Unable to locate customer id 1.", c);
    assertEquals("ID does not match", 1, c.getCustomerId());
  }

  @Test
  public void createCustomerPersists() {
    Customer newCustomer = CommonTestUtils.makeCustomer("Willy", "Wonka");
    try {
      int newId = service.createCustomer(newCustomer);

      Customer persistedCustomer = service.getCustomerById(newId);

      assertNotNull("Newly created customer account was lost.", persistedCustomer);
      assertEquals(newCustomer.getFirstName(), persistedCustomer.getFirstName());
      assertEquals(newCustomer.getLastName(), persistedCustomer.getLastName());
    }
    catch (InvalidCustomerProfileException e) {
      fail("A customer profile with a first and last name should not have resulted in an exception.");
    }
  }

  @Test
  public void exceptionRaisedCreatingNullCustomer() {
    creationShouldFail(null, "Creating a null customer did not throw an exception.");
  }

  @Test
  public void cannotCreateCustomerWithoutFirstName() {
    Customer c = new Customer();
    c.setLastName("Ramirez");
    creationShouldFail(c, "Creating a customer without a first name did not throw an exception.");
  }

  @Test
  public void cannotCreateCustomerWithoutLastName() {
    Customer c = new Customer();
    c.setFirstName("Jane");
    creationShouldFail(c, "Creating a customer without a last name did not throw an exception.");
  }

  @Test
  public void deletingCustomerRemovesFromPersistence() {
    try {
      service.deleteCustomer(2);
    }
    catch (NonExistentCustomerException e) {
      fail("Known-good customer reported as missing when trying to delete then.");
    }

    Customer c = service.getCustomerById(2);
    assertNull("Customer still retrievable after deleting them.", c);
  }

  @Test
  public void deletingNonExistentCustomerThrowsException() {
    try {
      service.deleteCustomer(371);
      fail("Deleting a customer that does not exist should have thrown an error.");
    }
    catch (NonExistentCustomerException e) {
      // test good
    }
  }

  @Test
  public void updatingCustomerPersists() {
    Customer revisedCustomer = CommonTestUtils.makeCustomer(2, "Jane", "Doe");

    try {
      service.updateCustomer(revisedCustomer);
    }
    catch (NonExistentCustomerException e) {
      fail("Updating a known-good customer profile threw an exception indicating the customer does not exist.");
    }
    catch (InvalidCustomerProfileException e) {
      fail("A perfectly good customer profile failed validation.");
    }

    Customer afterUpdate = service.getCustomerById(2);
    assertNotNull(afterUpdate);
    assertEquals(2, afterUpdate.getCustomerId());
    assertEquals("Jane", afterUpdate.getFirstName());
    assertEquals("Doe", afterUpdate.getLastName());
  }

  @Test
  public void updatingNonExistentCustomerThrowsException() {
    Customer revisedCustomer = CommonTestUtils.makeCustomer(691, "Bruce", "Wayne");
    try {
      service.updateCustomer(revisedCustomer);
      fail("Trying to modify a customer that does not exist appears to succeed.");
    }
    catch (NonExistentCustomerException e) {
      // good test
    }
    catch (InvalidCustomerProfileException e) {
      fail("Failed validation on a good profile.");
    }
  }

  @Test
  public void updatingNullCustomerThrowsException() {
    updateShouldFail(null, "Able to remove a user's first name.");
  }

  @Test
  public void updatingCustomerWithoutFirstNameThrowsException() {
    Customer revisedCustomer = CommonTestUtils.makeCustomer(2, null, "Robin");
    updateShouldFail(revisedCustomer, "Able to remove a user's first name.");
  }

  @Test
  public void updatingCustomerWithoutLastNameThrowsException() {
    Customer revisedCustomer = CommonTestUtils.makeCustomer(2, "Batman", null);
    updateShouldFail(revisedCustomer, "Able to remove a user's last name.");
  }

  private void creationShouldFail(Customer c, String error) {
    try {
      service.createCustomer(c);
      fail(error);
    }
    catch (InvalidCustomerProfileException e) {
      // test is good
    }
  }

  private void updateShouldFail(Customer c, String error) {
    try {
      service.updateCustomer(c);
      fail(error);
    }
    catch (InvalidCustomerProfileException e) {
      // test is good
    }
    catch (NonExistentCustomerException e) {
      fail("Known-good customer not found when updating.");
    }
  }
}
