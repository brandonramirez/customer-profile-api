package com.brandonsramirez.customerProfileApi;

public interface CustomerDao {
  public Customer getCustomer(int customerId);
  public int createCustomer(Customer customer);
  public void updateCustomer(Customer customer);
  public void deleteCustomer(int customerId);
  public SearchResult<Customer> findCustomers(SearchFilter criteria, int offset, int max);
}
