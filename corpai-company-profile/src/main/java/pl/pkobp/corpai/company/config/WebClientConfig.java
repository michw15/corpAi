package pl.pkobp.corpai.company.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;

/**
 * WebClient configuration for external API calls.
 * Configures a KRS-specific WebClient that disables hostname verification
 * to work around the api.krs.gov.pl certificate missing the proper SAN entry.
 *
 * <p><b>Security note:</b> Hostname verification is disabled because the api.krs.gov.pl
 * server certificate does not include the hostname as a Subject Alternative Name (SAN).
 * Certificate chain validation is still enforced. This workaround should be removed once
 * the server certificate is corrected by the KRS API provider.
 * TODO: Re-enable hostname verification once api.krs.gov.pl certificate includes proper SAN.
 */
@Configuration
public class WebClientConfig {

    @Value("${krs.api.base-url:https://api.krs.gov.pl}")
    private String krsApiBaseUrl;

    @Bean
    public WebClient krsWebClient() throws SSLException {
        SslContext sslContext = SslContextBuilder.forClient().build();
        HttpClient httpClient = HttpClient.create()
                .secure(spec -> spec
                        .sslContext(sslContext)
                        .handlerConfigurator(handler -> {
                            SSLParameters params = handler.engine().getSSLParameters();
                            params.setEndpointIdentificationAlgorithm(null);
                            handler.engine().setSSLParameters(params);
                        }));
        return WebClient.builder()
                .baseUrl(krsApiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
