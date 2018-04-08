package com.nowellpoint.authentication.provider;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.authentication.entity.UserProfileEntity;
import com.nowellpoint.authentication.util.EnvironmentVariables;

@Startup
@Singleton
public class DatastoreProvider {
	
	private Datastore datastore;
	private MongoClient mongoClient;
	
	public Datastore getDatastore() {
		return datastore;
	}
	
	@PostConstruct
	public void init() {
		final MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", EnvironmentVariables.getMongoClientUri()));
        mongoClient = new MongoClient(mongoClientUri);
        
        final Morphia morphia = new Morphia();
        
        morphia.map(UserProfileEntity.class);

        datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
        datastore.ensureIndexes();
	}
	
	@PreDestroy
	public void destroy() {
		mongoClient.close();
	}
}