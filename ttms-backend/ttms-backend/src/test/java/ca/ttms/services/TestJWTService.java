package ca.ttms.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.ttms.beans.User;
import ca.ttms.beans.enums.Roles;
import io.jsonwebtoken.ExpiredJwtException;

/**
 * Tests the services for JWTokens
 * 
 * @author Hamza
 * date: 2023/03/08
 */

class TestJWTService {
	private final JWTService jwtService = new JWTService();;
	private User user1, user2;
	private Map<String, Object> extraClaims1, extraClaims2;
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();;
	
	@BeforeEach
	void Arrange_Users(){
		user1 = User
				.builder()
		        .username("atchah")
		        .password(passwordEncoder.encode("1234"))
		        .role(Roles.AGENT)
		        .build();	
		
		user2 = User
				.builder()
		        .username("johns")
		        .password(passwordEncoder.encode("4567"))
		        .role(Roles.AGENT)
		        .build();
	}
	
	@BeforeEach
	void Arrange_Claims(){
		extraClaims1 = new HashMap<String, Object>();
		extraClaims1.put("Role", user1.getRole());
		
		extraClaims2 = new HashMap<String, Object>();
		extraClaims2.put("Firstname", user2.getRole());
		extraClaims2.put("Lastname", user2.getRole());
	}

	@Test 
	//Tests if a blank JWT is different depending on the subject
	void Generate_MultipleBlankJWTokens_CompareDifferentSubject() {
		//Act
		String jwtoken1 = jwtService.generateJwtoken(user1);
		String jwtoken2 = jwtService.generateJwtoken(user2);
		
		//Assert
		assertNotEquals( jwtoken1, jwtoken2 );
	}
	
	@Test
	//Tests if a blank JWT can be generated by checking the length
	void Generate_BalnkJWTokens_CheckValidLength() {
		//Act
		int expectedLength = 150;
		String jwtoken = jwtService.generateJwtoken(user1);
		int resultLength = jwtoken.length();
		
		//Assert
		assertTrue( resultLength  > expectedLength, "Token should be longer than 150" );
	}
	
	@Test
	//Tests if a blank JWT will generate if the user is null
	void Generate_NullBlankJWToken_CheckNullPointer() {	
		//Act & Assert
		assertThrows( NullPointerException.class, () -> {jwtService.generateJwtoken(null);} );
	}
	
	@Test
	//Tests if a JWT with claim can be generated by checking the length
	void Generate_JWToken_CheckValidLength() {
		//Act
		String jwtoken = jwtService.generateJwtoken(extraClaims1,user1);
		
		//Assert
		assertTrue( jwtoken.length() > 150, "Token should be longer than 150" );
	}
	
	@Test
	//Tests if a JWT with claims are unique with different claims but same subject
	void Generate_MultipleJWToken_CompareDifferentClaims(){
		//Act
		String jwtoken1 = jwtService.generateJwtoken(extraClaims1,user1);
		String jwtoken2 = jwtService.generateJwtoken(extraClaims2,user1);
		
		//Assert
		assertFalse( jwtoken1.equals(jwtoken2), "Token should be unique" );
	}
	
	@Test
	//Tests if claims is null be user is still entered
	void Generate_NullClaimJWToken_CheckIllegalArgument() {	
		//Act & Assert
		assertThrows( IllegalArgumentException.class, () -> {jwtService.generateJwtoken(null, user1);} );
	}
	
	@Test
	//Tests if extracting the subject from a JWT
	void Extract_SubjectofJWToken_CheckSubjectDoesntChange() {
		//Act
		String expectedSubject = user1.getUsername();
		String jwtoken = jwtService.generateJwtoken(extraClaims1,user1);
		String resultSubject = jwtService.extractSubject(jwtoken);
		
		//Assert
		assertTrue( resultSubject.equals(expectedSubject), "Subject should be the same as the username" );
	}

	@Test
	//Tests if extracting the subject from a 2 JWT to check if they are different
	void Extract_SubjectofJWToken_CheckSubjectIsDifferent() {
		//Act
		String jwtoken1 = jwtService.generateJwtoken(extraClaims1,user1);
		String jwtoken2 = jwtService.generateJwtoken(extraClaims2,user2);
		
		String subject1 = jwtService.extractSubject(jwtoken1);
		String subject2 = jwtService.extractSubject(jwtoken2);
		
		//Assert
		assertFalse( subject1.equals(subject2), "Subject should be different" );
	}

	@Test
	//Tests if token is null for extracting claims
	void Extract_SubjectofNullToken_CheckIllegalArgument() {	
		//Act & Assert
		assertThrows( IllegalArgumentException.class, () -> {jwtService.extractSubject(null);} );
	}

	@Test
	//Tests if token validation works by creating a JWT
	void Validate_JWToken_CheckValidSubject() {
		//Act
		String jwtoken = jwtService.generateJwtoken(extraClaims1,user1);
		
		//Assert
		assertTrue(jwtService.isJwtokenValid(jwtoken, user1), "Token should be valid" );
	}
	
	@Test
	//Tests if token validation works by creating a JWT with different username then subject
	void Validate_JWToken_CheckInvalidSubject() {
		//Act
		String jwtoken = jwtService.generateJwtoken(extraClaims1,user1);
		
		//Assert
		assertFalse(jwtService.isJwtokenValid(jwtoken, user2), "Token should be invalid" );
	}

	@Test
	//Tests if token validation will throw an error if token is expired
	void Validate_ExpiredJWToken_CheckJWTokenIsExpired() {
		//Act
		String jwtoken = jwtService.generateJwtoken(extraClaims1,user1,-1000L);
		
		//Assert
		assertThrows(ExpiredJwtException.class, () -> {jwtService.isJwtokenValid(jwtoken, user1);});
	}
}
