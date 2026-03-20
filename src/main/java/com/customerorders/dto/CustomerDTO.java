package com.customerorders.dto;

public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
    private int totalOrders;

    public CustomerDTO() {}

    public CustomerDTO(Long id, String name, String email, int totalOrders) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.totalOrders = totalOrders;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getTotalOrders() { return totalOrders; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
}