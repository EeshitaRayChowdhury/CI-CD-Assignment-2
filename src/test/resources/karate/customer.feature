Feature: Customer API

Scenario: Create customer

Given url 'http://localhost:8081/customers'
And request { name: 'Test User', email: 'test@example.com' }
When method post
Then status 201
And match response.name == 'Test User'