package com.nowellpoint.registration.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class RegistrationDAO extends BasicDAO<RegistrationEntity, ObjectId> {

	public RegistrationDAO(Class<RegistrationEntity> entityClass, Datastore datastore) {
		super(entityClass, datastore);
	}
}