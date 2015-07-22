package com.anilsevici.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ElasticSearchUtils {
	private static Node node = new NodeBuilder().client(true).build().start();
	
	public static boolean putUserDocument(JsonObject obj){
		String id = null;
		try{
			id = obj.get("_id").getAsString();
		} catch(NullPointerException e){
			throw new IllegalArgumentException("JsonObject must contain field _id");
		}
		Client client = node.client();
		boolean success;
		System.out.println("preparing object");
		JsonObject objectToPut = prepareObject(obj);
		System.out.println("object prepared");
		try {
			client.prepareIndex("users", "user").setId(id)
					.setSource(new Gson().toJson(objectToPut))
					.execute()
					.actionGet();
			success = true;
		} catch (ElasticsearchException e) {//adding a new document to elasticsearch failed
			//TODO: handle exception: logging, etc..
			e.printStackTrace();
			success = false;
		}
		client.close();
		System.out.println("closing client");
		return success;
	}
	
	private static JsonObject prepareObject(JsonObject source) throws IllegalArgumentException{
		JsonObject obj = new JsonObject();
		try{
			obj.addProperty("firstName", source.get("firstName").getAsString());
			obj.addProperty("lastName", source.get("lastName").getAsString());
		} catch(NullPointerException e){
			throw new IllegalArgumentException("JsonObject must contain all fields following: _id, firstName, lastName");
		}
		JsonElement industry = source.get("industry");
		if(industry != null)
			obj.addProperty("industry", industry.getAsString());
		JsonElement location = source.get("location");
		JsonElement locationName = null;
		if(location != null)
			locationName = location.getAsJsonObject().get("name");
		if(locationName != null)
			obj.addProperty("location", locationName.getAsString());
		
		return obj;
	}
	
	public static JsonArray findUserDocument(String name){
		Client client = node.client();
		if(name == null)
			name = "";
		JsonArray array = new JsonArray();
		String[] queryParams = prepareQueryParams(name);
		
		//initializing firstname, lastname queries
		List<QueryBuilder> queries = new ArrayList<QueryBuilder>();
		queries.add(initBoolQueryBuilder(queryParams, "firstName"));
		queries.add(initBoolQueryBuilder(queryParams, "lastName"));
		queries.add(initBoolQueryBuilder(queryParams, "industry"));
		queries.add(initBoolQueryBuilder(queryParams, "location"));
		
		JsonParser parser = new JsonParser();
		//combining queries
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		for(QueryBuilder qb : queries)
			query.should(qb);
		query.minimumNumberShouldMatch(1);
		
		SearchResponse response = client.prepareSearch("users").setQuery(query)
										.execute().actionGet();
		for(SearchHit hit : response.getHits().getHits()){
			JsonObject object = parser.parse(hit.getSourceAsString()).getAsJsonObject();
			object.addProperty("_id", hit.getId());
			array.add(object);
		}
		client.close();
		return array;
	}
	
	private static String[] prepareQueryParams(String query){
		String[] params = query.split(" ");
		for(int i = 0; i < params.length; i++)
			params[i] = "*" + params[i] + "*";
		return params;
	}
	
	private static BoolQueryBuilder initBoolQueryBuilder(String[] queryParams, String fieldName){
		BoolQueryBuilder builder = QueryBuilders.boolQuery();
		for(String q : queryParams)
			builder.should(QueryBuilders.wildcardQuery(fieldName, "*" + q + "*"));
		builder.minimumNumberShouldMatch(1);
		
		return builder;
	}
		
}
