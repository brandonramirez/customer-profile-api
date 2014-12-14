package com.brandonsramirez.customerProfileApi;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

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

  protected Map<Integer, Customer> getCustomerMap() {
    return customers;
  }

  private int getNextCustomerId() {
    return customerIdSequence.getAndIncrement();
  }

  protected void resetCustomerIdSequence() {
    customerIdSequence.set(1);
  }
}
