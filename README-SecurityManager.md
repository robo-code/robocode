# Robocode Security Implementation

## Java SecurityManager Deprecation and Removal

As of Java 17, the SecurityManager was deprecated for removal, and it has been fully removed in Java 24 (see [JEP 411](https://openjdk.org/jeps/411)). This document outlines how Robocode has adapted its security architecture to operate without relying on the SecurityManager.

## Robocode's Security Model

Robocode runs user-provided robot code in a sandbox to prevent malicious or buggy robots from harming the system. Previously, this was implemented using Java's SecurityManager, but we've now migrated to a custom security implementation that provides similar protections.

### Key Components of the New Security Implementation

1. **RobocodeSecurityPolicy**
   - Still used to provide permission checking for robot code
   - No longer relies on SecurityManager for enforcement

2. **SecurityUtil**
   - New utility class that provides thread and resource access checking
   - Enforces security constraints previously handled by SecurityManager

3. **RobotSecurityViolationError**
   - Custom error type to replace SecurityExceptions previously thrown by SecurityManager

### Main Security Features

- Thread access control
- Thread group management
- File system access restrictions
- Package access control
- Network access restrictions

## Implementation Details

The security implementation is enforced at the following key points:

1. **Thread Management**
   - Controls which threads can access other threads
   - Limits thread creation by robots

2. **File System Access**
   - Controls read/write/delete operations on files
   - Robots can only access their own directories

3. **Robot Interactions**
   - Prevents robots from interfering with each other
   - Blocks access to system resources

## Testing Security Features

We've maintained the security test suite to ensure robots cannot breach the sandbox. Tests include:

- AWT attack tests
- File system access tests
- Thread manipulation tests

## Future Considerations

With the removal of SecurityManager, we'll need to regularly review our security implementations and consider additional measures to protect against new types of attacks as Java evolves.

---

For more details, see the implementation in the `robocode.host.security` package.
