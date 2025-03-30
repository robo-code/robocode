package net.sf.robocode.security.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.isSubTypeOf;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class RobocodeSecurityAgent {

    /**
     * Premain method for the Java agent. This is triggered when the agent is loaded before the application.
     *
     * @param agentArgs       Command-line args if needed during agent startup.
     * @param instrumentation Provides tools for class instrumentation at runtime.
     */
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("Robocode Security Agent Loaded!");

        // Add security management hooks here
        setupClassTransformations(instrumentation);
    }

    private static void setupClassTransformations(Instrumentation instrumentation) {
        threadAgentBuilder().installOn(instrumentation);
    }

    private static AgentBuilder threadAgentBuilder() {
        return new AgentBuilder.Default()
                .type(isSubTypeOf(Thread.class)) // Apply to the "Thread" class
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.method(named("start")) // Hook into Thread.start() calls
                                .intercept( // Intercept calls to enforce sandboxing rules
                                        Advice.to(ThreadAdvice.class)
                                )
                );
    }
}