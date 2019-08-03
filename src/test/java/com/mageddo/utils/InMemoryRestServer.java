package com.mageddo.utils;

import org.junit.rules.ExternalResource;
import spark.Spark;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;

public class InMemoryRestServer extends ExternalResource {

	public static final String HOST = "localhost";

	private int port;

	public InMemoryRestServer() {

	}

	public static int findFreePort() {
		try {
			ServerSocket server = new ServerSocket(0);
			int port = server.getLocalPort();
			server.close();
			return port;
		} catch (IOException e){
			throw new UncheckedIOException(e);
		}
	}

	@Override
	protected void before() throws Throwable {
		port = findFreePort();
		Spark.port(port);
		Spark.init();
		Spark.awaitInitialization();
	}

	public String getURL() {
		return String.format("http://%s:%s", HOST, port);
	}

	public int getPort() {
		return port;
	}

	@Override
	protected void after() {
		Spark.stop();
		Spark.awaitStop();
	}

}
