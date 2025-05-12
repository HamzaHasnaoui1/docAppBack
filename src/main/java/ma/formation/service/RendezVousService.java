package ma.formation.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.formation.dtos.RendezVousDTO;
import ma.formation.entities.Medecin;
import ma.formation.entities.Ordonnance;
import ma.formation.entities.Patient;
import ma.formation.entities.RendezVous;
import ma.formation.enums.StatusRDV;
import ma.formation.exceptions.BusinessException;
import ma.formation.exceptions.ResourceNotFoundException;
import ma.formation.mappers.RendezVousMapper;
import ma.formation.repositories.MedecinRepository;
import ma.formation.repositories.OrdonnanceRepository;
import ma.formation.repositories.PatientRepository;
import ma.formation.repositories.RendezVousRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RendezVousService {
    private final RendezVousRepository rendezVousRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final RendezVousMapper rendezVousMapper;
    private final OrdonnanceRepository ordonnanceRepository;


    @Transactional(readOnly = true)
    public List<RendezVousDTO> getAllRendezVous() {
        return rendezVousRepository.findAll().stream()
                .map(rendezVousMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<RendezVousDTO> getRendezVousPaginated(int page, int size) {
        Page<RendezVous> rendezVousPage = rendezVousRepository.findAll(PageRequest.of(page, size));
        List<RendezVousDTO> rendezVousDTOs = rendezVousPage.getContent().stream()
                .map(rendezVousMapper::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(rendezVousDTOs, rendezVousPage.getPageable(), rendezVousPage.getTotalElements());
    }


    public Optional<RendezVousDTO> getRendezVousById(Long id) {
        return rendezVousRepository.findById(id).map(rendezVousMapper::toDTO);
    }


    @Transactional
    public RendezVousDTO createRendezVous(RendezVousDTO rendezVousDTO) {
        // 1. Check if the doctor is available
        Medecin medecin = medecinRepository.findById(rendezVousDTO.getMedecin().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Medecin", "id", rendezVousDTO.getMedecin().getId()));
        Patient patient = patientRepository.findById(rendezVousDTO.getPatient().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", rendezVousDTO.getPatient().getId()));

        Date dateRdv = rendezVousDTO.getDate();
        Date dateRdvFin = rendezVousDTO.getDateFin();
        List<RendezVous> existing = (dateRdvFin == null)
                ? rendezVousRepository.findByMedecinIdAndDate(medecin.getId(), dateRdv)
                : rendezVousRepository.findByMedecinAndDateBetween(medecin.getId(), dateRdv, dateRdvFin);
        if (!existing.isEmpty()) {
            throw new BusinessException("Le médecin a déjà un rendez-vous planifié à cette date et heure");
        }

        // 2. Convert DTO to Entity
        RendezVous rendezVous = rendezVousMapper.toEntity(rendezVousDTO);

        rendezVous.setPatient(patient);
        // 3. Handle Ordonnance (if it exists)
        if (rendezVous.getOrdonnance() != null && rendezVous.getOrdonnance().getId() != null) {
            Ordonnance managedOrdonnance = ordonnanceRepository.findById(rendezVous.getOrdonnance().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ordonnance", "id", rendezVous.getOrdonnance().getId()));
            rendezVous.setOrdonnance(managedOrdonnance);
        }

        // 4. Save the RendezVous
        RendezVous saved = rendezVousRepository.save(rendezVous);
        return rendezVousMapper.toDTO(saved);
    }


    @Transactional
    public RendezVousDTO updateRendezVous(Long id, RendezVousDTO rendezVousDTO) {
        RendezVous existing = rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RendezVous not found"));

        rendezVousMapper.updateFromDto(rendezVousDTO, existing);

        RendezVous updated = rendezVousRepository.save(existing);
        return rendezVousMapper.toDTO(updated);
    }


    @Transactional
    public void deleteRendezVous(Long id) {
        if (!rendezVousRepository.existsById(id)) {
            throw new ResourceNotFoundException("RendezVous", "id", id);
        }
        rendezVousRepository.deleteById(id);
    }

    @Transactional
    public RendezVousDTO changeRendezVousStatus(Long id, String status) {
        RendezVous rendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RendezVous", "id", id));

        try {
            StatusRDV newStatus = StatusRDV.valueOf(status);
            rendezVous.setStatusRDV(newStatus);
            RendezVous updatedRendezVous = rendezVousRepository.save(rendezVous);
            return rendezVousMapper.toDTO(updatedRendezVous);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Statut de rendez-vous invalide: " + status);
        }
    }

    @Transactional(readOnly = true)
    public List<RendezVousDTO> getRendezVousByPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient", "id", patientId);
        }

        List<RendezVous> rendezVous = rendezVousRepository.findByPatient_Id(patientId);
        return rendezVous.stream()
                .map(rendezVousMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RendezVousDTO> getRendezVousByMedecin(Long medecinId) {
        if (!medecinRepository.existsById(medecinId)) {
            throw new ResourceNotFoundException("Medecin", "id", medecinId);
        }

        List<RendezVous> rendezVous = rendezVousRepository.findByMedecin_Id(medecinId);
        return rendezVous.stream()
                .map(rendezVousMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RendezVousDTO> getRendezVousByDate(Date date) {
        List<RendezVous> rendezVous = rendezVousRepository.findByDate(date);
        return rendezVous.stream()
                .map(rendezVousMapper::toDTO)
                .collect(Collectors.toList());
    }
}