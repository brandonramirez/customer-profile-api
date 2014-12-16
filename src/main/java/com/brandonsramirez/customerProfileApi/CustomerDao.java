package com.brandonsramirez.customerProfileApi;

import java.util.List;
import java.util.Map;

public interface CustomerDao {
  public Customer getCustomer(int customerId);
  public int createCustomer(Customer customer);
  public void updateCustomer(Customer customer);
  public void deleteCustomer(int customerId);

  public SearchResult<Customer> findCustomers(SearchFilter criteria, int offset, int max);
  public Map<Integer, List<Customer>> findDuplicates();
  public List<Customer> findDuplicatesOfProfile(Customer customer);
}
