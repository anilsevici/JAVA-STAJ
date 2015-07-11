package com.anilsevici.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;

public class Parser {

	private String data;

	public Parser(String data) {
		this.data = data;
	}

	private JsonObject parseinit(String data) {
		JsonElement parser = new JsonParser().parse(data);
		JsonObject joUser = parser.getAsJsonObject();

		return joUser;
	}

	public BasicDBObject addObject() {
		JsonObject object = parseinit(data);

		String ilan_id = object.get("ilanid").getAsString();
		String title = object.get("title").getAsString();
		String def = object.get("definition").getAsString();
		String des = object.get("description").getAsString();
		String date = object.get("aktif").getAsString();
		String date2 = object.get("pasif").getAsString();
		

		BasicDBObject ilan = new BasicDBObject();
		ilan.append("_id", ilan_id).append("title", title)
				.append("definition", def).append("description", des)
				.append("aktif", date).append("pasif", date2).append("statu", false);

		return ilan;

	}

}
