package com.mageddo.portainer.client.vo;

import java.util.List;

public class DockerStackDeploy {

	private String name;
	private String stackFileContent;
	private boolean prune;
	private List<StackEnv> envs;

	public String getName() {
		return name;
	}

	public DockerStackDeploy setName(String name) {
		this.name = name;
		return this;
	}

	public String getStackFileContent() {
		return stackFileContent;
	}

	public DockerStackDeploy setStackFileContent(String stackFileContent) {
		this.stackFileContent = stackFileContent;
		return this;
	}

	public boolean isPrune() {
		return prune;
	}

	public DockerStackDeploy setPrune(boolean prune) {
		this.prune = prune;
		return this;
	}

	public DockerStackDeploy setEnvs(List<StackEnv> envs) {
		this.envs = envs;
		return this;
	}

	public List<StackEnv> getEnvs() {
		return envs;
	}
}
