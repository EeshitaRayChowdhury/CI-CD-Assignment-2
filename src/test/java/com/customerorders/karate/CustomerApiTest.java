package com.customerorders.karate;

import com.intuit.karate.junit5.Karate;

class CustomerApiTest {

    @Karate.Test
    Karate testCustomerApi() {
        return Karate.run("classpath:karate/customer.feature");
    }
}