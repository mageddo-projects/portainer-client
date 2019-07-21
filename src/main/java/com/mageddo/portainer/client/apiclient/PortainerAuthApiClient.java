package com.mageddo.portainer.client.apiclient;

import com.mageddo.common.jackson.JsonUtils;
import com.mageddo.portainer.client.apiclient.vo.AuthReqV1;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

public class PortainerAuthApiClient {

	private final WebTarget webTarget;

	public PortainerAuthApiClient(WebTarget webTarget) {
		this.webTarget = webTarget;
	}

	public String doAuth(String username, String password){
		final Response res = webTarget
			.path("/api/auth")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(JsonUtils.writeValueAsString(
				new AuthReqV1()
				.setUsername(username)
				.setPassword(password)
			)));
		return JsonUtils
			.readTree(res.readEntity(InputStream.class))
			.at("/jwt")
			.asText()
		;
	}
}
