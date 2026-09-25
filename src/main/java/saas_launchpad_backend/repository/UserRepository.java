package saas_launchpad_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import saas_launchpad_backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email
    Optional<User> findByEmail(String email);

    // Find all users belonging to a specific tenant
    List<User> findByTenantId(Long tenantId);

    // Find a specific user only if they belong to the specified tenant
    Optional<User> findByIdAndTenantId(Long id, Long tenantId);
}