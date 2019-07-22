package com.mageddo.portainer.client.apiclient;

import com.mageddo.common.jackson.JsonUtils;
import com.mageddo.portainer.client.apiclient.vo.AuthReqV1;
import okhttp3.*;

import java.io.IOException;
import java.io.UncheckedIOException;

public class PortainerAuthApiClient {

	private final OkHttpClient okHttpClient;
	private final HttpUrl baseUrl;

	public PortainerAuthApiClient(OkHttpClient okHttpClient, HttpUrl baseUrl) {
		this.okHttpClient = okHttpClient;
		this.baseUrl = baseUrl;
	}

	public String doAuth(String username, String password){
		final Call call = okHttpClient
		.newCall(
			new Request.Builder()
			.url(
				baseUrl
				.newBuilder()
				.addPathSegments("api/auth")
				.build()
			)
			.post(RequestBody.create(
				MediaType.get("application/json"),
				JsonUtils.writeValueAsString(new AuthReqV1()
					.setUsername(username)
					.setPassword(password))
			))
			.build()
		);
		try(Response res = call.execute()){
			return JsonUtils
				.readTree(res.body().byteStream())
				.at("/jwt")
				.asText()
			;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
