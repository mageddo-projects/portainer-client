package com.mageddo.portainer.client.vo;

import java.util.List;

public class DockerStack {

	private Long id;
	private List<StackEnv> envs;

	public Long getId() {
		return id;
	}

	public DockerStack setId(Long id) {
		this.id = id;
		return this;
	}

	public DockerStack setEnvs(List<StackEnv> envs) {
		this.envs = envs;
		return this;
	}

	public List<StackEnv> getEnvs() {
		return envs;
	}
}
