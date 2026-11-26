# spring-boot-sso-learning

A multi-module learning repo for **Single Sign-On with Spring Boot**.

Each module is a small, runnable showcase of one SSO approach. Shared Java / Spring Boot versions and plugin management live in the root aggregator POM so new modules stay consistent.

## Modules

| Module | Status | What it demonstrates |
| --- | --- | --- |
| [`spring-saml`](spring-saml/) | Ready | SAML 2.0 Service Provider using Spring Security’s built-in SAML2 support |
| `spring-oidc` | Planned | OAuth2 / OpenID Connect login |
| `spring-okta` | Planned | Okta-focused examples |
| `spring-keycloak` | Planned | Keycloak-focused examples |

## Prerequisites

- **JDK 21+**
- The Maven Wrapper (`./mvnw`) — no global Maven install required

## Build all modules

```bash
./mvnw verify
```

## Run a module

See the module README for setup (keys, IdP metadata, env vars). Example for SAML:

```bash
./mvnw -pl spring-saml spring-boot:run
```

## Project layout

```text
spring-boot-sso-learning/          # aggregator / parent POM
├── pom.xml
├── README.md
├── LICENSE
├── .github/workflows/ci.yml
├── mvnw
└── spring-saml/                   # SAML 2.0 SP sample
    ├── pom.xml
    ├── README.md
    ├── local/saml/                # gitignored developer credentials
    └── src/
```

## License

MIT — see [LICENSE](LICENSE).
