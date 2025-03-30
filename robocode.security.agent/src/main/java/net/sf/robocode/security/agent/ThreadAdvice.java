package net.sf.robocode.security.agent;

import net.bytebuddy.asm.Advice;

public class ThreadAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.This Thread thread) {
        if (!isThreadSafe(thread)) {
            throw new SecurityException(
                    "Unauthorized attempt to start a new thread: " + thread.getName());
        }
    }

    private static boolean isThreadSafe(Thread thread) {
        String threadName = thread.getName();
        return threadName != null && threadName.startsWith("RobotThread-");
    }
}