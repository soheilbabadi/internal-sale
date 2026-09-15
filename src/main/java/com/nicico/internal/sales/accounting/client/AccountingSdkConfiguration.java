package com.nicico.internal.sales.accounting.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgostar.accounting.sdk.client.DetailClient;
import com.fgostar.accounting.sdk.factory.AccountingApiClientFactory;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AccountingSdkConfiguration {

    @Value("${accounting.api.base-url:http://devapp01.icico.net.ir/accounting-backend/}")
    private String accountingApiBaseUrl;

    @Value("${spring.security.oauth2.client.provider.oserver.token-uri:http://devapp01.icico.net.ir/oauth/token}")
    private String accessTokenUri;

    @Value("${spring.security.oauth2.client.registration.oserver.client-id:${spring.application.name}}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.oserver.client-secret:password}")
    private String clientSecret;

    @Value("${spring.application.name:internal-sales}")
    private String appName;

    @Value("${nicico.security.sysPassword:password}")
    private String sysPassword;

    /**
     * Creates the Authenticator bean for system user authentication.
     */
    @Bean
    public PasswordGrantAuthenticator passwordGrantAuthenticator() {
        return new PasswordGrantAuthenticator(
                accessTokenUri,
                clientId,
                clientSecret,
                String.format("sys_%s", appName),
                sysPassword
        );
    }

    /**
     * Creates OkHttpClient configured for Accounting SDK calls with system token interceptor and authenticator.
     */
    @Bean(name = "accountingOkHttpClient")
    public OkHttpClient accountingOkHttpClient(PasswordGrantAuthenticator authenticator) {
        return new OkHttpClient.Builder()
                .addInterceptor(new PasswordGrantInterceptor(authenticator))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .sslSocketFactory(getSslSocketFactory(), getTrustAllCerts())
                .authenticator(authenticator)
                .build();
    }

    private X509TrustManager getTrustAllCerts() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    private SSLSocketFactory getSslSocketFactory() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new InternalSaleCustomException.ApplicationServerException(
                    "accounting.sdk.ssl.configuration.failed: " + e.getMessage());
        }
    }

    /**
     * Creates the AccountingApiClientFactory bean wrapping the Retrofit instance.
     */
    @Bean
    public AccountingApiClientFactory accountingApiClientFactory(
            @Qualifier("accountingOkHttpClient") OkHttpClient accountingOkHttpClient) {
        ObjectMapper objectMapper = AccountingApiClientFactory.createDefaultObjectMapper();
        String normalizedBaseUrl = accountingApiBaseUrl.endsWith("/") ? accountingApiBaseUrl : accountingApiBaseUrl + "/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(normalizedBaseUrl)
                .client(accountingOkHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        return new AccountingApiClientFactory(retrofit);
    }

    /**
     * Exposes DetailClient bean for dependency injection across services.
     */
    @Bean
    public DetailClient detailClient(AccountingApiClientFactory factory) {
        return factory.createDetailClient();
    }

    /**
     * Exposes ContactClient bean for dependency injection across services.
     */
    @Bean
    public com.fgostar.accounting.sdk.client.ContactClient contactClient(AccountingApiClientFactory factory) {
        return factory.createContactClient();
    }
}
