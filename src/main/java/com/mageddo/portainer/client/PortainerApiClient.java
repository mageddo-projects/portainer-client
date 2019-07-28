package com.mageddo.portainer.client;

import com.mageddo.portainer.client.service.PortainerStackService;
import com.mageddo.portainer.client.utils.BeansFactory;

public final class PortainerApiClient {

	PortainerApiClient() {}

	public PortainerStackService stacks(){
		return BeansFactory.newStackService();
	}

}
