package com.mageddo.utils;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

public class TestUtils {

	private TestUtils() {
	}

	public static String readAsString(String path) throws Exception {
		final InputStream resource = TestUtils.class.getResourceAsStream(path);
		assertNotNull("file not found: " + path, resource);
		return IOUtils.toString(resource, "UTF-8");
	}

	public static InputStream readAsStream(String path) throws Exception {
		return TestUtils.class.getResourceAsStream(path);
	}

}
