package com.brandonsramirez.customerProfileApi;

public class NonExistentCustomerException extends Exception {
  public NonExistentCustomerException() {
    super();
  }

  public NonExistentCustomerException(String msg) {
    super(msg);
  }

  public NonExistentCustomerException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public NonExistentCustomerException(Throwable cause) {
    super(cause);
  }
}