package com.mageddo.portainer.client.apiclient.vo;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.mageddo.portainer.client.vo.DockerStackDeploy;

import java.util.List;

public class StackCreateReqV1 {

	private String name;
	private String stackFileContent;
	private List<StackEnvReqV1> env;

	public static StackCreateReqV1 valueOf(DockerStackDeploy dockerStackDeploy) {
		return new StackCreateReqV1()
			.setName(dockerStackDeploy.getName())
			.setStackFileContent(dockerStackDeploy.getStackFileContent())
			.setEnv(StackEnvReqV1.valueOf(dockerStackDeploy.getEnvs()))
		;
	}

	@JsonGetter("Name")
	public String getName() {
		return name;
	}

	public StackCreateReqV1 setName(String name) {
		this.name = name;
		return this;
	}

	@JsonGetter("SwarmID")
	public String getSwarmId() {
		return "null";
	}

	@JsonGetter("StackFileContent")
	public String getStackFileContent() {
		return stackFileContent;
	}

	public StackCreateReqV1 setStackFileContent(String stackFileContent) {
		this.stackFileContent = stackFileContent;
		return this;
	}

	@JsonGetter("Env")
	public List<StackEnvReqV1> getEnv() {
		return env;
	}

	public StackCreateReqV1 setEnv(List<StackEnvReqV1> env) {
		this.env = env;
		return this;
	}
}
