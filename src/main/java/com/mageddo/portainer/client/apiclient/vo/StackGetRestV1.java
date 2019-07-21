package com.mageddo.portainer.client.apiclient.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StackGetRestV1 {

	private Long id;
	private String name;
	private List<StackEnvReqV1> envs;

	public Long getId() {
		return id;
	}

	@JsonSetter("Id")
	public StackGetRestV1 setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	@JsonSetter("Name")
	public StackGetRestV1 setName(String name) {
		this.name = name;
		return this;
	}

	public List<StackEnvReqV1> getEnvs() {
		return envs;
	}

	@JsonSetter("Env")
	public StackGetRestV1 setEnvs(List<StackEnvReqV1> envs) {
		this.envs = envs;
		return this;
	}
}
