package com.nowellpoint.identity.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class OrganizationDAO extends BasicDAO<OrganizationEntity, ObjectId>{

	public OrganizationDAO(Class<OrganizationEntity> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
}