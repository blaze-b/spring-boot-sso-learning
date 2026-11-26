## Spring Security SAML Sample with Spring Boot ##
This sample uses the plain old spring-security-saml library to add SP capabilities to a Spring Boot app, allowing it to authenticate against different IdPs.
The main purpose of this module is to expose the extensive configuration required to use Spring Security SAML, in comparison with the `spring-boot-security-saml` plugin for Spring Boot, that deals with all this complexities internally.

### Availabe IdPs ####

- [SSO Circle](http://www.ssocircle.com/en/)
- [OneLogin](https://www.onelogin.com/)
- [Ping One Clound](https://www.pingidentity.com/en/products/pingone.html)
- [OKTA](https://www.okta.com)

### Credentials ###

Use the following credentials:

- *SSO Circle:* Register with [SSO Circle](http://www.ssocircle.com/en/) and use those credentials to login in the application.
- *OneLogin:* Register with [OneLogin](https://www.onelogin.com/) and use those credentials to login in the application. 
- *Ping One:* Register with [Ping One Clound](https://www.pingidentity.com/en/products/pingone.html) and use those credentials to login in the application. 
- *OKTA:* Register with [OKTA](https://www.okta.com) and use those credentials to login in the application. 

### OneLogin configuration ###

To use OneLogin with this sample application, you'll have to:
- Create an [OneLogin developers account](https://www.onelogin.com/developer-signup)
- Add a SAML Test Connector (IdP)
- Configure the OneLogin application with:
  - *RelayState:* You can use anything here.
  - *Audience:* localhost-demo
  - *Recipient:* http://localhost:8080/saml/SSO
  - *ACS (Consumer) URL Validator:* ^http://localhost:8080/saml/SSO.*$
  - *ACS (Consumer) URL:* http://localhost:8080/saml/SSO
  - *Single Logout URL:* http://localhost:8080/saml/SingleLogout
  - *Parameters:* You can add additional parameters like firstName, lastName.
- In the SSO tab:
  - *X.509 Certificate:* Copy-paste the existing X.509 PEM cerficate into idp-onelogin.xml (ds:X509Certificate).
  - *SAML Signature algorythm:* Use the SHA-256, although SHA-1 will still work.
  - *Issuer URL:* Replace the entityID in the idp-onelogin.xml with this value.
  - *SAML 2.0 Endpoint (HTTP):* Replace the location for the HTTP-Redirect and HTTP-POST binding in the idp-onelogin.xml with this value.
  - *SLO Endpoint (HTTP):* Replace the location for the HTTP-Redirect binding in the idp-onelogin.xml with this value.

### Okta login configuration setup

To use okta with this sample applicatio, you'll have to:
- Create an [Okta developers account](https://www.okta.com)
- Add a sample saml connector details
- Configure the okta application with:
  - *Single Sign On URL* `http://localhost:8081/saml/SSO`
  - *Recipient URL* `http://localhost:8081/saml/SSO`
  - *Destination URL* `http://localhost:8081/saml/SSO`
  - *Audience Restriction* `http://localhost:8081/saml/metadata`
  - *Default Relay State* `Any value can be provided`
  - *Name ID Format* `Unspecified`
  - *Response* `Signed`
  - *Assertion Signature* `Signed`
  - *Signature Algorithm* `RSA_SHA256`
  - *Digest Algorithm* SHA256
  - *Assertion Encryption* Unencrypted
  - *Single Logout URL* `http://localhost:8081/saml/logout`
  - *SP Issuer* `http://localhost:8081/saml/metadata`
  - *Signature Certificate* Upload your locally generated `localhost.cert` (X509 PEM); see below
  - *Authentication context class* `X509 certificate`
  - *Honor Force Authentication* `No`
  - *SAML Issuer ID* `Default value`
  - *Paramters* Configure some paramters for display
- Final generated metadata file should be copy pasted in the idp-okta.xml
  
### Generate a local SAML SP key (do not commit)

The app loads `classpath:/saml/localhost.cert` and `classpath:/saml/localhost.key.der`. Private keys are gitignored — generate your own before running:

```bash
mkdir -p src/main/resources/saml
openssl genrsa -out src/main/resources/saml/localhost.key 2048
openssl req -new -x509 -key src/main/resources/saml/localhost.key \
  -out src/main/resources/saml/localhost.cert -days 365 \
  -subj "/CN=LOCAL"
openssl pkcs8 -topk8 -nocrypt -in src/main/resources/saml/localhost.key \
  -outform DER -out src/main/resources/saml/localhost.key.der
```

Upload `localhost.cert` to your IdP as the SP signature certificate. Never commit `localhost.key`, `localhost.key.der`, or `localhost.cert`.
