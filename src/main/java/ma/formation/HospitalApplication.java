package ma.formation;

import lombok.AllArgsConstructor;
import ma.formation.entities.*;
import ma.formation.enums.StatusRDV;
import ma.formation.enums.Titre;
import ma.formation.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@SpringBootApplication
@AllArgsConstructor
public class HospitalApplication implements CommandLineRunner {
    private PatientRepository patientRepository;
    private MedecinRepository medecinRepository;
    private RendezVousRepository rendezVousRepository;
    private ConsultationRepository consultationRepository;

    public static void main(String[] args) {

        SpringApplication.run(HospitalApplication.class, args);
    }

    @Bean
    PasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void run(String... args) throws Exception {
        //Patient p = new Patient("simo", new Date(1998 - 01 - 14), true, "adresse", "20000", "060000000", Titre.Mr,"aa");

        //Patient pSaved = patientRepository.save(p);

        //System.out.println("Patient sauvgarde avec succes  : " + p.getClass());

        //Medecin m = new Medecin("test", "mail@gmail.com", "Generaliste","060000000");
        //Medecin mSaved = medecinRepository.save(m);
        //System.out.println("Medecin sauvgarde avec succes : " + m.getClass());
    }
  /*  @Bean
    CommandLineRunner initData(
            PatientRepository patientRepository,
            DossierMedicalRepository dossierMedicalRepository,
            RendezVousRepository rendezVousRepository,
            ConsultationRepository consultationRepository,
            OrdonnanceRepository ordonnanceRepository,
            MedecinRepository medecinRepository
    ) {
        return args -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Medecin medecin = new Medecin(null, "Dr House", "house@mail.com", "Diagnostique", "0601010101");
            medecin = medecinRepository.save(medecin);

            for (int i = 1; i <= 8; i++) {
                Patient patient = new Patient();
                patient.setNom("Patient" + i);
                patient.setDateNaissance(sdf.parse("1990-01-0" + i));
                patient.setMalade(i % 2 == 0);
                patient.setAdresse("Rue " + i);
                patient.setCodePostal("1000" + i);
                patient.setNumeroTelephone("06000000" + i);
                patient.setTitre(Titre.Mr);
                patient.setRapport("Rapport médical initial");

                Patient savedPatient = patientRepository.save(patient);

                DossierMedical dossier = new DossierMedical();
                dossier.setPatient(savedPatient);
                dossier.setAllergies("Allergie " + i);
                dossier.setAntecedents("Aucun");
                dossier.setTraitementsChroniques("Aucun");
                dossierMedicalRepository.save(dossier);

                RendezVous rdv = new RendezVous();
                rdv.setDate(new Date());
                rdv.setStatusRDV(StatusRDV.PENDING);
                rdv.setPatient(savedPatient);
                rdv.setMedecin(medecin);
                rdv = rendezVousRepository.save(rdv);

                Consultation consultation = new Consultation();
                consultation.setDateConsultation(new Date());
                consultation.setRapport("Consultation OK");
                consultation.setStatusRDV(StatusRDV.DONE);
                consultation.setPrix("250");
                consultation.setRendezVous(rdv);
                consultation.setDossierMedical(dossier);
                consultation = consultationRepository.save(consultation);

                Ordonnance ordonnance = new Ordonnance();
                ordonnance.setConsultation(consultation);
                ordonnance.setContenu("Doliprane 1000mg 2x/jour");
                ordonnance.setDateEmission(LocalDate.now());
                ordonnanceRepository.save(ordonnance);
            }

            System.out.println("✅ 8 Patients + RDV + Consultations + Ordonnances générés.");
        };
    }*/
    /*@Bean
    CommandLineRunner savePatient (PatientRepository patientRepository , DossierMedicalRepository dossierMedicalRepository) {
        return (args) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Patient patient = new Patient();
            patient.setNom("Hamza");
            patient.setDateNaissance(sdf.parse("1998-05-12"));
            patient.setMalade(false);
            patient.setAdresse("Casablanca");
            patient.setCodePostal("20000");
            patient.setNumeroTelephone("0600000000");
            patient.setTitre(Titre.Mr);
            patient.setRapport("Rapport initial");

            Patient savedPatient = patientRepository.save(patient);

            DossierMedical dossier = new DossierMedical();
            dossier.setPatient(savedPatient);
            dossier.setAllergies("Aucune");
            dossier.setAntecedents("Aucun");
            dossier.setTraitementsChroniques("Aucun");

            dossierMedicalRepository.save(dossier);

            System.out.println("✅ Patient et Dossier Médical créés avec succès.");
        };
    }*/
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
