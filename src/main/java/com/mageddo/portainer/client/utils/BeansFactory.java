package com.mageddo.portainer.client.utils;

import com.mageddo.portainer.client.apiclient.PortainerAuthApiClient;
import com.mageddo.portainer.client.apiclient.PortainerAuthenticationFilter;
import com.mageddo.portainer.client.apiclient.PortainerStackApiClient;
import com.mageddo.portainer.client.service.PortainerStackService;
import okhttp3.ConnectionPool;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import javax.net.ssl.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public final class BeansFactory {

	private BeansFactory() {
	}

	public static PortainerStackService newStackService() {
		HttpUrl baseUrl = HttpUrl.parse(EnvUtils.getPortainerApiUri());
		return new PortainerStackService(
			new PortainerStackApiClient(
				createClient()
				.newBuilder()
				.authenticator(
						new PortainerAuthenticationFilter(new PortainerAuthApiClient(
						createClient(), baseUrl
					))
				)
				.build(),
				baseUrl
			)
		);
	}

	public static OkHttpClient createClient() {
		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
			.connectTimeout(5, TimeUnit.SECONDS)
			.readTimeout(30, TimeUnit.SECONDS)
			.callTimeout(36, TimeUnit.SECONDS)
			.connectionPool(new ConnectionPool(1, 1, TimeUnit.MINUTES))
		;

		if(EnvUtils.insecureConnection()) {
			return createByPassSSLOkHttpClient(clientBuilder);
		} else {
			return clientBuilder.build();
		}
	}

	private static OkHttpClient createByPassSSLOkHttpClient(OkHttpClient.Builder clientBuilder) {
			try {
				final X509TrustManager trustAllCerts = new X509TrustManager() {
					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType) {}

					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType) {}

					@Override
					public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[]{}; }
				};

				final SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, new TrustManager[]{trustAllCerts}, new SecureRandom());

				return clientBuilder
				.sslSocketFactory(sslContext.getSocketFactory(), trustAllCerts)
				.hostnameVerifier((hostname, session) -> true)
				.build()
				;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
	}
}
