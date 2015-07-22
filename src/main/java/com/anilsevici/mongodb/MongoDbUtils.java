package com.anilsevici.mongodb;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoDbUtils {

	public static MongoClient getClient() throws UnknownHostException {

		return new MongoClient(new MongoClientURI("mongodb://localhost"));
	}

	public static DB getDB() throws UnknownHostException {

		return getClient().getDB("kariyer");
	}

	public static DBCollection getIlanCollection() throws UnknownHostException {
		return getDB().getCollection("ilan");
	}

	public static DBCollection getHrIlanCollection()
			throws UnknownHostException {
		return getDB().getCollection("hrilan");
	}

	public static DBCollection getUserCollection() throws UnknownHostException {
		return getDB().getCollection("userprofile");
	}

	public static DBCollection getTagCollection() throws UnknownHostException {
		return getDB().getCollection("tags");
	}

	public static DBCollection getBlackCollection() throws UnknownHostException {
		return getDB().getCollection("blacklists");
	}

}
