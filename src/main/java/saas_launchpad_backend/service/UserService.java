package saas_launchpad_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import saas_launchpad_backend.dto.LoginRequestDTO;
import saas_launchpad_backend.dto.LoginResponseDTO;
import saas_launchpad_backend.dto.TenantResponseDTO;
import saas_launchpad_backend.dto.UserRequestDTO;
import saas_launchpad_backend.dto.UserResponseDTO;

import saas_launchpad_backend.entity.Tenant;
import saas_launchpad_backend.entity.User;

import saas_launchpad_backend.repository.TenantRepository;
import saas_launchpad_backend.repository.UserRepository;

import saas_launchpad_backend.security.JwtUtil;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            TenantRepository tenantRepository,
            JwtUtil jwtUtil) {

        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // ==========================================
    // REGISTER USER
    // ==========================================

    public UserResponseDTO saveUser(UserRequestDTO request) {

        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        String role = request.getRole();

        if (role == null || role.trim().isEmpty()) {
            role = "USER";
        }

        role = role.toUpperCase();

        user.setRole(role);

        // ADMIN users do not require a tenant.
        if ("ADMIN".equals(role)) {

            user.setTenant(null);

        } else {

            // Normal USER must belong to a tenant.
            if (request.getTenantId() == null) {
                throw new RuntimeException(
                        "Tenant ID is required for USER"
                );
            }

            Optional<Tenant> optionalTenant =
                    tenantRepository.findById(request.getTenantId());

            if (optionalTenant.isEmpty()) {
                throw new RuntimeException("Tenant not found");
            }

            user.setTenant(optionalTenant.get());
        }

        User savedUser = userRepository.save(user);

        UserResponseDTO response = new UserResponseDTO();

        response.setId(savedUser.getId());
        response.setFullName(savedUser.getFullName());
        response.setEmail(savedUser.getEmail());

        return response;
    }

    // ==========================================
    // GET USERS OF LOGGED-IN USER'S TENANT
    // ==========================================

    public List<UserResponseDTO> getUsersByTenant(String email) {

        Optional<User> loggedInUser =
                userRepository.findByEmail(email);

        if (loggedInUser.isEmpty()) {
            throw new RuntimeException("Logged-in user not found");
        }

        User currentUser = loggedInUser.get();

        if (currentUser.getTenant() == null) {
            throw new RuntimeException("User has no tenant");
        }

        Long tenantId =
                currentUser.getTenant().getId();

        List<User> users =
                userRepository.findByTenantId(tenantId);

        return users.stream()
                .map(user -> {

                    UserResponseDTO response =
                            new UserResponseDTO();

                    response.setId(user.getId());
                    response.setFullName(user.getFullName());
                    response.setEmail(user.getEmail());

                    return response;

                })
                .toList();
    }

    // ==========================================
    // GET USER BY ID
    // SAME TENANT ONLY
    // ==========================================

    public UserResponseDTO getUserByIdForTenant(
            Long id,
            String email) {

        Optional<User> loggedInUser =
                userRepository.findByEmail(email);

        if (loggedInUser.isEmpty()) {
            throw new RuntimeException("Logged-in user not found");
        }

        User currentUser = loggedInUser.get();

        if (currentUser.getTenant() == null) {
            throw new RuntimeException("User has no tenant");
        }

        Long tenantId =
                currentUser.getTenant().getId();

        Optional<User> optionalUser =
                userRepository.findByIdAndTenantId(
                        id,
                        tenantId
                );

        if (optionalUser.isEmpty()) {
            throw new RuntimeException(
                    "User not found in your tenant"
            );
        }

        User user = optionalUser.get();

        UserResponseDTO response =
                new UserResponseDTO();

        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());

        return response;
    }

    // ==========================================
    // DELETE USER
    // SAME TENANT ONLY
    // ==========================================

    public void deleteUserForTenant(
            Long id,
            String email) {

        Optional<User> loggedInUser =
                userRepository.findByEmail(email);

        if (loggedInUser.isEmpty()) {
            throw new RuntimeException("Logged-in user not found");
        }

        User currentUser = loggedInUser.get();

        if (currentUser.getTenant() == null) {
            throw new RuntimeException("User has no tenant");
        }

        Long tenantId =
                currentUser.getTenant().getId();

        Optional<User> optionalUser =
                userRepository.findByIdAndTenantId(
                        id,
                        tenantId
                );

        if (optionalUser.isEmpty()) {
            throw new RuntimeException(
                    "User not found in your tenant"
            );
        }

        userRepository.delete(optionalUser.get());
    }

    // ==========================================
    // GET LOGGED-IN USER'S TENANT
    // ==========================================

    public TenantResponseDTO getMyTenant(String email) {

        Optional<User> loggedInUser =
                userRepository.findByEmail(email);

        if (loggedInUser.isEmpty()) {
            throw new RuntimeException("Logged-in user not found");
        }

        User currentUser = loggedInUser.get();

        if (currentUser.getTenant() == null) {
            throw new RuntimeException("User has no tenant");
        }

        Tenant tenant = currentUser.getTenant();

        return new TenantResponseDTO(
                tenant.getId(),
                tenant.getName(),
                tenant.getSlug(),
                tenant.isActive()
        );
    }

    // ==========================================
    // LOGIN
    // ==========================================

    public LoginResponseDTO login(LoginRequestDTO request) {

        Optional<User> optionalUser =
                userRepository.findByEmail(
                        request.getEmail()
                );

        if (optionalUser.isEmpty()) {
            return new LoginResponseDTO("User not found");
        }

        User user = optionalUser.get();

        // Check password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return new LoginResponseDTO("Invalid password");
        }

        // Check whether user's tenant is active
        Tenant tenant = user.getTenant();

        if (tenant == null && !"ADMIN".equals(user.getRole())) {
            return new LoginResponseDTO("User has no tenant");
        }

        if (tenant != null && !tenant.isActive()) {
            return new LoginResponseDTO("Tenant is inactive");
        }

        // Generate JWT containing:
        // email
        // role
        String token =
                jwtUtil.generateToken(
                        user.getEmail(),
                        user.getRole()
                );

        return new LoginResponseDTO(
                "Login Successful",
                token
        );
    }
}