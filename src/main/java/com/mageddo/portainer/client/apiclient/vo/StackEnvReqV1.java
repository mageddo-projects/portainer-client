package com.mageddo.portainer.client.apiclient.vo;

import com.mageddo.portainer.client.vo.StackEnv;

import java.util.List;
import java.util.stream.Collectors;

public class StackEnvReqV1 {

	private String name;
	private String value;

	public static List<StackEnvReqV1> valueOf(List<StackEnv> envs) {
		return envs
			.stream()
			.map(it ->
				new StackEnvReqV1()
				.setName(it.getName())
				.setValue(it.getValue())
			)
			.collect(Collectors.toList())
		;
	}

	public String getName() {
		return name;
	}

	public StackEnvReqV1 setName(String name) {
		this.name = name;
		return this;
	}

	public String getValue() {
		return value;
	}

	public StackEnvReqV1 setValue(String value) {
		this.value = value;
		return this;
	}
}
