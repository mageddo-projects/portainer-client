package com.mageddo.portainer.client.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mageddo.portainer.client.apiclient.PortainerStackApiClient;
import com.mageddo.portainer.client.apiclient.vo.RequestRes;
import com.mageddo.portainer.client.apiclient.vo.StackCreateReqV1;
import com.mageddo.portainer.client.apiclient.vo.StackUpdateReqV1;
import com.mageddo.portainer.client.vo.DockerStack;
import com.mageddo.portainer.client.vo.DockerStackDeploy;
import com.mageddo.portainer.client.vo.StackEnv;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.UUID;

import static com.mageddo.portainer.client.utils.YamlUtils.getYamlInstance;

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

	/**
	 * Run stack cloning existent services this way you can run the same service multiple times in parallel,
	 * useful when you are using the stack as a task runner
	 */
	public void runStackClonningServices(String stackName, boolean prune, List<StackEnv> envs){
		Validate.notNull(envs, "envs can't be null");
		DockerStack dockerStack = mustFindStack(stackName);
		createOrUpdateStack(
			new DockerStackDeploy()
				.setName(stackName)
				.setStackFileContent(String.valueOf(createTempServices(findStackContent(dockerStack.getId()))))
				.setPrune(prune)
				.setEnvs(StackEnv.merge(dockerStack.getEnvs(), envs))
		);
	}

	public void runStack(String stackName, boolean prune, List<StackEnv> envs) {
		Validate.notNull(envs, "envs can't be null");
		DockerStack dockerStack = mustFindStack(stackName);
		createOrUpdateStack(
			new DockerStackDeploy()
			.setName(stackName)
			.setStackFileContent(findStackContent(dockerStack.getId()))
			.setPrune(prune)
			.setEnvs(StackEnv.merge(dockerStack.getEnvs(), envs))
		);
	}

	private DockerStack mustFindStack(String stackName) {
		DockerStack dockerStack = findDockerStack(stackName);
		Validate.notNull(dockerStack, "stack not found", stackName);
		return dockerStack;
	}

	private String findStackContent(long stackId) {
		return portainerStackApiClient
			.findStackContent(stackId)
			.getStackFileContent()
		;
	}


	JsonNode createTempServices(final String composeFileContent) {
		return createTempServices(composeFileContent, create8DigitsHash());
	}

	JsonNode createTempServices(final String composeFileContent, final String hash) {
		try {
			return createTempServices(getYamlInstance().readTree(composeFileContent), hash);
		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	JsonNode createTempServices(JsonNode composeFileNode) {
		return createTempServices(composeFileNode, create8DigitsHash());
	}

	JsonNode createTempServices(JsonNode composeFileNode, final String hash) {
		final ObjectNode services = (ObjectNode) composeFileNode.at("/services");
		services
			.fields()
			.forEachRemaining(it -> {
				services.remove(it.getKey());
				services.set(formatServiceName(it.getKey(), hash), it.getValue());
			});
		return composeFileNode;
	}

	private String formatServiceName(String serviceName, String hash) {
		serviceName = serviceName.replaceAll("(.*)(__\\w*)(.*)", "$1$3");
		return String.format("%s__%s", serviceName, hash);
	}

	private String create8DigitsHash() {
		return UUID
			.randomUUID()
			.toString()
			.replaceAll("-", "")
			.substring(0, 8)
			;
	}
}
