package com.brandonsramirez.customerProfileApi;

public class CommonTestUtils {
  static Customer makeCustomer(int customerId, String firstName, String lastName) {
    Customer c = makeCustomer(firstName, lastName);
    c.setCustomerId(customerId);
    return c;
  }

  static Customer makeCustomer(String firstName, String lastName) {
    Customer c = new Customer();
    c.setFirstName(firstName);
    c.setLastName(lastName);
    return c;
  }
}
