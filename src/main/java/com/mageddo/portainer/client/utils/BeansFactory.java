package com.mageddo.portainer.client.utils;

import com.mageddo.portainer.client.apiclient.PortainerAuthApiClient;
import com.mageddo.portainer.client.apiclient.PortainerAuthenticationFilter;
import com.mageddo.portainer.client.apiclient.PortainerStackApiClient;
import com.mageddo.portainer.client.service.PortainerStackService;
import okhttp3.ConnectionPool;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.security.NoSuchAlgorithmException;
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

		if(EnvUtils.insecureConnection()){
			try {
				clientBuilder
					.hostnameVerifier((a, b) -> true)
					.sslSocketFactory( SSLContext.getDefault().getSocketFactory(), new X509TrustManager() {
						public void checkClientTrusted(X509Certificate[] arg0, String arg1) {}
						public void checkServerTrusted(X509Certificate[] arg0, String arg1) {}
						public X509Certificate[] getAcceptedIssuers() {
							return new X509Certificate[0];
						}
					})
				;
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}

		return clientBuilder.build();
	}
}
