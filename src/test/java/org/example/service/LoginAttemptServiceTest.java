package org.example.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.servlet.http.HttpServletRequest;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LoginAttemptService.class})
class LoginAttemptServiceTest {

    @MockBean
    private HttpServletRequest request;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @BeforeEach
    public void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        loginAttemptService = new LoginAttemptService(request);
    }

    @Test
    public void shouldNotBeBlockedWhenLoginFailed() {
        loginAttemptService.loginFailed();
        assertFalse(loginAttemptService.isBlocked());
    }

    @Test
    public void shouldBeBlockedAfterThreeAttemptsWhenLoginFailed() {
        assertFalse(loginAttemptService.isBlocked());

        for (int i = 1; i < 3; i++) {
            loginAttemptService.loginFailed();
            assertFalse(loginAttemptService.isBlocked());
        }

        loginAttemptService.loginFailed();
        assertTrue(loginAttemptService.isBlocked());
    }

    @Test
    public void shouldNotBeBlockedAfterBanDurationTimePassWhenLoginFailed() throws InterruptedException {
        for (int i = 1; i < 3; i++) {
            loginAttemptService.loginFailed();
        }

        Thread.sleep((5 + 1) * 60 * 1000);

        assertFalse(loginAttemptService.isBlocked());
    }
}