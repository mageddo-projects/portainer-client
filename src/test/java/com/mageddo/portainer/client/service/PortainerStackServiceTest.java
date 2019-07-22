package com.mageddo.portainer.client.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mageddo.common.jackson.JsonUtils;
import com.mageddo.portainer.client.apiclient.PortainerStackApiClient;
import com.mageddo.portainer.client.vo.DockerStack;
import com.mageddo.portainer.client.vo.DockerStackDeploy;
import com.mageddo.portainer.client.vo.StackEnv;
import com.mageddo.utils.InMemoryRestServer;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Arrays;

import static com.mageddo.utils.TestUtils.readAsString;
import static org.junit.Assert.assertEquals;

public class PortainerStackServiceTest {

	@ClassRule
	public static final InMemoryRestServer server = new InMemoryRestServer(Proxy.class);

	private PortainerStackService portainerStackService;

	@Before
	public void before(){
		portainerStackService = new PortainerStackService(new PortainerStackApiClient(new OkHttpClient(), HttpUrl.parse(server.getURL())));
	}

	@Test
	public void mustFindPortainerStack(){
		// act
		DockerStack dockerStack = portainerStackService.findDockerStack("web3");

		// assert
		assertEquals(Long.valueOf(3), dockerStack.getId());
	}

	@Test
	public void mustSendEnvsToTheServer(){

		// arrange

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

	@Path("/")
	public static class Proxy {

		@Path("/api/stacks")
		@GET
		public Response stacks() throws Exception {
			return Response.ok(readAsString("/mocks/portainer-stack-service-test/001.json")).build();
		}

		@Path("/api/stacks")
		@POST
		@Consumes(MediaType.APPLICATION_JSON)
		public Response createStack(String json) throws Exception {
			JsonNode jsonNode = JsonUtils.readTree(json);
			if(jsonNode.at("/Name").asText().equals("my-stack-with-envs")){
				assertEquals("ka", jsonNode.at("/Env/0/name").asText());
				assertEquals("a", jsonNode.at("/Env/0/value").asText());
				return Response.ok().build();
			}
			return Response.serverError().build();
		}
	}

}
