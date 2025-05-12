package ma.formation.service;

import lombok.RequiredArgsConstructor;
import ma.formation.dtos.DonneesPhysiologiquesDTO;
import ma.formation.entities.DonneesPhysiologiques;
import ma.formation.entities.RendezVous;
import ma.formation.exceptions.ResourceNotFoundException;
import ma.formation.mappers.DonneesPhysiologiquesMapper;
import ma.formation.repositories.DonneesPhysiologiquesRepository;
import ma.formation.repositories.RendezVousRepository;
import ma.formation.utils.MedicalCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DonneesPhysiologiquesService {
    private final DonneesPhysiologiquesRepository donneesPhysiologiquesRepository;
    private final RendezVousRepository rendezVousRepository;
    private final DonneesPhysiologiquesMapper donneesPhysiologiquesMapper;

    // Dans votre DonneesPhysiologiquesService
    @Transactional
    public DonneesPhysiologiquesDTO saveDonneesPhysiologiques(DonneesPhysiologiquesDTO dto, Long rendezVousId) {
        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new ResourceNotFoundException("RendezVous", "id", rendezVousId));

        // Convertir DTO en entité
        DonneesPhysiologiques donneesPhysiologiques = donneesPhysiologiquesMapper.toEntity(dto);
        donneesPhysiologiques.setRendezVous(rendezVous);

        // Utiliser MedicalCalculator pour calculer l'IMC
        if (dto.getPoids() != null && dto.getTaille() != null && dto.getTaille() > 0) {
            double imc = MedicalCalculator.calculateIMC(dto.getPoids(), dto.getTaille());
            donneesPhysiologiques.setImc(imc);
        }

        // Enregistrer les données
        DonneesPhysiologiques savedData = donneesPhysiologiquesRepository.save(donneesPhysiologiques);

        // Convertir l'entité en DTO pour le retour
        return donneesPhysiologiquesMapper.toDTO(savedData);
    }

    @Transactional(readOnly = true)
    public DonneesPhysiologiquesDTO getDonneesPhysiologiquesByRendezVous(Long rendezVousId) {
        return donneesPhysiologiquesRepository.findByRendezVousId(rendezVousId)
                .map(donneesPhysiologiquesMapper::toDTO)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<DonneesPhysiologiquesDTO> getLatestDonneesPhysiologiquesByPatient(Long patientId) {
        List<DonneesPhysiologiques> donneesPhysiologiques = donneesPhysiologiquesRepository.findLatestByPatientId(patientId);
        return donneesPhysiologiquesMapper.toDTOs(donneesPhysiologiques);
    }

    @Transactional(readOnly = true)
    public List<DonneesPhysiologiquesDTO> getDonneesPhysiologiquesByPatientAndDateRange(
            Long patientId, Date startDate, Date endDate) {
        List<DonneesPhysiologiques> donneesPhysiologiques =
                donneesPhysiologiquesRepository.findByPatientIdAndDateRange(patientId, startDate, endDate);
        return donneesPhysiologiquesMapper.toDTOs(donneesPhysiologiques);
    }

    private void updateEntityFromDTO(DonneesPhysiologiques entity, DonneesPhysiologiquesDTO dto) {
        if (dto.getPoids() != null) entity.setPoids(dto.getPoids());
        if (dto.getTaille() != null) entity.setTaille(dto.getTaille());
        if (dto.getOeilDroit() != null) entity.setOeilDroit(dto.getOeilDroit());
        if (dto.getOeilGauche() != null) entity.setOeilGauche(dto.getOeilGauche());
        if (dto.getTensionSystolique() != null) entity.setTensionSystolique(dto.getTensionSystolique());
        if (dto.getTensionDiastolique() != null) entity.setTensionDiastolique(dto.getTensionDiastolique());
        if (dto.getFrequenceCardiaque() != null) entity.setFrequenceCardiaque(dto.getFrequenceCardiaque());
        if (dto.getFrequenceRespiratoire() != null) entity.setFrequenceRespiratoire(dto.getFrequenceRespiratoire());
        if (dto.getTemperature() != null) entity.setTemperature(dto.getTemperature());
        if (dto.getGlycemie() != null) entity.setGlycemie(dto.getGlycemie());
        if (dto.getRemarques() != null) entity.setRemarques(dto.getRemarques());

        // Recalculer l'IMC si nécessaire
        if ((dto.getPoids() != null || dto.getTaille() != null) && entity.getPoids() != null && entity.getTaille() != null && entity.getTaille() > 0) {
            double tailleEnMetres = entity.getTaille() / 100.0;
            double imc = entity.getPoids() / (tailleEnMetres * tailleEnMetres);
            entity.setImc(Math.round(imc * 10.0) / 10.0);
        }
    }
}