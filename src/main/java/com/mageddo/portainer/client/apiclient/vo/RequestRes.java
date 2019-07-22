package com.mageddo.portainer.client.apiclient.vo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mageddo.common.jackson.JsonUtils;
import okhttp3.Response;

import java.io.UncheckedIOException;
import java.util.Objects;

public class RequestRes {

	private int status;
	private Object msg;

	public int getStatus() {
		return status;
	}

	public RequestRes setStatus(int status) {
		this.status = status;
		return this;
	}

	public Object getMsg() {
		return msg;
	}

	public RequestRes setMsg(Object msg) {
		this.msg = msg;
		return this;
	}

	@Override
	public String toString() {
		try {
			return JsonUtils.prettyInstance().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static RequestRes valueOf(Response response, String body){
		return new RequestRes()
			.setMsg(
				Objects.equals("application/json", response.header("Content-Type"))
					? JsonUtils.readTree(body)
					: body
			)
			.setStatus(response.code())
		;
	}
}
