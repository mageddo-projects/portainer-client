package com.mageddo.portainer.client.vo;

import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.List;

public class DockerStackDeploy {

	private String name;
	private String stackFileContent;
	private boolean prune;
	private List<StackEnv> envs;

	public DockerStackDeploy() {
		this.envs = Collections.emptyList();
	}

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
		this.envs = Validate.notNull(envs, "envs can't be empty");
		return this;
	}

	public List<StackEnv> getEnvs() {
		return envs;
	}
}
