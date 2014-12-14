package com.brandonsramirez.customerProfileApi;

/**
 * Very lightweight service layer so that the REST layer does not have to
 * talk directly to the DAO.  Validation lives here.
 */
public class CustomerProfileService {
  private CustomerDao dao;

  public CustomerProfileService(CustomerDao dao) {
    this.dao = dao;
  }

  public Customer getCustomerById(int customerId) {
    return dao.getCustomer(customerId);
  }

  public int createCustomer(Customer customer) throws InvalidCustomerProfileException {
    validateCustomerProfile(customer);
    return dao.createCustomer(customer);
  }

  public void updateCustomer(Customer customer) throws NonExistentCustomerException, InvalidCustomerProfileException {
    validateCustomerProfile(customer);

    Customer existing = getCustomerById(customer.getCustomerId());
    if (existing == null) {
      throw new NonExistentCustomerException();
    }
    dao.updateCustomer(customer);
  }

  public void deleteCustomer(Customer c) throws NonExistentCustomerException {
    deleteCustomer(c.getCustomerId());
  }

  public void deleteCustomer(int customerId) throws NonExistentCustomerException {
    Customer existingCustomer = getCustomerById(customerId);
    if (existingCustomer == null) {
      throw new NonExistentCustomerException();
    }
    dao.deleteCustomer(customerId);
  }

  private static void validateCustomerProfile(Customer customer) throws InvalidCustomerProfileException {
    if (customer == null) {
      throw new InvalidCustomerProfileException("No customer specified.");
    }

    if (customer.getFirstName() == null || "".equals(customer.getFirstName().trim())) {
      throw new InvalidCustomerProfileException("First name is required.");
    }

    if (customer.getLastName() == null || "".equals(customer.getLastName().trim())) {
      throw new InvalidCustomerProfileException("Last name is required.");
    }
  }
}
