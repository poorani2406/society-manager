package com.horizon.societymanager

import com.horizon.societymanager.core.session.SessionManager
import com.horizon.societymanager.data.model.UserRole
import com.horizon.societymanager.data.repository.AuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    private lateinit var sessionManager: SessionManager
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        sessionManager = SessionManager()
        authRepository = AuthRepository(sessionManager)
    }

    @Test
    fun login_withValidResidentCredentials_succeedsAndSetsSession() = runTest {
        val result = authRepository.login("resident@horizon.com", "resident123")

        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("Aarav Sharma", user?.name)
        assertEquals(UserRole.RESIDENT, user?.role)
        assertEquals("A-402", user?.flatNumber)
        assertTrue(sessionManager.isLoggedIn)
        assertTrue(sessionManager.isResident)
        assertFalse(sessionManager.isAdmin)
    }

    @Test
    fun login_withValidAdminCredentials_succeedsAndSetsAdminRole() = runTest {
        val result = authRepository.login("admin@horizon.com", "admin123")

        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals(UserRole.ADMIN, user?.role)
        assertTrue(sessionManager.isAdmin)
        assertFalse(sessionManager.isResident)
    }

    @Test
    fun login_withInvalidCredentials_fails() = runTest {
        val result = authRepository.login("invalid@horizon.com", "wrongpass")

        assertTrue(result.isFailure)
        assertFalse(sessionManager.isLoggedIn)
    }

    @Test
    fun logout_clearsUserSession() = runTest {
        authRepository.login("resident@horizon.com", "resident123")
        assertTrue(sessionManager.isLoggedIn)

        authRepository.logout()
        assertFalse(sessionManager.isLoggedIn)
        assertNull(sessionManager.currentUser.value)
    }
}
