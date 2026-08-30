# Security Policy

## Mobile Security & Best Practices

The Vehicle Maintenance History mobile application adheres to the following security guidelines:

- **Local Storage Abstraction:** Sensitive access tokens and user credentials use token store abstractions (prepared for encrypted platform storage).
- **Secure Network Transport:** All external API communications require HTTPS network connections.
- **Automated CI Checks:** Code quality and build integrity checks run automatically on every pull request and push to `main`.

## Reporting a Vulnerability

If you discover a security vulnerability in this application, please do not report it in public GitHub issues. Send a private notification with steps to reproduce the issue and potential impact.

Reports will be acknowledged within 48 hours.
