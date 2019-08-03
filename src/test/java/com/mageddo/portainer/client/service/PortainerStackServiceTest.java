package com.mageddo.portainer.client.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mageddo.common.jackson.JsonUtils;
import com.mageddo.portainer.client.apiclient.PortainerStackApiClient;
import com.mageddo.portainer.client.utils.YamlUtils;
import com.mageddo.portainer.client.vo.DockerStack;
import com.mageddo.portainer.client.vo.DockerStackDeploy;
import com.mageddo.portainer.client.vo.StackEnv;
import com.mageddo.utils.InMemoryRestServer;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.Validate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import spark.Spark;

import java.util.Arrays;

import static com.mageddo.utils.TestUtils.readAsString;
import static org.junit.Assert.*;

public class PortainerStackServiceTest {

	@ClassRule
	public static final InMemoryRestServer server = new InMemoryRestServer();

	private PortainerStackService portainerStackService;

	@Before
	public void before(){
		portainerStackService = new PortainerStackService(new PortainerStackApiClient(new OkHttpClient(), HttpUrl.parse(server.getURL())));
	}

	@Test
	public void mustFindPortainerStack(){

		// arrange
		findStacksRoute();

		// act
		DockerStack dockerStack = portainerStackService.findDockerStack("web3");

		// assert
		assertEquals(Long.valueOf(3), dockerStack.getId());
	}

	@Test
	public void mustSendEnvsToTheServer(){

		// arrange
		findStacksRoute();
		createStacksRoute();

		// act
		portainerStackService.createOrUpdateStack(
			new DockerStackDeploy()
			.setEnvs(Arrays.asList(new StackEnv("ka", "a"), new StackEnv("kb", "b")))
			.setPrune(true)
			.setStackFileContent("...")
			.setName("my-stack-with-envs")
		);

		// assert

	}

	@Test
	public void mustRunExistentStackChangingEnv(){

		// arrange
		setupStackRunDeps();

		// act
		portainerStackService.runStack("ls-stack", true, StackEnv.of("VERSION", 2));

	}

	@Test
	public void mustRenameStackServicesToTempName() throws Exception {

		// arrange
		final String composeFileContent = readAsString("/mocks/portainer-stack-service-test/004.yml");
		final String expectedComposeFileContent = readAsString("/mocks/portainer-stack-service-test/005.yml");

		// act
		JsonNode replacedStack = portainerStackService.createTempServices(composeFileContent, "da4c5da0");

		// assert
		assertNotNull(replacedStack);
		assertEquals(expectedComposeFileContent, YamlUtils.getYamlInstance().writeValueAsString(replacedStack));

	}

	private void setupStackRunDeps() {
		Spark.get("/api/stacks", (req, res) -> {
			res.type("application/json");
			return readAsString("/mocks/portainer-stack-service-test/002.json");
		});

		Spark.get("/api/stacks/:stackId/file", (req, res) -> {
			res.type("application/json");
			Validate.isTrue("123".equals(req.params("stackId")));
			return readAsString("/mocks/portainer-stack-service-test/003.json");
		});

		Spark.put("/api/stacks/:stackId", "application/json", (req, res) -> {
			res.type("application/json");
			assertEquals("123", req.params("stackId"));
			assertTrue(req.body(), req.body().contains("\"name\":\"VERSION\",\"value\":\"2\""));
			return "";
		});
	}



	void findStacksRoute() {
		Spark.get("/api/stacks", (req, res) -> {
			res.type("application/json");
			return readAsString("/mocks/portainer-stack-service-test/001.json");
		});
	}

	void createStacksRoute() {
		Spark.post("/api/stacks", "application/json", (req, res) -> {
			res.type("application/json");
			final JsonNode jsonNode = JsonUtils.readTree(req.body());
			if(jsonNode.at("/Name").asText().equals("my-stack-with-envs")){
				assertEquals("ka", jsonNode.at("/Env/0/name").asText());
				assertEquals("a", jsonNode.at("/Env/0/value").asText());
				return "";
			}
			res.status(500);
			return null;
		});
	}

}
