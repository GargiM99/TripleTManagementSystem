package ca.ttms.components;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ca.ttms.beans.User;
import ca.ttms.beans.enums.Roles;
import ca.ttms.repositories.AddressRepo;
import ca.ttms.repositories.ContactRepo;
import ca.ttms.repositories.PersonRepo;
import ca.ttms.repositories.TokenRepo;
import ca.ttms.repositories.UserRepo;
import ca.ttms.services.JWTService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApplicationStart implements ApplicationListener<ApplicationReadyEvent>{
	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		
		User adminUser = new User();
		adminUser.setUsername("Admin");
		adminUser.setPassword(passwordEncoder.encode("ttmssmtt-2023"));
		adminUser.setRole(Roles.ADMIN);
		
		userRepo.save(adminUser);
	}
}
