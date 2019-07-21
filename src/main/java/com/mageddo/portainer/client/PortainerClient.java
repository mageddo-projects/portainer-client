package com.mageddo.portainer.client;

public final class PortainerClient {

	public static AuthorizedPortainerClient auth(String username, String password){
		System.setProperty("portainer.auth.username", username);
		System.setProperty("portainer.auth.password", password);
		return new AuthorizedPortainerClient();
	}

}
