package tattoowebsite.tattobackend.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GoogleSheetsConfig {

    @Bean
    public Sheets sheetsService() throws IOException, GeneralSecurityException {
        // Lê o JSON de credenciais da variável de ambiente
        String credentialsJson = System.getenv("GOOGLE_CREDENTIALS");
        if (credentialsJson == null || credentialsJson.isEmpty()) {
            throw new IllegalStateException("Variável de ambiente GOOGLE_CREDENTIALS não configurada ou vazia.");
        }

        // Converte o JSON de credenciais em um stream
        GoogleCredential credential = GoogleCredential.fromStream(
                new ByteArrayInputStream(credentialsJson.getBytes()))
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
                .setApplicationName("TattooBackend")
                .build();
    }
}
