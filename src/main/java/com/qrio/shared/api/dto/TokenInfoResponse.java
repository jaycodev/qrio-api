package com.qrio.shared.api.dto;

import java.util.Date;

public class TokenInfoResponse {
    private String subject;
    private String role;
    private Long customerId;
    private String email;
    private String name;
    private Date issuedAt;
    private Date expiration;

    public TokenInfoResponse() {}

    public TokenInfoResponse(String subject, String role, Long customerId, String email, String name, Date issuedAt, Date expiration) {
        this.subject = subject;
        this.role = role;
        this.customerId = customerId;
        this.email = email;
        this.name = name;
        this.issuedAt = issuedAt;
        this.expiration = expiration;
    }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Date getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Date issuedAt) { this.issuedAt = issuedAt; }
    public Date getExpiration() { return expiration; }
    public void setExpiration(Date expiration) { this.expiration = expiration; }
}
