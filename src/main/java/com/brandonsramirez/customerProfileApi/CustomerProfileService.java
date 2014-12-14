package com.brandonsramirez.customerProfileApi;

public class CustomerProfileService {
  private CustomerDao dao;

  public CustomerProfileService(CustomerDao dao) {
    this.dao = dao;
  }

  public Customer getCustomerById(int customerId) {
    return null;
  }
}
