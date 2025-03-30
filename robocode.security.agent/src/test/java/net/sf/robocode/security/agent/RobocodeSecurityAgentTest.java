package net.sf.robocode.security.agent;

import org.junit.jupiter.api.Test;

import java.lang.instrument.Instrumentation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RobocodeSecurityAgentTest {

    /**
     * Tests for the `premain` method in the `RobocodeSecurityAgent` class.
     * <p>
     * The `premain` method is invoked when an instrumentation agent is loaded.
     * It installs a class transformer on `Thread.start()` method using ByteBuddy
     * to enforce security checks when new threads are started.
     */

    @Test
    void testPremainExecutesWithoutExceptions() {
        // Arrange: Mock the instrumentation instance
        Instrumentation instrumentationMock = mock(Instrumentation.class);

        // Act and Assert: Ensure premain completes without exception
        assertDoesNotThrow(() -> RobocodeSecurityAgent.premain(null, instrumentationMock));
    }

    @Test
    void testThreadAdviceAllowsSafeThreads() {
        // Arrange: Create a valid thread object
        Thread safeThread = mock(Thread.class);
        when(safeThread.getName()).thenReturn("RobotThread-Safe");

        // Act and Assert: Ensure no exception is thrown for safe threads
        RobocodeSecurityAgent.ThreadAdvice.onEnter(safeThread);
    }

    @Test
    void testThreadAdviceBlocksUnsafeThreads() {
        // Arrange: Create an invalid thread object
        Thread unsafeThread = mock(Thread.class);
        when(unsafeThread.getName()).thenReturn("UnsafeThread");

        // Act and Assert: Ensure SecurityException is thrown for unsafe threads
        assertThrows(SecurityException.class, () ->
                RobocodeSecurityAgent.ThreadAdvice.onEnter(unsafeThread)
        );
    }

    @Test
    void testThreadAdviceBlocksNullNamedThreads() {
        // Arrange: Create a thread with a null name
        Thread nullNameThread = mock(Thread.class);
        when(nullNameThread.getName()).thenReturn(null);

        // Act and Assert: Ensure SecurityException is thrown for null-named threads
        assertThrows(SecurityException.class, () ->
                RobocodeSecurityAgent.ThreadAdvice.onEnter(nullNameThread)
        );
    }
}