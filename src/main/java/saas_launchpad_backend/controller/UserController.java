package saas_launchpad_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import saas_launchpad_backend.dto.LoginRequestDTO;
import saas_launchpad_backend.dto.LoginResponseDTO;
import saas_launchpad_backend.dto.UserRequestDTO;
import saas_launchpad_backend.dto.UserResponseDTO;
import saas_launchpad_backend.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ==============================
    // REGISTER
    // ==============================

    @PostMapping("/register")
    public UserResponseDTO register(
            @Valid @RequestBody UserRequestDTO request) {

        return userService.saveUser(request);
    }

    // ==============================
    // GET USERS OF SAME TENANT ONLY
    // ==============================

    @GetMapping
    public List<UserResponseDTO> getAllUsers(
            Authentication authentication) {

        String email = authentication.getName();

        return userService.getUsersByTenant(email);
    }

    // ==============================
    // GET USER BY ID
    // SAME TENANT ONLY
    // ==============================

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        return userService.getUserByIdForTenant(id, email);
    }

    // ==============================
    // DELETE USER
    // SAME TENANT ONLY
    // ==============================

    @DeleteMapping("/{id}")
    public String deleteUser(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        userService.deleteUserForTenant(id, email);

        return "User deleted successfully";
    }

    // ==============================
    // LOGIN
    // ==============================

    @PostMapping("/login")
    public LoginResponseDTO login(
            @Valid @RequestBody LoginRequestDTO request) {

        return userService.login(request);
    }
    // ==============================
// GET LOGGED-IN USER'S TENANT
// ==============================

@GetMapping("/me/tenant")
public saas_launchpad_backend.dto.TenantResponseDTO getMyTenant(
        Authentication authentication) {

    String email = authentication.getName();

    return userService.getMyTenant(email);
}
}