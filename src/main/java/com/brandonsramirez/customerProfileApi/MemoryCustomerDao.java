package com.brandonsramirez.customerProfileApi;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

public class MemoryCustomerDao implements CustomerDao {
  private Map<Integer, Customer> customers = new ConcurrentHashMap<Integer, Customer>();
  private Map<Integer, List<Customer>> duplicates = new ConcurrentHashMap<Integer, List<Customer>>();
  private AtomicInteger customerIdSequence = new AtomicInteger(1);

  @Override
  public Customer getCustomer(int customerId) {
    return getCustomerMap().get(customerId);
  }

  @Override
  @SuppressWarnings("unchecked")
  public int createCustomer(Customer customer) {
    Customer c = (Customer) customer.clone();
    c.setCustomerId(getNextCustomerId());
    getCustomerMap().put(c.getCustomerId(), c);

    indexDuplicates(c);

    return c.getCustomerId();
  }

  @Override
  public void updateCustomer(Customer customer) {
    Customer existingCustomer = getCustomerMap().get(customer.getCustomerId());
    if (existingCustomer != null) {
      unindexDuplicates(existingCustomer);

      existingCustomer.setFirstName(customer.getFirstName());
      existingCustomer.setLastName(customer.getLastName());
      existingCustomer.setEmail(customer.getEmail());

      indexDuplicates(existingCustomer);
    }
  }

  @Override
  public void deleteCustomer(int customerId) {
    Customer customer = getCustomerMap().get(customerId);
    if (customer != null) {
      unindexDuplicates(customer);
    }
    getCustomerMap().remove(customerId);
  }

  @Override
  public SearchResult<Customer> findCustomers(SearchFilter criteria, int offset, int max) {
    Collection<Customer> hits = Collections2.filter(getCustomerMap().values(), predicateFromSearchFilter(criteria));
    List<Customer> sortedHits = new ArrayList<Customer>(hits);
    Collections.sort(sortedHits, LAST_NAME_FIRST_COMPARATOR);

    SearchResult<Customer> results = new SearchResult<Customer>();
    results.setTotalCount(hits.size());
    results.setResults(Collections.unmodifiableList(sortedHits.subList(offset, Math.min(offset + max, sortedHits.size()))));
    return results;
  }

  @Override
  public Map<Integer, List<Customer>> findDuplicates() {
    Map<Integer, List<Customer>> dups = new HashMap<Integer, List<Customer>>();
    for (Customer customer : getCustomerMap().values()) {
      dups.put(customer.getCustomerId(), findDuplicatesOfProfile(customer));
    }

    return Collections.unmodifiableMap(dups);
  }

  @Override
  public List<Customer> findDuplicatesOfProfile(Customer customer) {
    // linked list is used so that we can rapidly add, then delete ourselves without re-shuffling an array.
    List<Customer> potentialDups = new LinkedList<Customer>();

    // Duplicates by name
    int dupKeyByName = (customer.getFirstName() + " " + customer.getLastName()).hashCode();
    potentialDups.addAll(duplicates.get(dupKeyByName));

    // Duplicates by email, if supplied
    if (customer.getEmail() != null) {
      int dupKeyByEmail = customer.getEmail().hashCode();
      potentialDups.addAll(duplicates.get(dupKeyByEmail));
    }

    potentialDups.remove(customer);
    return Collections.unmodifiableList(potentialDups);
  }

  protected Map<Integer, Customer> getCustomerMap() {
    return customers;
  }

  private int getNextCustomerId() {
    return customerIdSequence.getAndIncrement();
  }

  protected void resetCustomerIdSequence() {
    customerIdSequence.set(1);
  }

  private void indexDuplicates(Customer c) {
    int dupKeyByName = (c.getFirstName() + " " + c.getLastName()).hashCode();

    if (!duplicates.containsKey(dupKeyByName)) {
      duplicates.put(dupKeyByName, new ArrayList<Customer>(5));
    }
    duplicates.get(dupKeyByName).add(c);

    if (c.getEmail() != null) {
      int dupKeyByEmail = c.getEmail().hashCode();
      if (!duplicates.containsKey(dupKeyByEmail)) {
        duplicates.put(dupKeyByEmail, new ArrayList<Customer>(5));
      }
      duplicates.get(dupKeyByEmail).add(c);
    }
  }

  private void unindexDuplicates(Customer customer) {
    int dupKeyByName = (customer.getFirstName() + " " + customer.getLastName()).hashCode();
    if (duplicates.containsKey(dupKeyByName)) {
      duplicates.get(dupKeyByName).remove(customer);
    }
    if (customer.getEmail() != null) {
      int dupKeyByEmail = customer.getEmail().hashCode();
      if (duplicates.containsKey(dupKeyByEmail)) {
        duplicates.get(dupKeyByEmail).remove(customer);
      }
    }
  }

  private Predicate<Customer> predicateFromSearchFilter(final SearchFilter criteria) {
    if (criteria.isBlank()) {
      return Predicates.alwaysTrue();
    }

    return new Predicate<Customer>() {
      @Override
      public boolean apply(Customer input) {
        return (!criteria.hasFirstName() || criteria.getFirstName().equals(input.getFirstName())) &&
          (!criteria.hasLastName() || criteria.getLastName().equals(input.getLastName())) &&
            (!criteria.hasEmail() || criteria.getEmail().equals(input.getEmail()));
      }
    };
  }

  private static final Comparator<Customer> LAST_NAME_FIRST_COMPARATOR = new Comparator<Customer>() {
    public int compare(Customer c1, Customer c2) {
      return c1.getLastName().compareTo(c2.getLastName());
    }
  };
}
