# spring-saml-okta

Okta-focused SAML 2.0 **Service Provider** module for [`spring-boot-sso-learning`](../README.md).

Originally imported from
[`blaze-b/okta-spring-boot-saml-example`](https://github.com/blaze-b/okta-spring-boot-saml-example)
(2020 Okta blog sample). It now uses **Java 21**, **Spring Boot 3.5**, and Spring Security’s
built-in SAML2 support instead of the deprecated SAML DSL / OpenSAML 2 stack.

## Prerequisites

- JDK 21+
- An [Okta Developer](https://developer.okta.com/) account
- `openssl` to generate a local SP keypair

## Setup

### 1. Generate a local SP keypair (do not commit)

From the **`spring-saml-okta` module directory**:

```bash
mkdir -p local/saml
openssl genrsa -out local/saml/sp.key 2048
openssl req -new -x509 -key local/saml/sp.key -out local/saml/sp.crt -days 365 \
  -subj "/CN=localhost"
```

Files under `local/saml/` are gitignored. There is **no** committed `keystore.jks` and no default passwords.

### 2. Create a SAML 2.0 app in Okta

1. Sign in to the [Okta Admin Console](https://developer.okta.com/).
2. **Applications → Applications → Create App Integration**.
3. Choose **SAML 2.0** and create the app.
4. Configure SAML settings (default app port is `8081`):

| Setting | Value |
| --- | --- |
| Single sign-on URL (ACS) | `http://localhost:8081/login/saml2/sso/okta` |
| Audience URI (SP Entity ID) | `http://localhost:8081/saml2/metadata/okta` |
| Name ID format | EmailAddress (or Unspecified) |

5. Optionally upload `local/saml/sp.crt` as the SP signing certificate.
6. Finish the wizard, then open the app’s **Sign On** tab and copy the
   **Identity Provider metadata** URL.
7. Assign yourself (or a test user) to the application under **Assignments**.

Historical screenshots from the original Okta tutorial live in
[`docs/screenshots/`](docs/screenshots/) (UI labels may differ in today’s Okta console).

### 3. Point the app at your Okta metadata

Either export an env var:

```bash
export OKTA_METADATA_URL='https://YOUR_OKTA_DOMAIN/app/exk.../sso/saml/metadata'
```

Or download the metadata XML to `local/saml/idp-metadata.xml` (the default file path).

### 4. Run

From the **repo root**:

```bash
./mvnw -pl spring-saml-okta spring-boot:run
```

Open [http://localhost:8081](http://localhost:8081) → **Sign in with Okta**.

| Endpoint | Purpose |
| --- | --- |
| `/` | Public landing page |
| `/saml2/authenticate/okta` | Start SSO |
| `/saml2/metadata/okta` | SP metadata (paste into Okta if needed) |
| `/home` | Assertion attributes (authenticated) |
| `/logout` | Logout |

## Configuration

| Variable | Default | Description |
| --- | --- | --- |
| `SERVER_PORT` | `8081` | HTTP port |
| `OKTA_METADATA_URL` | (falls back below) | Okta IdP metadata URL |
| `SAML_IDP_METADATA` | `file:local/saml/idp-metadata.xml` | Fallback metadata URI |
| `SAML_SP_PRIVATE_KEY` | `file:local/saml/sp.key` | SP private key |
| `SAML_SP_CERTIFICATE` | `file:local/saml/sp.crt` | SP certificate |

Example:

```bash
export OKTA_METADATA_URL='https://dev-123456.okta.com/app/exk.../sso/saml/metadata'
./mvnw -pl spring-saml-okta spring-boot:run
```

## Tests

```bash
./mvnw -pl spring-saml-okta test
```

Uses the `test` profile. Throwaway SP keys and mock IdP metadata are generated
at test runtime by `sso-test-support` (nothing committed under `src/test/resources`).

## How this differs from `spring-saml`

| | `spring-saml` | `spring-saml-okta` |
| --- | --- | --- |
| Focus | Generic SAML2 SP | Okta app integration walkthrough |
| Default port | `8080` | `8081` |
| Metadata env | `SAML_IDP_METADATA` | Prefer `OKTA_METADATA_URL` |
