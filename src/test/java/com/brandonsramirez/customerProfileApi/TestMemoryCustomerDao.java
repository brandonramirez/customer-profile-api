package com.brandonsramirez.customerProfileApi;

import java.util.HashMap;
import java.util.List;
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
    addInitialCustomersWithDups();

    Customer c = dao.getCustomer(1);
    assertNotNull(c);
    assertEquals(1, c.getCustomerId());
    assertEquals("Ramirez", c.getLastName());
    assertEquals("Brandon", c.getFirstName());
  }

  @Test
  public void createCustomer() {
    Customer customer = CommonTestUtils.makeCustomer("Brandon", "Ramirez");
    int customerId = dao.createCustomer(customer);

    assertNotNull(testCustomers.get(1));
    assertEquals(1, testCustomers.get(1).getCustomerId());
    assertEquals("Ramirez", testCustomers.get(1).getLastName());
    assertEquals("Brandon", testCustomers.get(1).getFirstName());
  }

  @Test
  public void createDoesNotOverrideOriginalObject() {
    Customer c = CommonTestUtils.makeCustomer("John", "Doe");
    int customerId = dao.createCustomer(c);
    assertNotNull(testCustomers.get(1));
    assertNotSame(1, c.getCustomerId());
  }

  @Test
  public void createReturnsIncrementingIds() {
    int firstId = dao.createCustomer(CommonTestUtils.makeCustomer("Jane", "Doe"));
    int secondId = dao.createCustomer(CommonTestUtils.makeCustomer("John", "Doe"));

    assertNotSame("createCustomer returned the same identifier for multiple customers", firstId, secondId);
    assertEquals("2 sequentially created customers don't have id's separated by 1.", firstId + 1, secondId);
  }

  @Test
  public void deleteRemovedCustomer() {
    Customer c = CommonTestUtils.makeCustomer(5, "Super", "User");
    testCustomers.put(c.getCustomerId(), c);

    dao.deleteCustomer(c.getCustomerId());

    assertNull("After deleting a customer, the customer object remained.", testCustomers.get(c.getCustomerId()));
  }

  @Test
  public void updateModifiedCustomer() {
    Customer test = CommonTestUtils.makeCustomer(6, "Test", "User");
    testCustomers.put(test.getCustomerId(), test);
    assertEquals("User", test.getLastName());

    dao.updateCustomer(CommonTestUtils.makeCustomer(test.getCustomerId(), test.getFirstName(), "Suite"));
    assertEquals("Change via updateCustomer did not persist.", "Suite", testCustomers.get(test.getCustomerId()).getLastName());
  }

  @Test
  public void updateDoesNotChangePrimaryKey() {
    Customer test = CommonTestUtils.makeCustomer(6, "Test", "User");
    testCustomers.put(test.getCustomerId(), test);
    dao.updateCustomer(CommonTestUtils.makeCustomer(test.getCustomerId() + 3, test.getFirstName(), "Suite"));
    assertEquals("Primary key changed!", test.getCustomerId(), testCustomers.get(test.getCustomerId()).getCustomerId());
  }

  @Test
  public void searchFindsFirstNameMatches() {
    addInitialCustomersWithDups();

    SearchResult<Customer> results = dao.findCustomers(SearchFilter.firstName("Brandon"), 0, 10);
    assertEquals("Expected two total hits", 2, results.getTotalCount());
    assertEquals("Expected two hits on page", 2, results.getResults().size());
  }

  @Test
  public void searchFindsLastNameMatches() {
    addInitialCustomersWithDups();

    SearchResult<Customer> results = dao.findCustomers(SearchFilter.lastName("Ramirez"), 0, 10);
    assertEquals("Expected two total hits", 2, results.getTotalCount());
    assertEquals("Expected two hits on page", 2, results.getResults().size());
  }

  @Test
  public void searchFindsEmailMatches() {
    addInitialCustomersWithDups();

    SearchResult<Customer> results = dao.findCustomers(SearchFilter.email("bsr@somefakedomain"), 0, 10);
    assertEquals("Expected two total hits", 2, results.getTotalCount());
    assertEquals("Expected two hits on page", 2, results.getResults().size());
  }

  @Test
  public void searchResultsOffset() {
    addInitialCustomersWithDups();

    SearchResult<Customer> results = dao.findCustomers(SearchFilter.blank(), 2, 1);
    assertEquals(3, results.getTotalCount());
    assertEquals(1, results.getResults().size());
    assertEquals("Ramirez", results.getResults().get(0).getLastName());
  }

  @Test
  public void searchResultsCap() {
    addInitialCustomersWithDups();

    SearchResult<Customer> results = dao.findCustomers(SearchFilter.lastName("Ramirez"), 0, 1);
    assertEquals(2, results.getTotalCount());
    assertEquals(1, results.getResults().size());
  }

  @Test
  public void searchResultsSortedByLastName() {
    addInitialCustomersWithDups();

    SearchResult<Customer> results = dao.findCustomers(SearchFilter.blank(), 0, 10);
    assertEquals("Doe", results.getResults().get(0).getLastName());
    assertEquals("Ramirez", results.getResults().get(1).getLastName());
    assertEquals("Ramirez", results.getResults().get(2).getLastName());
  }

  @Test
  public void duplicatesProperlyFlaggedAsSuch() {
    int firstCustomerId = dao.createCustomer(CommonTestUtils.makeCustomer("Brandon", "Ramirez"));
    int secondCustomerId = dao.createCustomer(CommonTestUtils.makeCustomer("The", "Batman"));
    int thirdCustomerId = dao.createCustomer(CommonTestUtils.makeCustomer("Brandon", "Ramirez"));

    Map<Integer, List<Customer>> duplicates = dao.findDuplicates();

    assertTrue(duplicates.containsKey(firstCustomerId));
    assertTrue(duplicates.containsKey(thirdCustomerId));

    List<Customer> dupsOfProfile1 = duplicates.get(firstCustomerId);
    List<Customer> dupsOfProfile3 = duplicates.get(thirdCustomerId);

    assertEquals(1, dupsOfProfile1.size());
    assertEquals(1, dupsOfProfile3.size());

    assertEquals(thirdCustomerId, dupsOfProfile1.get(0).getCustomerId());
    assertEquals(firstCustomerId, dupsOfProfile3.get(0).getCustomerId());
  }

  @Test
  public void nonDuplicatesFlaggedAsSuch() {
    int firstCustomerId = dao.createCustomer(CommonTestUtils.makeCustomer("Brandon", "Ramirez"));
    int secondCustomerId = dao.createCustomer(CommonTestUtils.makeCustomer("The", "Batman"));
    int thirdCustomerId = dao.createCustomer(CommonTestUtils.makeCustomer("Brandon", "Ramirez"));

    Map<Integer, List<Customer>> duplicates = dao.findDuplicates();

    assertTrue(duplicates.containsKey(secondCustomerId));
    List<Customer> dupsOfProfile2 = duplicates.get(secondCustomerId);
    assertEquals(0, dupsOfProfile2.size());
  }

  @Test
  public void deletingRemovesDuplicate() {
    int firstCustomerId = dao.createCustomer(CommonTestUtils.makeCustomer("Brandon", "Ramirez"));
    int secondCustomerId = dao.createCustomer(CommonTestUtils.makeCustomer("The", "Batman"));
    int thirdCustomerId = dao.createCustomer(CommonTestUtils.makeCustomer("Brandon", "Ramirez"));

    List<Customer> duplicates = dao.findDuplicatesOfProfile(dao.getCustomer(firstCustomerId));
    assertEquals(1, duplicates.size());

    dao.deleteCustomer(thirdCustomerId);

    duplicates = dao.findDuplicatesOfProfile(dao.getCustomer(firstCustomerId));
    assertEquals(0, duplicates.size());
  }

  @Test
  public void updatingRecalculatesDuplicates() {
    int firstCustomerId = dao.createCustomer(CommonTestUtils.makeCustomer("Brandon", "Ramirez"));
    int secondCustomerId = dao.createCustomer(CommonTestUtils.makeCustomer("The", "Batman"));
    int thirdCustomerId = dao.createCustomer(CommonTestUtils.makeCustomer("Brandon", "Ramirez"));

    Customer revised = CommonTestUtils.makeCustomer(3, "The", "Batman");
    dao.updateCustomer(revised);

    assertEquals(0, dao.findDuplicatesOfProfile(dao.getCustomer(firstCustomerId)).size());
    assertEquals(thirdCustomerId, dao.findDuplicatesOfProfile(dao.getCustomer(secondCustomerId)).get(0).getCustomerId());
  }

  private void addInitialCustomersWithDups() {
    Customer c1 = CommonTestUtils.makeCustomer(1, "Brandon", "Ramirez", "bsr@somefakedomain");
    Customer c2 = CommonTestUtils.makeCustomer(2, "John", "Doe", "jdoe@localhost");
    Customer c3 = CommonTestUtils.makeCustomer(3, "Brandon", "Ramirez", "bsr@somefakedomain");

    testCustomers.put(c1.getCustomerId(), c1);
    testCustomers.put(c2.getCustomerId(), c2);
    testCustomers.put(c3.getCustomerId(), c3);
  }
}
