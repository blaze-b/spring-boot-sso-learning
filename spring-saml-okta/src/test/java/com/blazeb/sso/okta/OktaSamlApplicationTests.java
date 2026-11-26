package com.blazeb.sso.okta;

import com.blazeb.sso.testsupport.SamlTestMaterial;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OktaSamlApplicationTests {

    private static final SamlTestMaterial.Paths MATERIAL = SamlTestMaterial.generate(createTempDir());

    @DynamicPropertySource
    static void registerSamlProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.security.saml2.relyingparty.registration.okta.signing.credentials[0].private-key-location",
                () -> MATERIAL.spPrivateKey().toUri().toString()
        );
        registry.add(
                "spring.security.saml2.relyingparty.registration.okta.signing.credentials[0].certificate-location",
                () -> MATERIAL.spCertificate().toUri().toString()
        );
        registry.add(
                "spring.security.saml2.relyingparty.registration.okta.assertingparty.metadata-uri",
                () -> MATERIAL.idpMetadata().toUri().toString()
        );
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RelyingPartyRegistrationRepository relyingPartyRegistrationRepository;

    @Test
    void contextLoads() {
        assertThat(relyingPartyRegistrationRepository.findByRegistrationId("okta")).isNotNull();
    }

    @Test
    void indexIsPublic() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void homeRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection());
    }

    private static java.nio.file.Path createTempDir() {
        try {
            return Files.createTempDirectory("spring-saml-okta-test-");
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
