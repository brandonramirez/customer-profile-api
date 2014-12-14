package com.brandonsramirez.customerProfileApi;

import javax.servlet.ServletContext;

public class ServiceLocator {
  private static final String CONTEXT_ATTRIBUTE_NAME = "customerProfile.service";

  @SuppressWarnings("unchecked")
  public static CustomerProfileService getCustomerProfileService(ServletContext ctx) {
    if (ctx.getAttribute(CONTEXT_ATTRIBUTE_NAME) == null) {
      ctx.setAttribute(CONTEXT_ATTRIBUTE_NAME, new CustomerProfileService(new MemoryCustomerDao()));
    }
    return (CustomerProfileService) ctx.getAttribute(CONTEXT_ATTRIBUTE_NAME);
  }
}
