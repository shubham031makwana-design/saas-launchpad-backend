package saas_launchpad_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String email;

    private String password;

    // Each USER can belong to one Tenant.
    // ADMIN users can have no tenant.
    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = true)
    private Tenant tenant;

    @Column(nullable = false)
    private String role = "USER";

    public User() {
    }

    // ==============================
    // ID
    // ==============================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // ==============================
    // FULL NAME
    // ==============================

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // ==============================
    // EMAIL
    // ==============================

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ==============================
    // PASSWORD
    // ==============================

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // ==============================
    // TENANT
    // ==============================

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    // ==============================
    // ROLE
    // ==============================

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}