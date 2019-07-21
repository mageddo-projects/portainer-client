package com.mageddo.portainer.client.utils;

import com.mageddo.common.resteasy.RestEasy;
import com.mageddo.portainer.client.apiclient.PortainerAuthApiClient;
import com.mageddo.portainer.client.apiclient.PortainerAuthenticationFilter;
import com.mageddo.portainer.client.apiclient.PortainerStackApiClient;
import com.mageddo.portainer.client.service.PortainerStackService;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.ws.rs.client.Client;

public final class BeansFactory {

	private BeansFactory() {
	}

	public static PortainerStackService newStackService() {
		return new PortainerStackService(
			new PortainerStackApiClient(
				createClient()
					.register(new PortainerAuthenticationFilter(new PortainerAuthApiClient(
						createClient().target(EnvUtils.getPortainerApiUri())
					)))
					.target(EnvUtils.getPortainerApiUri())
			)
		);
	}

	public static Client createClient() {

		HttpClientBuilder clientBuilder = HttpClientBuilder
			.create()
			.setDefaultRequestConfig(
				RequestConfig.custom()
					.setConnectionRequestTimeout(30_000)
					.setConnectTimeout(5000)
					.setSocketTimeout(36_000)
					.setRedirectsEnabled(false)
					.build()
			)
			.setMaxConnTotal(1)
			.setMaxConnPerRoute(1)
			;

		if(EnvUtils.insecureConnection()){
			clientBuilder
				.setSSLContext(RestEasy.createFakeSSLContext())
				.setSSLHostnameVerifier((a,b) -> true)
			;
		}

		return RestEasy.newRestEasyBuilder(clientBuilder).build();
	}
}
