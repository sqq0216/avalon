package org.eea.tcf.listener.feign;

import feign.Client;
import feign.RequestInterceptor;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.eea.tcf.listener.config.ConfigurationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

@Configuration
public class FeignConfiguration {

    private ConfigurationService confService;

    public FeignConfiguration(ConfigurationService confService) {
        this.confService = confService;
    }

    private SSLContext getSslContext() {
        try {
            return new SSLContextBuilder().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Content-Type", (Iterable) null);
            requestTemplate.header("Content-Type", "application/json");
        };
    }

    @Bean
    public Client feignClient() {
        SSLSocketFactory socketFactory = null;
        if (getSslContext() != null) {
            socketFactory = getSslContext().getSocketFactory();
        }
        return new Client.Default(socketFactory, new NoopHostnameVerifier());
    }

    @Bean
    public RestTemplate restTemplate() {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        setProxy(clientBuilder);
        setSslContext(clientBuilder);
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(clientBuilder.build());
        return new RestTemplate(factory);
    }

    /*
     * TODO
     * Set multiple proxies
     * Use HttpRoutePlanner to support both http & https proxies at the same time
     * https://stackoverflow.com/a/34432952
     * */
    private void setProxy(HttpClientBuilder clientBuilder) {
        HttpHost proxy = null;
        if (confService.getHttpsProxyHost() != null && confService.getHttpsProxyPort() != null) {
            proxy = new HttpHost(confService.getHttpsProxyHost(), confService.getHttpsProxyPort(), "https");
        } else if (confService.getHttpProxyHost() != null && confService.getHttpProxyPort() != null) {
            proxy = new HttpHost(confService.getHttpProxyHost(), confService.getHttpProxyPort(), "http");
        }
        if (proxy != null){
            clientBuilder.setProxy(proxy);
        }
    }

    private void setSslContext(HttpClientBuilder clientBuilder) {
        SSLContext sslContext = getSslContext();
        if (sslContext != null) {
            clientBuilder.setSSLContext(sslContext);
        }
    }

}
