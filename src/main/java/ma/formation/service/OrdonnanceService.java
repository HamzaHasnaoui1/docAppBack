package ma.formation.service;

import lombok.RequiredArgsConstructor;
import ma.formation.dtos.OrdonnanceDTO;
import ma.formation.entities.Ordonnance;
import ma.formation.entities.RendezVous;
import ma.formation.exceptions.ResourceNotFoundException;
import ma.formation.mappers.OrdonnanceMapper;
import ma.formation.repositories.OrdonnanceRepository;
import ma.formation.repositories.RendezVousRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdonnanceService {
    private final OrdonnanceRepository ordonnanceRepository;
    private final RendezVousRepository rendezVousRepository;
    private final OrdonnanceMapper ordonnanceMapper;

    @Transactional(readOnly = true)
    public Page<OrdonnanceDTO> searchOrdonnances(String keyword, int page, int size) {
        Page<Ordonnance> ordonnances = ordonnanceRepository.findByRendezVous_Patient_NomContains(keyword, PageRequest.of(page, size));
        List<OrdonnanceDTO> dtos = ordonnances.getContent().stream()
                .map(ordonnanceMapper::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, ordonnances.getPageable(), ordonnances.getTotalElements());
    }

    @Transactional(readOnly = true)
    public OrdonnanceDTO getOrdonnance(Long id) {
        return ordonnanceRepository.findById(id)
                .map(ordonnanceMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Ordonnance", "id", id));
    }

    // Méthode originale conservée pour compatibilité
    @Transactional
    public OrdonnanceDTO createOrdonnance(OrdonnanceDTO ordonnanceDTO, Long consultationId) {
        RendezVous consultation = rendezVousRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation", "id", consultationId));

        // Check if consultation already has an ordonnance
        if (ordonnanceRepository.existsByRendezVous_Id(consultationId)) {
            throw new IllegalStateException("Cette consultation possède déjà une ordonnance");
        }

        Ordonnance ordonnance = ordonnanceMapper.toEntity(ordonnanceDTO);
        ordonnance.setDateEmission(LocalDate.now());
        ordonnance.setRendezVous(consultation);
        ordonnance.setArchivee(false);

        Ordonnance savedOrdonnance = ordonnanceRepository.save(ordonnance);
        return ordonnanceMapper.toDTO(savedOrdonnance);
    }

    @Transactional
    public OrdonnanceDTO updateOrdonnance(Long id, OrdonnanceDTO ordonnanceDTO) {
        Ordonnance existingOrdonnance = ordonnanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordonnance", "id", id));

        // Update basic info
        existingOrdonnance.setContenu(ordonnanceDTO.getContenu());
        existingOrdonnance.setRemarques(ordonnanceDTO.getRemarques());
        existingOrdonnance.setArchivee(ordonnanceDTO.isArchivee());

        Ordonnance updatedOrdonnance = ordonnanceRepository.save(existingOrdonnance);
        return ordonnanceMapper.toDTO(updatedOrdonnance);
    }

    @Transactional
    public void deleteOrdonnance(Long id) {
        if (!ordonnanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ordonnance", "id", id);
        }
        ordonnanceRepository.deleteById(id);
    }

    @Transactional
    public OrdonnanceDTO archiverOrdonnance(Long id) {
        Ordonnance ordonnance = ordonnanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordonnance", "id", id));

        ordonnance.setArchivee(true);
        Ordonnance updatedOrdonnance = ordonnanceRepository.save(ordonnance);
        return ordonnanceMapper.toDTO(updatedOrdonnance);
    }

    @Transactional(readOnly = true)
    public List<OrdonnanceDTO> getOrdonnancesByPatient(Long patientId) {
        List<Ordonnance> ordonnances = ordonnanceRepository.findByPatientId(patientId);
        return ordonnances.stream()
                .map(ordonnanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<OrdonnanceDTO> getOrdonnancesByPatientPageable(Long patientId, int page, int size) {
        Page<Ordonnance> ordonnances = ordonnanceRepository.findByPatientIdPageable(patientId, PageRequest.of(page, size));
        List<OrdonnanceDTO> dtos = ordonnances.getContent().stream()
                .map(ordonnanceMapper::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, ordonnances.getPageable(), ordonnances.getTotalElements());
    }
}