package com.mageddo.portainer.client.apiclient.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthReqV1 {

	@JsonProperty("Username")
	private String username;

	@JsonProperty("Password")
	private String password;

	public String getUsername() {
		return username;
	}

	public AuthReqV1 setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public AuthReqV1 setPassword(String password) {
		this.password = password;
		return this;
	}
}
