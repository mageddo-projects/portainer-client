package com.mageddo.portainer.client.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class EnvUtils {

	private static PortainerProp props;

	public static String getPortainerApiUri(){
		return configProps().asText("portainer.uri", "http://localhost:9000");
	}

	public static String getAuthToken() {
		return configProps().asText("portainer.auth.token");
	}

	public static void setAuthToken(String authToken) {
		configProps().put("portainer.auth.token", authToken);
	}

	public static void setUsername(String username) {
		configProps().put("portainer.auth.username", username);
	}

	public static String getUsername() {
		return configProps().asText("portainer.auth.username");
	}

	public static void setPassword(String password) {
		configProps().put("portainer.auth.password", password);
	}

	public static String getPassword() {
		return configProps().asText("portainer.auth.password");
	}

	public static PortainerProp configProps(){
			if(props != null){
				return props;
			}
			return props = loadConfigProps();
	}

	public static void clearCache(){
		props = null;
	}

	private static PortainerProp loadConfigProps(){
		return mergeWithEnvVars(loadConfigProps0());
	}

	private static PortainerProp mergeWithEnvVars(PortainerProp props) {
		System
			.getenv()
			.entrySet()
			.stream()
			.filter(it -> it.getKey().startsWith("PTN"))
			.forEach(env -> props.put(toPropKey(env), env.getValue()));
		return props;
	}

	private static String toPropKey(Map.Entry<String, String> env) {
		return env
			.getKey()
			.replaceFirst("PTN", "PORTAINER")
			.replaceAll("_", ".")
			.toLowerCase()
		;
	}

	private static PortainerProp loadConfigProps0(){
		final PortainerProp resourceProps = loadConfigPropsFromResources();
		final PortainerProp pathProps = loadConfigPropsFromPath();
		if(pathProps == null){
			return resourceProps;
		}
		resourceProps.merge(pathProps);
		return Objects.requireNonNull(resourceProps, "Default config props not found");
	}


	public static Path getConfigFilePath() {
		return getConfigDir().resolve(getConfigFileName());
	}

	private static String getConfigFileName() {
		return StringUtils.firstNonBlank(
			System.getenv("PTN_CONFIG_FILE_NAME"),
			"portainer-cli.properties"
		);
	}

	private static PortainerProp loadConfigPropsFromPath() {
		final Path configPath = getConfigFilePath();
		if(!Files.exists(configPath)){
			return null;
		}
		try (InputStream in = Files.newInputStream(configPath)){
			return new PortainerProp(in);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static Path getConfigDir() {
		return Paths.get(StringUtils.firstNonBlank(
			System.getenv("PTN_CONFIG_DIR"),
			System.getProperty("PTN_CONFIG_DIR"),
			Paths.get(System.getProperty("user.home"), ".portainer-cli/").toString()
		));
	}

	static PortainerProp loadConfigPropsFromResources() {
		return Optional
			.ofNullable(getConfigStreamFromResources())
			.map(PortainerProp::new)
			.orElse(null)
		;
	}

	private static InputStream getConfigStreamFromResources() {
		return EnvUtils.class.getResourceAsStream("/portainer-cli.properties");
	}

	public static Properties createProperties(InputStream it) {
		try {
			Properties p = new Properties();
			p.load(it);
			return p;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static boolean insecureConnection() {
		return configProps().asBoolean("portainer.uri.insecure", false);
	}

	public static void setInsecureConnection(boolean insecureConnection) {
		configProps().put("portainer.uri.insecure", String.valueOf(insecureConnection));
	}

	public static void setPortainerApiUri(String serverURI) {
		configProps().put("portainer.uri", serverURI);
	}

}
