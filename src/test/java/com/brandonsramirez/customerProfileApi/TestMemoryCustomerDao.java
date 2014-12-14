package com.brandonsramirez.customerProfileApi;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Test;

public class TestMemoryCustomerDao {
  private Map<Integer, Customer> testCustomers = new HashMap<Integer, Customer>();

  private MemoryCustomerDao dao = new MemoryCustomerDao() {
    @Override
    protected Map<Integer, Customer> getCustomerMap() {
      return testCustomers;
    }
  };

  @After
  public void tearDown() {
    testCustomers.clear();
    dao.resetCustomerIdSequence();
  }

  @Test
  public void getCustomer() {
    Customer c1 = makeCustomer(1, "Brandon", "Ramirez");
    Customer c2 = makeCustomer(2, "John", "Doe");
    Customer c3 = makeCustomer(3, "Brandon", "Ramirez");

    testCustomers.put(c1.getCustomerId(), c1);
    testCustomers.put(c2.getCustomerId(), c2);
    testCustomers.put(c3.getCustomerId(), c3);

    Customer c = dao.getCustomer(1);
    assertNotNull(c);
    assertEquals(1, c.getCustomerId());
    assertEquals("Ramirez", c.getLastName());
    assertEquals("Brandon", c.getFirstName());
  }

  @Test
  public void createCustomer() {
    Customer customer = makeCustomer("Brandon", "Ramirez");
    int customerId = dao.createCustomer(customer);

    assertNotNull(testCustomers.get(1));
    assertEquals(1, testCustomers.get(1).getCustomerId());
    assertEquals("Ramirez", testCustomers.get(1).getLastName());
    assertEquals("Brandon", testCustomers.get(1).getFirstName());
  }

  @Test
  public void createDoesNotOverrideOriginalObject() {
    Customer c = makeCustomer("John", "Doe");
    int customerId = dao.createCustomer(c);
    assertNotNull(testCustomers.get(1));
    assertNotSame(1, c.getCustomerId());
  }

  @Test
  public void createReturnsIncrementingIds() {
    int firstId = dao.createCustomer(makeCustomer("Jane", "Doe"));
    int secondId = dao.createCustomer(makeCustomer("John", "Doe"));

    assertNotSame("createCustomer returned the same identifier for multiple customers", firstId, secondId);
    assertEquals("2 sequentially created customers don't have id's separated by 1.", firstId + 1, secondId);
  }

  @Test
  public void deleteRemovedCustomer() {
    Customer c = makeCustomer(5, "Super", "User");
    testCustomers.put(c.getCustomerId(), c);

    dao.deleteCustomer(c.getCustomerId());

    assertNull("After deleting a customer, the customer object remained.", testCustomers.get(c.getCustomerId()));
  }

  @Test
  public void updateModifiedCustomer() {
    Customer test = makeCustomer(6, "Test", "User");
    testCustomers.put(test.getCustomerId(), test);
    assertEquals("User", test.getLastName());

    dao.updateCustomer(makeCustomer(test.getCustomerId(), test.getFirstName(), "Suite"));
    assertEquals("Change via updateCustomer did not persist.", "Suite", testCustomers.get(test.getCustomerId()).getLastName());
  }

  @Test
  public void updateDoesNotChangePrimaryKey() {
    Customer test = makeCustomer(6, "Test", "User");
    testCustomers.put(test.getCustomerId(), test);
    dao.updateCustomer(makeCustomer(test.getCustomerId() + 3, test.getFirstName(), "Suite"));
    assertEquals("Primary key changed!", test.getCustomerId(), testCustomers.get(test.getCustomerId()).getCustomerId());
  }

  private Customer makeCustomer(int customerId, String firstName, String lastName) {
    Customer c = makeCustomer(firstName, lastName);
    c.setCustomerId(customerId);
    return c;
  }

  private Customer makeCustomer(String firstName, String lastName) {
    Customer c = new Customer();
    c.setFirstName(firstName);
    c.setLastName(lastName);
    return c;
  }
}
