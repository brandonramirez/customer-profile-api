package com.brandonsramirez.customerProfileApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

public class MemoryCustomerDao implements CustomerDao {
  private Map<Integer, Customer> customers = new ConcurrentHashMap<Integer, Customer>();
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
    return c.getCustomerId();
  }

  @Override
  public void updateCustomer(Customer customer) {
    Customer existingCustomer = getCustomerMap().get(customer.getCustomerId());
    if (existingCustomer != null) {
      existingCustomer.setFirstName(customer.getFirstName());
      existingCustomer.setLastName(customer.getLastName());
      existingCustomer.setEmail(customer.getEmail());
    }
  }

  @Override
  public void deleteCustomer(int customerId) {
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

  protected Map<Integer, Customer> getCustomerMap() {
    return customers;
  }

  private int getNextCustomerId() {
    return customerIdSequence.getAndIncrement();
  }

  protected void resetCustomerIdSequence() {
    customerIdSequence.set(1);
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
