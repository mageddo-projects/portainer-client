package com.mageddo.portainer.client.vo;

import com.mageddo.portainer.client.apiclient.vo.StackEnvReqV1;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StackEnv {

	private final String name;
	private final String value;

	public StackEnv(String name) {
		this.name = name;
		this.value = null;
	}

	public StackEnv(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public static List<StackEnv> of(Object ... pairs){
		Validate.isTrue(pairs.length % 2 == 0, "quantity must be even");
		final List<StackEnv> envs = new ArrayList<>();
		for (int i = 0; i < pairs.length; i += 2) {
			envs.add(new StackEnv(String.valueOf(pairs[i]), String.valueOf(pairs[i + 1])));
		}
		return envs;
	}

	public static List<StackEnv> valueOf(List<StackEnvReqV1> envs) {
		return envs
			.stream()
			.map(it -> new StackEnv(it.getName(), it.getValue()))
			.collect(Collectors.toList())
		;
	}

	public static List<StackEnv> merge(List<StackEnv> actual, List<StackEnv> newest) {
		final List<StackEnv> newStackEnvs = new ArrayList<>(actual);
		for (StackEnv stackEnv : newest) {
			newStackEnvs.remove(stackEnv);
			newStackEnvs.add(stackEnv);
		}
		return newStackEnvs;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StackEnv stackEnv = (StackEnv) o;
		return name.equals(stackEnv.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
