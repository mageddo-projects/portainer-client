package com.mageddo.portainer.client.apiclient.vo;

import com.fasterxml.jackson.annotation.JsonSetter;

public class StackFileGetResV1 {

	private String stackFileContent;

	public String getStackFileContent() {
		return stackFileContent;
	}

	@JsonSetter("StackFileContent")
	public StackFileGetResV1 setStackFileContent(String stackFileContent) {
		this.stackFileContent = stackFileContent;
		return this;
	}
}
