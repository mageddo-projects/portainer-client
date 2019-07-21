package com.mageddo.portainer.client.apiclient;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.mageddo.common.resteasy.RestEasy;
import com.mageddo.common.resteasy.RestEasyClient;
import com.mageddo.portainer.client.utils.EnvUtils;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.util.Date;

public class PortainerAuthenticationFilter implements ClientRequestFilter {

	private static final JWTVerifier JWT_VERIFIER = JWT.require(Algorithm.HMAC256("secret"))
		.withIssuer("auth0")
		.build();

	private final PortainerAuthApiClient portainerAuthApiClient;

	public PortainerAuthenticationFilter(PortainerAuthApiClient portainerAuthApiClient) {
		this.portainerAuthApiClient = portainerAuthApiClient;
	}

	@Override
	public void filter(ClientRequestContext requestContext) {
		try {
			final String token = EnvUtils.getAuthToken();
			if(isTokenExpired(token)) {
				throw new JWTVerificationException("token expired: " + token);
			}
			setupToken(requestContext, token);
		} catch (JWTVerificationException exception){
			final String token = portainerAuthApiClient.doAuth(EnvUtils.getUsername(), EnvUtils.getPassword());
			EnvUtils.setAuthToken(token);
			setupToken(requestContext, token);
		}
	}

	private boolean isTokenExpired(String token) {
		if(StringUtils.isBlank(token)){
			return true;
		}
		return JWT_VERIFIER
				.verify(token)
				.getExpiresAt()
				.before(new Date());
	}

	private void setupToken(ClientRequestContext requestContext, String token) {
		requestContext
			.getHeaders()
			.add("Authorization", "Bearer " + token)
		;
	}
}
