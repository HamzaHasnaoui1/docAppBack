package ma.formation;

import ma.formation.entities.*;
import ma.formation.repositories.ConsultationRepository;
import ma.formation.repositories.MedecinRepository;
import ma.formation.repositories.PatientRepository;
import ma.formation.repositories.RendezVousRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

@SpringBootApplication
public class PatientApplication implements CommandLineRunner {
	@Autowired
	private PatientRepository patientRepository;
	@Autowired
	private MedecinRepository medecinRepository;
	@Autowired
	private RendezVousRepository rendezVousRepository;
	@Autowired
	private ConsultationRepository consultationRepository;

	public static void main(String[] args) {
		SpringApplication.run(PatientApplication.class, args);
	}

	@Bean
	PasswordEncoder PasswordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Override
	public void run(String... args) throws Exception {
		Patient p = new Patient("simo", new Date(1998 - 01 - 14), true, "adresse", "20000", "060000000", Titre.Mr);

		//Patient pSaved = patientRepository.save(p);

		System.out.println("Patient sauvgarde avec succes  : " + p.getClass());

		Medecin m = new Medecin("test", "mail@gmail.com", "Generaliste");
		//Medecin mSaved = medecinRepository.save(m);
		System.out.println("Medecin sauvgarde avec succes : " + m.getClass());
	}
	//@Bean
	/*CommandLineRunner saveUsers(SecurityService securityService){
		return args ->{
			securityService.saveNewUser("youness","1234","1234");
			securityService.saveNewUser("zanfar","1234","1234");
			securityService.saveNewUser("hamza","1234","1234");

			securityService.saveNewRole("USER","");
			securityService.saveNewRole("ADMIN","");

			securityService.addRoleToUser("hamza","USER");
			securityService.addRoleToUser("youness","ADMIN");
			securityService.addRoleToUser("zanfar","USER");
			securityService.addRoleToUser("hamza","ADMIN");
		};*/
	}
