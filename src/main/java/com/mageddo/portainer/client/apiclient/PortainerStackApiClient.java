package com.mageddo.portainer.client.apiclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mageddo.common.jackson.JsonUtils;
import com.mageddo.portainer.client.apiclient.vo.RequestRes;
import com.mageddo.portainer.client.apiclient.vo.StackCreateReqV1;
import com.mageddo.portainer.client.apiclient.vo.StackFileGetResV1;
import com.mageddo.portainer.client.apiclient.vo.StackGetRestV1;
import com.mageddo.portainer.client.apiclient.vo.StackUpdateReqV1;
import org.apache.commons.lang3.Validate;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class PortainerStackApiClient {

	private final WebTarget webTarget;

	public PortainerStackApiClient(WebTarget webTarget) {
		this.webTarget = webTarget;
	}

	public List<StackGetRestV1> findStacks(){
		return JsonUtils.readValue(webTarget
			.path("/api/stacks")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(String.class), new TypeReference<List<StackGetRestV1>>(){})
		;
	}

	public void createStack(StackCreateReqV1 createReqV1){
		Response res = webTarget
			.path("/api/stacks")
			.queryParam("type", 1)
			.queryParam("method", "string")
			.queryParam("endpointId", 1)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(JsonUtils.writeValueAsString(createReqV1)));
		Validate.isTrue(
			res.getStatusInfo().toEnum() == Response.Status.OK,
			res.readEntity(String.class)
		);
	}

	public RequestRes updateStack(StackUpdateReqV1 updateReqV1){
		Response res = webTarget
			.path("/api/stacks/")
			.path(String.valueOf(updateReqV1.getId()))
			.queryParam("endpointId", 1)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.put(Entity.json(JsonUtils.writeValueAsString(updateReqV1)));
		final String body = res.readEntity(String.class);
		Validate.isTrue(
			res.getStatusInfo().toEnum() == Response.Status.OK, body
		);
		return RequestRes.valueOf(res, body);
	}

	public StackFileGetResV1 findStackContent(long stackId) {
		return webTarget
			.path("/api/stacks/")
			.path(String.valueOf(stackId))
			.path("/file")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(StackFileGetResV1.class)
		;
	}
}
