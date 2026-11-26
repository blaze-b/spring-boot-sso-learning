# spring-saml

SAML 2.0 **Service Provider** sample for [`spring-boot-sso-learning`](../README.md).

Built with **Java 21**, **Spring Boot 3.5**, and Spring Security’s built-in
`spring-security-saml2-service-provider` (not the deprecated `spring-security-saml` extension).

## Prerequisites

- JDK 21+
- An Identity Provider (Okta Developer, Keycloak, SimpleSAMLphp, …)
- `openssl` to generate a local SP keypair

## Setup

### 1. Generate a local SP keypair (do not commit)

Run from the **`spring-saml` module directory**:

```bash
mkdir -p local/saml
openssl genrsa -out local/saml/sp.key 2048
openssl req -new -x509 -key local/saml/sp.key -out local/saml/sp.crt -days 365 \
  -subj "/CN=localhost"
```

Files under `local/saml/` are gitignored.

### 2. Configure an IdP (Okta example)

1. Create an Okta developer account and add a **SAML 2.0** app.
2. Use these SP values (default port `8080`):

| Setting | Value |
| --- | --- |
| Single Sign-On URL (ACS) | `http://localhost:8080/login/saml2/sso/okta` |
| Audience URI (SP Entity ID) | `http://localhost:8080/saml2/metadata/okta` |
| Name ID format | EmailAddress (or Unspecified) |

3. Upload `local/saml/sp.crt` if your IdP requires an SP signing certificate.
4. Download IdP metadata and save it as `local/saml/idp-metadata.xml`.

### 3. Run

From the **repo root**:

```bash
./mvnw -pl spring-saml spring-boot:run
```

Or from this module (with the wrapper on your `PATH` / via root):

```bash
cd spring-saml
../mvnw spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080) → **Sign in with SAML**.

| Endpoint | Purpose |
| --- | --- |
| `/` | Public landing page |
| `/saml2/authenticate/okta` | Start SSO |
| `/saml2/metadata/okta` | SP metadata |
| `/home` | Assertion attributes (authenticated) |
| `/logout` | Logout |

## Configuration

Defaults are in `src/main/resources/application.yml`. Override with env vars:

| Variable | Default | Description |
| --- | --- | --- |
| `SERVER_PORT` | `8080` | HTTP port |
| `SAML_SP_PRIVATE_KEY` | `file:local/saml/sp.key` | SP private key |
| `SAML_SP_CERTIFICATE` | `file:local/saml/sp.crt` | SP certificate |
| `SAML_IDP_METADATA` | `file:local/saml/idp-metadata.xml` | IdP metadata URI |

```bash
export SAML_IDP_METADATA=https://your-tenant.okta.com/app/exk.../sso/saml/metadata
./mvnw -pl spring-saml spring-boot:run
```

The registration id (`okta`) is arbitrary — change it in `application.yml` and update IdP ACS/Audience URLs to match.

## Tests

From the repo root:

```bash
./mvnw -pl spring-saml test
```

The `test` profile uses classpath fixtures under `src/test/resources/saml/` (throwaway keypair + mock IdP metadata), so CI does not need a live IdP.
