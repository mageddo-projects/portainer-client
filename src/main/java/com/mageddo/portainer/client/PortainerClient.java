package com.mageddo.portainer.client;

import com.mageddo.portainer.client.utils.EnvUtils;
import nativeimage.Reflection;

@Reflection(
	scanPackage = "com.mageddo.portainer.client.apiclient.vo",
	declaredConstructors = true,
	declaredMethods = true,
	declaredFields = true
)
public final class PortainerClient {

	private PortainerClient() {}

	public static PortainerClient builder() {
		return new PortainerClient();
	}

	public PortainerClient auth(String username, String password) {
		EnvUtils.setUsername(username);
		EnvUtils.setPassword(password);
		return this;
	}

	public PortainerClient portainerApiUri(String serverURI){
		EnvUtils.setPortainerApiUri(serverURI);
		return this;
	}

	public PortainerClient insecureConnection(boolean insecureConnection){
		EnvUtils.setInsecureConnection(insecureConnection);
		return this;
	}

	public PortainerApiClient build() {
		return new PortainerApiClient();
	}
}
