package net.sf.robocode.security.agent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ThreadAdviceTest {

    @Test
    void testThreadAdviceAllowsSafeThreads() {
        // Arrange: Create a valid thread object
        Thread safeThread = mock(Thread.class);
        when(safeThread.getName()).thenReturn("RobotThread-Safe");

        // Act and Assert: Ensure no exception is thrown for safe threads
        ThreadAdvice.onEnter(safeThread);
    }

    @Test
    void testThreadAdviceBlocksUnsafeThreads() {
        // Arrange: Create an invalid thread object
        Thread unsafeThread = mock(Thread.class);
        when(unsafeThread.getName()).thenReturn("UnsafeThread");

        // Act and Assert: Ensure SecurityException is thrown for unsafe threads
        assertThrows(SecurityException.class, () ->
                ThreadAdvice.onEnter(unsafeThread)
        );
    }

    @Test
    void testThreadAdviceBlocksNullNamedThreads() {
        // Arrange: Create a thread with a null name
        Thread nullNameThread = mock(Thread.class);
        when(nullNameThread.getName()).thenReturn(null);

        // Act and Assert: Ensure SecurityException is thrown for null-named threads
        assertThrows(SecurityException.class, () ->
                ThreadAdvice.onEnter(nullNameThread)
        );
    }
}
