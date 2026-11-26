package com.blazeb.sso.testsupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Generates throwaway SP keys and mock IdP metadata for tests via openssl.
 * Nothing written here should ever be committed.
 */
public final class SamlTestMaterial {

    public record Paths(Path spPrivateKey, Path spCertificate, Path idpMetadata) {
    }

    private SamlTestMaterial() {
    }

    public static Paths generate(Path directory) {
        try {
            Files.createDirectories(directory);

            Path spKey = directory.resolve("sp.key");
            Path spCert = directory.resolve("sp.crt");
            Path idpKey = directory.resolve("idp.key");
            Path idpCert = directory.resolve("idp.crt");
            Path metadata = directory.resolve("idp-metadata.xml");

            run("openssl", "genrsa", "-out", spKey.toString(), "2048");
            run("openssl", "req", "-new", "-x509", "-key", spKey.toString(),
                    "-out", spCert.toString(), "-days", "3650", "-subj", "/CN=sso-learning-test-sp");

            run("openssl", "genrsa", "-out", idpKey.toString(), "2048");
            run("openssl", "req", "-new", "-x509", "-key", idpKey.toString(),
                    "-out", idpCert.toString(), "-days", "3650", "-subj", "/CN=sso-learning-test-idp");

            String certBody = Files.readString(idpCert, StandardCharsets.UTF_8)
                    .replace("-----BEGIN CERTIFICATE-----", "")
                    .replace("-----END CERTIFICATE-----", "")
                    .replaceAll("\\s+", "");

            Files.writeString(metadata, mockIdpMetadata(certBody), StandardCharsets.UTF_8);

            // Drop the IdP private key immediately; tests only need the public cert in metadata
            Files.deleteIfExists(idpKey);

            return new Paths(spKey, spCert, metadata);
        }
        catch (Exception ex) {
            throw new IllegalStateException(
                    "Failed to generate SAML test material (is openssl installed?)", ex);
        }
    }

    private static void run(String... command) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
        String output;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            output = reader.lines().collect(Collectors.joining("\n"));
        }
        if (!process.waitFor(60, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new IllegalStateException("Timed out running: " + String.join(" ", command));
        }
        if (process.exitValue() != 0) {
            throw new IllegalStateException(
                    "Command failed (" + process.exitValue() + "): "
                            + String.join(" ", command) + "\n" + output);
        }
    }

    private static String mockIdpMetadata(String base64Certificate) {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <md:EntityDescriptor xmlns:md="urn:oasis:names:tc:SAML:2.0:metadata"
                                     entityID="http://localhost/mock-idp">
                  <md:IDPSSODescriptor WantAuthnRequestsSigned="false"
                                       protocolSupportEnumeration="urn:oasis:names:tc:SAML:2.0:protocol">
                    <md:KeyDescriptor use="signing">
                      <ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
                        <ds:X509Data>
                          <ds:X509Certificate>%s</ds:X509Certificate>
                        </ds:X509Data>
                      </ds:KeyInfo>
                    </md:KeyDescriptor>
                    <md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress</md:NameIDFormat>
                    <md:SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect"
                                            Location="http://localhost/mock-idp/sso"/>
                    <md:SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"
                                            Location="http://localhost/mock-idp/sso"/>
                    <md:SingleLogoutService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect"
                                            Location="http://localhost/mock-idp/slo"/>
                  </md:IDPSSODescriptor>
                </md:EntityDescriptor>
                """.formatted(base64Certificate);
    }
}
