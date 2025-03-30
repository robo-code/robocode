package net.sf.robocode.security.agent;

import org.junit.jupiter.api.Test;

import java.lang.instrument.Instrumentation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

class RobocodeSecurityAgentTest {

    @Test
    void testPremainExecutesWithoutExceptions() {
        // Arrange: Mock the instrumentation instance
        Instrumentation instrumentationMock = mock(Instrumentation.class);

        // Act and Assert: Ensure premain completes without exception
        assertDoesNotThrow(() -> RobocodeSecurityAgent.premain(null, instrumentationMock));
    }
}