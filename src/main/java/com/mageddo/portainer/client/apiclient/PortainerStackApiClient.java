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
			return JsonUtils.instance().readValue(
				okHttpClient
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
				.execute()
				.body()
				.byteStream(),
				new TypeReference<List<StackGetRestV1>>(){}
			);
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
		Call call = okHttpClient
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
				.put(RequestBody.create(MediaType.get("application/json"), JsonUtils.writeValueAsString(updateReqV1)))
				.build()
		);
		try (Response res  = call.execute()){
			Validate.isTrue(res.isSuccessful(), res.body().string());
			return RequestRes.valueOf(res, res.body().string());
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
			Validate.isTrue(res.isSuccessful(), res.body().string());
			return JsonUtils.readValue(res.body().byteStream(), StackFileGetResV1.class);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
