package com.anilsevici.linkedlnaccount;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LinkedlnAccounting {

	private static final String linkedinKey = "7702ee2hazt424";
	private static final String linkedinSecret = "bL8tcyXNjAVFV0P4";
	private OAuthService service;
	private Token requestToken;
	private Token accessToken;
	private Response info;

	public void connect(String ouath_verifier) {
		info = ouathrequest(ouath_verifier);

	}

	private OAuthService serviceconfig() {
		service = new ServiceBuilder().provider(LinkedInApi.class)
				.apiKey(linkedinKey).apiSecret(linkedinSecret)
				.callback("http://localhost:8080/kariyerweb/index.html")
				.build();

		return service;
	}

	public OAuthService getService() {
		return service;
	}

	public Token getRequestToken() {
		return requestToken;
	}

	public Token getAccessToken() {
		return accessToken;
	}

	public String getAuthorizationUrl() {
		OAuthService service = serviceconfig();
		requestToken = service.getRequestToken();

		return service.getAuthorizationUrl(requestToken);
	}

	private void getaccessToken(String v) {
		Verifier verifier = new Verifier(v);
		accessToken = getService().getAccessToken(getRequestToken(), verifier);

	}

	private String Endpointquery(String v) {
		getaccessToken(v);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("https://api.linkedin.com/v1/people/~");
		stringBuilder.append("?oauth2_access_token=");
		stringBuilder.append(getAccessToken().getToken());
		stringBuilder.append("&format=json");

		return stringBuilder.toString();
	}

	public Response ouathrequest(String v) {
		OAuthRequest request = new OAuthRequest(Verb.GET, Endpointquery(v));
		getService().signRequest(getAccessToken(), request);

		return request.send();
	}

	public JsonObject getinformationuser() {
		JsonParser jsonparser = new JsonParser();
		return (JsonObject) jsonparser.parse(info.getBody());
	}

}
