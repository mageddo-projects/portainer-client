package com.mageddo.portainer.client.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EnvUtilsTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void mustLoadPropsFromResources(){
		
		// act
		EnvUtils.clearCache();
		PortainerProp props = EnvUtils.configProps();

		// assert
		assertNotNull(props);
		assertEquals("admin", props.asText("portainer.auth.username", " "));
	}

	@Test
	public void mustLoadPropsFromConfigPath() throws Exception {

		// arrange
		System.setProperty("PTN_CONFIG_DIR", temporaryFolder.getRoot().toPath().toString());
		final Path configPath = EnvUtils.getConfigFilePath();
		EnvUtils.loadConfigPropsFromResources()
			.put("portainer.auth.username", "elvis")
			.store(configPath)
		;

		// act
		final PortainerProp props = EnvUtils.configProps();

		// assert
		assertNotNull(props);
		assertEquals("elvis", props.asText("portainer.auth.username", " "));
		System.out.println(configPath);
	}

}
