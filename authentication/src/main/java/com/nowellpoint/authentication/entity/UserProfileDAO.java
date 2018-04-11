package com.nowellpoint.authentication.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class UserProfileDAO extends BasicDAO<UserProfileEntity, ObjectId>{

	public UserProfileDAO(Class<UserProfileEntity> entityClass, Datastore ds) {
		super(entityClass, ds);
	}

}
