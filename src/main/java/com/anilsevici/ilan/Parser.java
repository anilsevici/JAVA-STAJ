package com.anilsevici.ilan;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;

public class Parser {

	private String data;

	public Parser(String data) {
		this.data = data;
	}

	public JsonObject parseinit(String data) {
		JsonElement parser = new JsonParser().parse(data);
		JsonObject joUser = parser.getAsJsonObject();

		return joUser;
	}

	public BasicDBObject addObject() {
		JsonObject object = parseinit(data);

		String ilan_id = object.get("_id").getAsString();
		String title = object.get("title").getAsString();
		String def = object.get("definition").getAsString();
		String des = object.get("description").getAsString();
		String date = object.get("aktif").getAsString();
		String date2 = object.get("pasif").getAsString();
		String tag = object.get("tag").getAsString();

		BasicDBObject ilan = new BasicDBObject();
		ilan.append("_id", ilan_id).append("title", title)
				.append("definition", def).append("description", des)
				.append("aktif", date).append("pasif", date2)
				.append("statu", false).append("publish", true).append("tag", tag);

		return ilan;

	}


}
