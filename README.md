# Spring Boot SSO Learning

Hands-on chapters for Single Sign-On with Spring Boot — each module is a small, runnable Service Provider you can build, configure, and debug.

[![CI](https://github.com/blaze-b/spring-boot-sso-learning/actions/workflows/ci.yml/badge.svg)](https://github.com/blaze-b/spring-boot-sso-learning/actions/workflows/ci.yml)

## Chapters

| Chapter | Title | Module / Path | Summary |
| --- | --- | --- | --- |
| 0 | [SSO And SAML Basics](docs/chapter-0-sso-and-saml-basics.md) | notes | Terms, roles, and the SAML browser login flow before you touch code |
| 1 | [Spring SAML](spring-saml/README.md) | `spring-saml` | Generic SAML 2.0 SP with Spring Security’s built-in SAML2 support |
| 2 | [Spring SAML With Okta](spring-saml-okta/README.md) | `spring-saml-okta` | Same stack, focused on Okta app integration and metadata URL setup |

Planned later: OpenID Connect / OAuth2 login, Keycloak-focused examples.

## Prerequisites

- **Java 21+**
- **Maven Wrapper** (`./mvnw`) — no global Maven install required
- `openssl` when you run a module against a real IdP (local SP keypair)
- An Identity Provider for live login (Okta Developer, Keycloak, SimpleSAMLphp, …)

## Project Layout

```text
spring-boot-sso-learning/
├── pom.xml                      # aggregator / parent (Boot 3.5, Java 21)
├── README.md                    # this book overview
├── docs/
│   └── chapter-0-sso-and-saml-basics.md
├── sso-test-support/            # generates throwaway SAML keys for tests
├── spring-saml/                 # Chapter 1
└── spring-saml-okta/            # Chapter 2
```

## Quick Start

### Build All Modules

```bash
./mvnw verify
```

### Run A Chapter Module

```bash
# Chapter 1 — generic SAML SP (port 8080)
./mvnw -pl spring-saml spring-boot:run

# Chapter 2 — Okta-focused SAML SP (port 8081)
./mvnw -pl spring-saml-okta spring-boot:run
```

Each chapter README covers generating a local SP keypair, pointing at IdP metadata, and the ACS / Entity ID values to paste into your IdP.

### Run Tests

```bash
./mvnw test
# or one chapter:
./mvnw -pl spring-saml,sso-test-support test
```

## How Test Keys Are Generated

Private keys and certificates are **never committed** (including under `src/test`). Root `.gitignore` blocks `*.key`, `*.crt`, `*.pem`, `*.p12`, `*.jks`, `*.pfx`, and similar patterns everywhere.

CI and unit tests use [`sso-test-support`](sso-test-support/) to create a throwaway SP keypair and mock IdP metadata in a temp directory at runtime (via `openssl`). See `@DynamicPropertySource` in each module’s tests.

When you run an app locally, generate your own material under that module’s `local/saml/` (gitignored).

## How To Add A New SSO Chapter

1. Create a sibling Maven module (for example `spring-oidc`) and add it to the root `pom.xml` `<modules>` list.
2. Inherit the parent POM (Java 21 / Spring Boot versions stay centralized).
3. Add a chapter README titled `# Chapter N: Title Case Title` with Previous/Next links.
4. Add a row to the **Chapters** table above.
5. Keep secrets out of git; reuse `sso-test-support` for smoke tests when SAML-like credentials are needed.
6. Ensure `./mvnw verify` and the existing CI (including gitleaks) stay green.

## Conventions

- **Book titles**: root README is `# Spring Boot SSO Learning` (repo name as words, each capitalised).
- **Chapter titles**: `# Chapter N: Title Case Title` in each chapter README.
- **Headings**: Title Case throughout.
- **Links**: relative paths between chapters and the root overview.
- **Config**: properties + env vars with placeholders only — no real tenant hosts or secrets in docs.
- **Keys**: generate locally or at test runtime; never commit keystores or PEM material.

## License

MIT — see [LICENSE](LICENSE).
