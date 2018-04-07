package com.nowellpoint.registration.test;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.registration.entity.RegistrationDAO;
import com.nowellpoint.registration.entity.RegistrationEntity;
import com.nowellpoint.registration.entity.UserProfileEntity;
import com.nowellpoint.registration.model.ModifiableRegistration;
import com.nowellpoint.registration.model.ModifiableUserInfo;
import com.nowellpoint.registration.model.Registration;
import com.nowellpoint.registration.model.UserInfo;
import com.okta.sdk.lang.Assert;

public class CreateRegistrationTest {
	
	@Test
	public void testCreateRegistration() {
		
		MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", System.getenv("MONGO_CLIENT_URI")));
		
		MongoClient mongoClient = new MongoClient(mongoClientUri);
		
		final Morphia morphia = new Morphia();
		
		morphia.mapPackage("com.nowellpoint.signup.entity");
		
		final Datastore datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
		datastore.ensureIndexes();
		
		ModelMapper mapper = new ModelMapper();
		mapper.addConverter(new AbstractConverter<String, ObjectId>() {
			@Override
			protected ObjectId convert(String source) {
				return source == null ? null : new ObjectId(source);
			}
		});
		mapper.addConverter(new AbstractConverter<ObjectId, String>() {		
			@Override
			protected String convert(ObjectId source) {
				return source == null ? null : source.toString();
			}
		});
		mapper.addConverter(new AbstractConverter<UserProfileEntity, UserInfo>() {
			@Override
			protected UserInfo convert(UserProfileEntity source) {
				return source == null ? null : mapper.map(source, ModifiableUserInfo.class).toImmutable();
			}
		});
		
		Query<UserProfileEntity> query = datastore
				.createQuery(UserProfileEntity.class)
				.field("username")
				.equal("system.administrator@nowellpoint.com");
				 
		UserProfileEntity userProfileEntity = query.get();
		
		UserInfo userInfo = mapper.map(userProfileEntity, ModifiableUserInfo.class).toImmutable();
		
		Registration registration = Registration.builder()
				.countryCode("US")
				.email("jherson@aim.com")
				.firstName("John")
				.lastName("Herson")
				.createdBy(userInfo)
				.lastUpdatedBy(userInfo)
				.build();
		
		RegistrationDAO dao = new RegistrationDAO(RegistrationEntity.class, datastore);
		
		RegistrationEntity document = mapper.map(registration, RegistrationEntity.class);
		
		dao.save(document);
		
		document.setPhone(null);
		
		datastore.save(document);
		
		registration = mapper.map(document, ModifiableRegistration.class).toImmutable();
		
		System.out.println(registration.getId());
		System.out.println(registration.getCreatedBy().getId());
		System.out.println(registration.getCreatedBy().getName());
		System.out.println(registration.getLastUpdatedBy().getId());
		System.out.println(registration.getLastUpdatedBy().getName());
		
		RegistrationEntity entity = dao.get(new ObjectId(registration.getId()));
		
		Assert.notNull(entity.getCountryCode());
		
		dao.deleteById(new ObjectId(registration.getId()));
	}
}