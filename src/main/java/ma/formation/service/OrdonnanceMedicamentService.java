package ma.formation.service;

import lombok.RequiredArgsConstructor;
import ma.formation.dtos.OrdonnanceMedicamentDTO;
import ma.formation.entities.Medicament;
import ma.formation.entities.Ordonnance;
import ma.formation.entities.OrdonnanceMedicament;
import ma.formation.exceptions.ResourceNotFoundException;
import ma.formation.mappers.OrdonnanceMedicamentMapper;
import ma.formation.repositories.MedicamentRepository;
import ma.formation.repositories.OrdonnanceMedicamentRepository;
import ma.formation.repositories.OrdonnanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdonnanceMedicamentService {
    private final OrdonnanceMedicamentRepository ordonnanceMedicamentRepository;
    private final OrdonnanceRepository ordonnanceRepository;
    private final MedicamentRepository medicamentRepository;
    private final OrdonnanceMedicamentMapper ordonnanceMedicamentMapper;

    @Transactional(readOnly = true)
    public List<OrdonnanceMedicamentDTO> getMedicamentsByOrdonnance(Long ordonnanceId) {
        if (!ordonnanceRepository.existsById(ordonnanceId)) {
            throw new ResourceNotFoundException("Ordonnance", "id", ordonnanceId);
        }

        List<OrdonnanceMedicament> medicaments = ordonnanceMedicamentRepository.findByOrdonnanceId(ordonnanceId);
        return ordonnanceMedicamentMapper.toDTOs(medicaments);
    }

    @Transactional
    public OrdonnanceMedicamentDTO ajouterMedicament(Long ordonnanceId, OrdonnanceMedicamentDTO dto) {
        Ordonnance ordonnance = ordonnanceRepository.findById(ordonnanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordonnance", "id", ordonnanceId));

        Medicament medicament = medicamentRepository.findById(dto.getMedicament().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Medicament", "id", dto.getMedicament().getId()));

        OrdonnanceMedicament ordonnanceMedicament = new OrdonnanceMedicament();
        ordonnanceMedicament.setOrdonnance(ordonnance);
        ordonnanceMedicament.setMedicament(medicament);
        ordonnanceMedicament.setPosologie(dto.getPosologie());
        ordonnanceMedicament.setDuree(dto.getDuree());
        ordonnanceMedicament.setFrequence(dto.getFrequence());
        ordonnanceMedicament.setInstructions(dto.getInstructions());

        OrdonnanceMedicament savedEntity = ordonnanceMedicamentRepository.save(ordonnanceMedicament);
        return ordonnanceMedicamentMapper.toDTO(savedEntity);
    }

    @Transactional
    public OrdonnanceMedicamentDTO updateMedicament(Long id, OrdonnanceMedicamentDTO dto) {
        OrdonnanceMedicament entity = ordonnanceMedicamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrdonnanceMedicament", "id", id));

        entity.setPosologie(dto.getPosologie());
        entity.setDuree(dto.getDuree());
        entity.setFrequence(dto.getFrequence());
        entity.setInstructions(dto.getInstructions());

        OrdonnanceMedicament updatedEntity = ordonnanceMedicamentRepository.save(entity);
        return ordonnanceMedicamentMapper.toDTO(updatedEntity);
    }

    @Transactional
    public void supprimerMedicament(Long id) {
        if (!ordonnanceMedicamentRepository.existsById(id)) {
            throw new ResourceNotFoundException("OrdonnanceMedicament", "id", id);
        }

        ordonnanceMedicamentRepository.deleteById(id);
    }
}