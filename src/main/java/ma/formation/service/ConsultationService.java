package ma.formation.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import ma.formation.dtos.ConsultationDTO;
import ma.formation.dtos.PatientDTO;
import ma.formation.entities.Consultation;
import ma.formation.entities.Ordonnance;
import ma.formation.entities.Patient;
import ma.formation.entities.RendezVous;
import ma.formation.enums.StatusRDV;
import ma.formation.mappers.ConsultationMapper;
import ma.formation.repositories.ConsultationRepository;
import ma.formation.repositories.OrdonnanceRepository;
import ma.formation.repositories.RendezVousRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final RendezVousRepository rendezVousRepository;
    private final ConsultationMapper consultationMapper;
    private final OrdonnanceRepository ordonnanceRepository;


    public Page<ConsultationDTO> searchConsultations(String keyword, int page, int size) {
        Page<Consultation> consultations = consultationRepository.findByRendezVous_Patient_NomContainingIgnoreCase(keyword, PageRequest.of(page, size));
        List<ConsultationDTO> dtos = consultations.getContent().stream().map(consultationMapper::toDTO).toList();
        return new PageImpl<>(dtos, consultations.getPageable(), consultations.getTotalElements());
    }

    public List<ConsultationDTO> getAllConsultations() {
        return consultationRepository.findAll().stream()
                .map(consultationMapper::toDTO)
                .toList();
    }


    public ConsultationDTO createConsultation(ConsultationDTO consultationDTO, Long rendezVousId) {
        // Vérifie si une consultation existe déjà pour ce rendez-vous
        if (consultationRepository.existsByRendezVous_Id(rendezVousId)) {
            throw new IllegalStateException("❌ Une consultation existe déjà pour ce rendez-vous.");
        }

        // Récupérer le rendez-vous
        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new NotFoundException("Rendez-vous non trouvé"));

        // Créer la consultation
        Consultation consultation = Consultation.builder()
                .dateConsultation(consultationDTO.getDateConsultation())
                .rapport(consultationDTO.getRapport())
                .statusRDV(StatusRDV.DONE)
                .prix(consultationDTO.getPrix())
                .build();

        // Lier les deux entités
        consultation.setRendezVous(rendezVous);
        rendezVous.setConsultation(consultation);
        rendezVous.setStatusRDV(StatusRDV.DONE);

        // Sauvegarde unique — Hibernate s’occupe du reste grâce au cascade
        rendezVousRepository.save(rendezVous);

        return consultationMapper.toDTO(consultation);
    }



    public ConsultationDTO updateConsultation(Long id, ConsultationDTO consultationDTO) {
        Consultation existingConsultation = consultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation non trouvée"));

        Consultation updatedConsultation = Consultation.builder()
                .id(id)
                .dateConsultation(consultationDTO.getDateConsultation())
                .rapport(consultationDTO.getRapport())
                .statusRDV(StatusRDV.valueOf(consultationDTO.getStatusRDV()))
                .prix(consultationDTO.getPrix())
                .rendezVous(existingConsultation.getRendezVous())
                .dossierMedical(existingConsultation.getDossierMedical())
                .ordonnance(existingConsultation.getOrdonnance())
                .build();

        Consultation savedConsultation = consultationRepository.save(updatedConsultation);
        return consultationMapper.toDTO(savedConsultation);
    }

    public void deleteConsultation(Long id) {
        consultationRepository.deleteById(id);
    }

    public ConsultationDTO getConsultationById(Long id) {
        return consultationRepository.findById(id)
                .map(consultationMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Consultation non trouvée"));
    }

    public Map<String, Object> getConsultationFormData() {
        Map<String, Object> response = new HashMap<>();
        response.put("rendezvous", rendezVousRepository.findAll());
        return response;
    }
}