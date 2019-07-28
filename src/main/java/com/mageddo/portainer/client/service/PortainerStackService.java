package com.mageddo.portainer.client.service;

import com.mageddo.portainer.client.apiclient.PortainerStackApiClient;
import com.mageddo.portainer.client.apiclient.vo.RequestRes;
import com.mageddo.portainer.client.apiclient.vo.StackCreateReqV1;
import com.mageddo.portainer.client.apiclient.vo.StackUpdateReqV1;
import com.mageddo.portainer.client.vo.DockerStack;
import com.mageddo.portainer.client.vo.DockerStackDeploy;
import com.mageddo.portainer.client.vo.StackEnv;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class PortainerStackService {

	private final PortainerStackApiClient portainerStackApiClient;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public PortainerStackService(PortainerStackApiClient portainerStackApiClient) {
		this.portainerStackApiClient = portainerStackApiClient;
	}

	public DockerStack findDockerStack(String name){
		return portainerStackApiClient.findStacks()
		.stream()
		.filter(it -> Objects.equals(it.getName(), name))
		.findFirst()
		.map(
			it -> new DockerStack()
			.setId(it.getId())
			.setEnvs(StackEnv.valueOf(it.getEnvs()))
		)
		.orElse(null)
		;
	}

	public void createOrUpdateStack(String name, Path stackFile, boolean prune, List<StackEnv> envs){
		try {
			createOrUpdateStack(
				new DockerStackDeploy()
				.setName(name)
				.setStackFileContent(IOUtils.toString(Files.newInputStream(stackFile), StandardCharsets.UTF_8))
				.setPrune(prune)
				.setEnvs(envs)
			);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void createOrUpdateStack(DockerStackDeploy dockerStackDeploy){
		final DockerStack dockerStack = findDockerStack(dockerStackDeploy.getName());
		if(dockerStack == null){
			logger.debug("status=creating-stack, stack={}", dockerStackDeploy.getName());
			portainerStackApiClient.createStack(StackCreateReqV1.valueOf(dockerStackDeploy));
		} else {
			final RequestRes res = portainerStackApiClient.updateStack(StackUpdateReqV1.valueOf(
				dockerStackDeploy, dockerStack.getId()
			));
			logger.debug("status=updating-stack, stack={}, res={}", dockerStackDeploy.getName(), res);
		}
	}

	public void runStack(String stackName, boolean prune, List<StackEnv> envs) {
		Validate.notNull(envs, "envs can't be null");
		DockerStack dockerStack = findDockerStack(stackName);
		Validate.notNull(dockerStack, "stack not found", stackName);
		createOrUpdateStack(
			new DockerStackDeploy()
			.setName(stackName)
			.setStackFileContent(findStackContent(dockerStack.getId()))
			.setPrune(prune)
			.setEnvs(StackEnv.merge(dockerStack.getEnvs(), envs))
		);
	}

	private String findStackContent(long stackId) {
		return portainerStackApiClient
			.findStackContent(stackId)
			.getStackFileContent()
		;
	}
}
