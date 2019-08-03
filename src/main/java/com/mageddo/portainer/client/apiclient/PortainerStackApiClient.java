package com.mageddo.portainer.client.apiclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mageddo.common.jackson.JsonUtils;
import com.mageddo.portainer.client.apiclient.vo.*;
import okhttp3.*;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class PortainerStackApiClient {

	private final OkHttpClient okHttpClient;
	private final HttpUrl baseUrl;

	public PortainerStackApiClient(OkHttpClient okHttpClient, HttpUrl baseUrl) {
		this.okHttpClient = okHttpClient;
		this.baseUrl = baseUrl;
	}

	public List<StackGetRestV1> findStacks(){
		try {
			final Response call = okHttpClient
				.newCall(
					new Request.Builder()
						.url(
							baseUrl
								.newBuilder()
								.addPathSegments("api/stacks")
								.build()
						)
						.build()
				)
				.execute();
			final String resBody = call.body().string();
			Validate.isTrue(call.isSuccessful(), resBody);
			return JsonUtils.instance().readValue(resBody, new TypeReference<List<StackGetRestV1>>(){});
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void createStack(StackCreateReqV1 createReqV1){
		Call call = okHttpClient
			.newCall(
				new Request.Builder()
				.url(
					baseUrl
						.newBuilder()
						.addEncodedPathSegments("api/stacks")
						.setQueryParameter("type", "1")
						.setQueryParameter("method", "string")
						.setQueryParameter("endpointId", "1")
						.build()
				)
				.post(RequestBody.create(
					MediaType.get("application/json"), JsonUtils.writeValueAsString(createReqV1)
				))
				.build()
			);
		try(Response res = call.execute()){
			Validate.isTrue(res.isSuccessful(), res.body().string());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public RequestRes updateStack(StackUpdateReqV1 updateReqV1){
		final String reqBody = JsonUtils.writeValueAsString(updateReqV1);
		final Call call = okHttpClient
		.newCall(
			new Request.Builder()
				.url(
					baseUrl
					.newBuilder()
					.addEncodedPathSegments("api/stacks")
					.addEncodedPathSegment(String.valueOf(updateReqV1.getId()))
					.setQueryParameter("endpointId", "1")
					.build()
				)
				.put(RequestBody.create(MediaType.get("application/json"), reqBody))
				.build()
		);
		try (Response res  = call.execute()){
			String resBody = res.body().string();
			Validate.isTrue(res.isSuccessful(), String.format("request: %s\n\n response: %s\n", reqBody, resBody));
			return RequestRes.valueOf(res, resBody);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public StackFileGetResV1 findStackContent(long stackId) {
		Call call = okHttpClient
			.newCall(
				new Request.Builder()
					.url(
						baseUrl
							.newBuilder()
							.addPathSegments("api/stacks")
							.addPathSegment(String.valueOf(stackId))
							.addPathSegment("file")
							.build()
					)
					.get()
					.build()
			);
		try(Response res = call.execute()){
			String body = res.body().string();
			Validate.isTrue(res.isSuccessful(), body);
			return JsonUtils.instance().readValue(body, StackFileGetResV1.class);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
