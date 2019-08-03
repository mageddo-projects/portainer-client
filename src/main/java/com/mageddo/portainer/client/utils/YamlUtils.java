package com.mageddo.portainer.client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public final class YamlUtils {

	private static final ObjectMapper instance;

	static {
		instance = new ObjectMapper(new YAMLFactory())
		.enable(SerializationFeature.INDENT_OUTPUT)
		;
	}

	private YamlUtils() {
	}

	public static ObjectMapper getYamlInstance() {
		return instance;
	}
}
