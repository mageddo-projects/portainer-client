package com.mageddo.portainer.client.apiclient;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.mageddo.portainer.client.utils.EnvUtils;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class PortainerAuthenticationFilter implements Authenticator {

	private static final JWTVerifier JWT_VERIFIER = JWT.require(Algorithm.HMAC256("secret"))
		.withIssuer("auth0")
		.build();

	private final PortainerAuthApiClient portainerAuthApiClient;

	public PortainerAuthenticationFilter(PortainerAuthApiClient portainerAuthApiClient) {
		this.portainerAuthApiClient = portainerAuthApiClient;
	}

//	@Override
//	public Response intercept(Chain chain) throws IOException {
//		Request request;
//		try {
//			final String token = EnvUtils.getAuthToken();
//			if(isTokenExpired(token)) {
//				throw new JWTVerificationException("token expired: " + token);
//			}
//			request = setupToken(token, chain
//				.request());
//		} catch (JWTVerificationException exception){
//			final String token = portainerAuthApiClient.doAuth(EnvUtils.getUsername(), EnvUtils.getPassword());
//			EnvUtils.setAuthToken(token);
//			request = setupToken(token, chain
//				.request());
//		}
//		return chain.proceed(request);
//	}

	@Override
	public Request authenticate(Route route, Response response) {
		try {
			final String token = EnvUtils.getAuthToken();
			if(isTokenExpired(token)) {
				throw new JWTVerificationException("token expired: " + token);
			}
			return setupToken(token, response);
		} catch (JWTVerificationException exception){
			final String token = portainerAuthApiClient.doAuth(EnvUtils.getUsername(), EnvUtils.getPassword());
			EnvUtils.setAuthToken(token);
			return setupToken(token, response);
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

	private Request setupToken(String token, Response response) {
		return response
			.request()
			.newBuilder()
			.addHeader("Authorization", "Bearer " + token)
			.build()
		;
	}
}
