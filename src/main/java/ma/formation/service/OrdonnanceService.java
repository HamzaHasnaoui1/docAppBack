package ma.formation.service;

import lombok.RequiredArgsConstructor;
import ma.formation.dtos.OrdonnanceDTO;
import ma.formation.dtos.OrdonnanceMedicamentDTO;
import ma.formation.dtos.OrdonnanceRequest;
import ma.formation.entities.Medicament;
import ma.formation.entities.Ordonnance;
import ma.formation.entities.OrdonnanceMedicament;
import ma.formation.entities.RendezVous;
import ma.formation.exceptions.ResourceNotFoundException;
import ma.formation.mappers.OrdonnanceMapper;
import ma.formation.repositories.MedicamentRepository;
import ma.formation.repositories.OrdonnanceMedicamentRepository;
import ma.formation.repositories.OrdonnanceRepository;
import ma.formation.repositories.RendezVousRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdonnanceService {
    private final OrdonnanceRepository ordonnanceRepository;
    private final RendezVousRepository rendezVousRepository;
    private final MedicamentRepository medicamentRepository;
    private final OrdonnanceMedicamentRepository ordonnanceMedicamentRepository;
    private final OrdonnanceMapper ordonnanceMapper;

    @Transactional(readOnly = true)
    public Page<OrdonnanceDTO> getAllOrdonnances(String keyword, int page, int size) {
        Page<Ordonnance> ordonnances = ordonnanceRepository.findByRendezVous_Patient_NomContains(keyword, PageRequest.of(page, size));
        List<OrdonnanceDTO> dtos = ordonnances.getContent().stream()
                .map(ordonnanceMapper::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, ordonnances.getPageable(), ordonnances.getTotalElements());
    }

    @Transactional(readOnly = true)
    public OrdonnanceDTO getOrdonnanceById(Long id) {
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
    
    // Nouvelle méthode pour créer une ordonnance avec des médicaments
    @Transactional
    public OrdonnanceDTO createOrdonnance(Long rdvId, OrdonnanceRequest request) {
        RendezVous rendezVous = rendezVousRepository.findById(rdvId)
                .orElseThrow(() -> new ResourceNotFoundException("RendezVous", "id", rdvId));

        // Vérifier si le rendez-vous a déjà une ordonnance
        if (ordonnanceRepository.existsByRendezVous_Id(rdvId)) {
            throw new IllegalStateException("Ce rendez-vous possède déjà une ordonnance");
        }

        Ordonnance ordonnance = new Ordonnance();
        ordonnance.setContenu(request.getContenu());
        ordonnance.setRemarques(request.getRemarques());
        ordonnance.setDateEmission(LocalDate.now());
        ordonnance.setRendezVous(rendezVous);
        ordonnance.setArchivee(request.getArchivee() != null ? request.getArchivee() : false);
        
        // Sauvegarder l'ordonnance d'abord pour obtenir son ID
        Ordonnance savedOrdonnance = ordonnanceRepository.save(ordonnance);
        
        // Ajouter les médicaments si présents
        if (request.getMedicaments() != null && !request.getMedicaments().isEmpty()) {
            List<OrdonnanceMedicament> medicaments = new ArrayList<>();
            
            for (OrdonnanceMedicamentDTO medicamentDTO : request.getMedicaments()) {
                Medicament medicament = medicamentRepository.findById(medicamentDTO.getMedicament().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Medicament", "id", medicamentDTO.getMedicament().getId()));
                
                OrdonnanceMedicament ordonnanceMedicament = new OrdonnanceMedicament();
                ordonnanceMedicament.setOrdonnance(savedOrdonnance);
                ordonnanceMedicament.setMedicament(medicament);
                ordonnanceMedicament.setPosologie(medicamentDTO.getPosologie());
                ordonnanceMedicament.setDuree(medicamentDTO.getDuree());
                ordonnanceMedicament.setFrequence(medicamentDTO.getFrequence());
                ordonnanceMedicament.setInstructions(medicamentDTO.getInstructions());
                
                medicaments.add(ordonnanceMedicament);
            }
            
            ordonnanceMedicamentRepository.saveAll(medicaments);
            savedOrdonnance.setMedicaments(medicaments);
        }
        
        return ordonnanceMapper.toDTO(savedOrdonnance);
    }

    // Méthode mise à jour pour modifier une ordonnance avec des médicaments
    @Transactional
    public OrdonnanceDTO updateOrdonnance(Long id, OrdonnanceRequest request) {
        Ordonnance existingOrdonnance = ordonnanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordonnance", "id", id));

        // Mettre à jour les informations de base
        existingOrdonnance.setContenu(request.getContenu());
        existingOrdonnance.setRemarques(request.getRemarques());
        existingOrdonnance.setArchivee(request.getArchivee() != null ? request.getArchivee() : existingOrdonnance.isArchivee());

        // Supprimer les médicaments existants
        if (existingOrdonnance.getMedicaments() != null) {
            ordonnanceMedicamentRepository.deleteAll(existingOrdonnance.getMedicaments());
            existingOrdonnance.getMedicaments().clear();
        }
        
        // Ajouter les nouveaux médicaments
        if (request.getMedicaments() != null && !request.getMedicaments().isEmpty()) {
            List<OrdonnanceMedicament> medicaments = new ArrayList<>();
            
            for (OrdonnanceMedicamentDTO medicamentDTO : request.getMedicaments()) {
                Medicament medicament = medicamentRepository.findById(medicamentDTO.getMedicament().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Medicament", "id", medicamentDTO.getMedicament().getId()));
                
                OrdonnanceMedicament ordonnanceMedicament = new OrdonnanceMedicament();
                ordonnanceMedicament.setOrdonnance(existingOrdonnance);
                ordonnanceMedicament.setMedicament(medicament);
                ordonnanceMedicament.setPosologie(medicamentDTO.getPosologie());
                ordonnanceMedicament.setDuree(medicamentDTO.getDuree());
                ordonnanceMedicament.setFrequence(medicamentDTO.getFrequence());
                ordonnanceMedicament.setInstructions(medicamentDTO.getInstructions());
                
                medicaments.add(ordonnanceMedicament);
            }
            
            ordonnanceMedicamentRepository.saveAll(medicaments);
            existingOrdonnance.setMedicaments(medicaments);
        }

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