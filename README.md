# spring-boot-sso-learning

A multi-module learning repo for **Single Sign-On with Spring Boot**.

Each module is a small, runnable showcase of one SSO approach. Shared Java / Spring Boot versions and plugin management live in the root aggregator POM so new modules stay consistent.

## Modules

| Module | Status | What it demonstrates |
| --- | --- | --- |
| [`spring-saml`](spring-saml/) | Ready | Generic SAML 2.0 Service Provider using Spring Security’s built-in SAML2 support |
| [`spring-saml-okta`](spring-saml-okta/) | Ready | Okta-focused SAML 2.0 SP (imported from `okta-spring-boot-saml-example`, modernized) |
| `spring-oidc` | Planned | OAuth2 / OpenID Connect login |
| `spring-keycloak` | Planned | Keycloak-focused examples |

## Prerequisites

- **JDK 21+**
- The Maven Wrapper (`./mvnw`) — no global Maven install required

## Build all modules

```bash
./mvnw verify
```

## Run a module

See each module README for setup (keys, IdP metadata, env vars).

```bash
# Generic SAML SP (port 8080)
./mvnw -pl spring-saml spring-boot:run

# Okta-focused SAML SP (port 8081)
./mvnw -pl spring-saml-okta spring-boot:run
```

## Project layout

```text
spring-boot-sso-learning/          # aggregator / parent POM
├── pom.xml
├── README.md
├── LICENSE
├── .github/workflows/ci.yml
├── mvnw
├── sso-test-support/              # generates throwaway SAML keys for tests
├── spring-saml/                   # generic SAML 2.0 SP
└── spring-saml-okta/              # Okta SAML 2.0 SP
```

Private keys and certificates are never committed (including under `src/test`).
Tests generate throwaway material at runtime via `sso-test-support`.

## License

MIT — see [LICENSE](LICENSE).
