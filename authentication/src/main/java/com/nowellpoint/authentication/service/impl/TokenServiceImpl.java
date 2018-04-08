package com.nowellpoint.authentication.service.impl;

import java.math.BigInteger;
import java.net.URI;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.mongodb.morphia.query.Query;

import com.nowellpoint.api.IdentityResource;
import com.nowellpoint.api.model.Token;
import com.nowellpoint.authentication.entity.UserProfileEntity;
import com.nowellpoint.authentication.model.Key;
import com.nowellpoint.authentication.model.Keys;
import com.nowellpoint.authentication.model.TokenResponse;
import com.nowellpoint.authentication.provider.DatastoreProvider;
import com.nowellpoint.authentication.service.AuthenticationService;
import com.nowellpoint.authentication.service.TokenService;
import com.nowellpoint.util.Properties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolverAdapter;

public class TokenServiceImpl implements TokenService {
	
	private static final Map<String,Key> KEY_CACHE = new ConcurrentHashMap<String,Key>();
	
	@Inject
	private Logger log;
	
	@Inject
	private DatastoreProvider datastoreProvider;
	
	@Inject
	private AuthenticationService authenticationService;
	
	@Inject
	private Event<UserProfileEntity> loggedInEvent;
	
	@Override
	public Token createToken(TokenResponse tokenResponse) {
		Jws<Claims> jwsClaims = Jwts.parser()
				.setSigningKeyResolver(new SigningKeyResolverAdapter() {
					@SuppressWarnings("rawtypes")
					public java.security.Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
						Key key = getKey(jwsHeader.getKeyId());
						try {
							BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(key.getModulus()));
							BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(key.getExponent()));
							return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, exponent));
						} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
							log.error(e);
							return null;
						}
					}})
				.parseClaimsJws(tokenResponse.getAccessToken());
		
		UserProfileEntity userProfile = lookupUserProfile(jwsClaims.getBody().getSubject());
		
		String userId = userProfile.getId().toString();
		String organizationId = userProfile.getOrganization().getId().toString();
		
		URI href = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
				.path(IdentityResource.class)
				.path("{organizationId}")
				.path("{userId}")
				.build(organizationId, userId);
		
		SecretKey key = new SecretKeySpec(KEY_CACHE.get(jwsClaims.getHeader().getKeyId()).getModulus().getBytes(), "HmacSHA512");
		
		String jws = Jwts.builder()
				.setHeaderParam("kid", jwsClaims.getHeader().getKeyId())
				.setId(jwsClaims.getBody().getId())
				.setIssuer(jwsClaims.getBody().getIssuer())
				.setAudience(organizationId)
				.setSubject(userId)
				.setExpiration(jwsClaims.getBody().getExpiration())
				.setIssuedAt(jwsClaims.getBody().getIssuedAt())
				.claim("scope", jwsClaims.getBody().get("groups"))
				.signWith(SignatureAlgorithm.HS512, key)
				.compact();

		Token token = Token.builder()
				.environmentUrl(System.getProperty(Properties.API_HOSTNAME))
				.id(href.toString())
				.accessToken(jws)
				.expiresIn(tokenResponse.getExpiresIn())
				.refreshToken(tokenResponse.getRefreshToken())
				.tokenType(tokenResponse.getTokenType())
				.build();
		
		//
		// fire event for handling login functions
		//
		
		loggedInEvent.fire(userProfile);
        
        return token;
	}
	
	@Override
	public Jws<Claims> verifyToken(String accessToken) {
		return Jwts.parser()
				.setSigningKeyResolver(new SigningKeyResolverAdapter() {
		        	@SuppressWarnings("rawtypes")
					public java.security.Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
		        		Key key = getKey(jwsHeader.getKeyId());
		        		return new SecretKeySpec(key.getModulus().getBytes(), "HmacSHA512");
		            }})
				.parseClaimsJws(accessToken);
	}
	
	private UserProfileEntity lookupUserProfile(String referenceId) {
		Query<UserProfileEntity> query = datastoreProvider.getDatastore()
				.createQuery(UserProfileEntity.class)
				.field("referenceId")
				.equal(referenceId);
				 
		UserProfileEntity userProfileEntity = query.get();
		
		return userProfileEntity;
	}
	
	private Key getKey(String keyId) {
		if (! KEY_CACHE.containsKey(keyId)) {
			addKeys();
		}
		
		Key key = KEY_CACHE.get(keyId);
		
		return key;
	}
	
	private void addKeys() {
		Keys keys = authenticationService.getKeys();
		keys.getKeys().forEach(key -> {
			KEY_CACHE.put(key.getKeyId(), key);
		});
	}
}