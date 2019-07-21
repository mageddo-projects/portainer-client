package com.mageddo.portainer.client.apiclient.vo;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mageddo.portainer.client.vo.DockerStackDeploy;

import java.util.List;

public class StackUpdateReqV1 {

	private Long id;
	private String stackFileContent;
	private boolean prune;
	private List<StackEnvReqV1> env;

	public static StackUpdateReqV1 valueOf(DockerStackDeploy dockerStackDeploy, Long stackId) {
		return new StackUpdateReqV1()
			.setId(stackId)
			.setStackFileContent(dockerStackDeploy.getStackFileContent())
			.setEnv(StackEnvReqV1.valueOf(dockerStackDeploy.getEnvs()))
		;
	}

	@JsonIgnore
	public Long getId() {
		return id;
	}

	public StackUpdateReqV1 setId(Long id) {
		this.id = id;
		return this;
	}

	@JsonGetter("StackFileContent")
	public String getStackFileContent() {
		return stackFileContent;
	}

	public StackUpdateReqV1 setStackFileContent(String stackFileContent) {
		this.stackFileContent = stackFileContent;
		return this;
	}

	@JsonGetter("Prune")
	public boolean isPrune() {
		return prune;
	}

	public StackUpdateReqV1 setPrune(boolean prune) {
		this.prune = prune;
		return this;
	}

	public List<StackEnvReqV1> getEnv() {
		return env;
	}

	@JsonGetter("Env")
	public StackUpdateReqV1 setEnv(List<StackEnvReqV1> env) {
		this.env = env;
		return this;
	}
}
