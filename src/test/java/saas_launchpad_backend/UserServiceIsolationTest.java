package saas_launchpad_backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import saas_launchpad_backend.entity.Tenant;
import saas_launchpad_backend.entity.User;
import saas_launchpad_backend.repository.TenantRepository;
import saas_launchpad_backend.repository.UserRepository;
import saas_launchpad_backend.security.JwtUtil;
import saas_launchpad_backend.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceIsolationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private Tenant tenantOne;
    private Tenant tenantTwo;
    private User userOne;
    private User userTwo;

    @BeforeEach
    void setUp() {

        tenantOne = new Tenant();
        tenantOne.setId(52L);
        tenantOne.setName("Tenant One");
        tenantOne.setSlug("tenant-one");
        tenantOne.setActive(true);

        tenantTwo = new Tenant();
        tenantTwo.setId(2L);
        tenantTwo.setName("ABC Technologies");
        tenantTwo.setSlug("abc-technologies");
        tenantTwo.setActive(true);

        userOne = new User();
        userOne.setId(100L);
        userOne.setFullName("Tenant One User");
        userOne.setEmail("tenant1.user@gmail.com");
        userOne.setTenant(tenantOne);

        userTwo = new User();
        userTwo.setId(200L);
        userTwo.setFullName("Tenant Two User");
        userTwo.setEmail("rahul.tenant2@gmail.com");
        userTwo.setTenant(tenantTwo);
    }

    @Test
    void userCannotAccessAnotherTenantUser() {

        when(userRepository.findByEmail("tenant1.user@gmail.com"))
                .thenReturn(Optional.of(userOne));

        when(userRepository.findByIdAndTenantId(200L, 52L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> userService.getUserByIdForTenant(
                        200L,
                        "tenant1.user@gmail.com")
        );
    }

    @Test
    void userCanAccessUserFromSameTenant() {

        User sameTenantUser = new User();
        sameTenantUser.setId(101L);
        sameTenantUser.setFullName("Another Tenant One User");
        sameTenantUser.setEmail("another@tenantone.com");
        sameTenantUser.setTenant(tenantOne);

        when(userRepository.findByEmail("tenant1.user@gmail.com"))
                .thenReturn(Optional.of(userOne));

        when(userRepository.findByIdAndTenantId(101L, 52L))
                .thenReturn(Optional.of(sameTenantUser));

        var result =
                userService.getUserByIdForTenant(
                        101L,
                        "tenant1.user@gmail.com");

        assertEquals(101L, result.getId());
        assertEquals(
                "another@tenantone.com",
                result.getEmail());
    }
}