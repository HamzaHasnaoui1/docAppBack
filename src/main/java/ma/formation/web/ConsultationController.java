package ma.formation.web;

import lombok.AllArgsConstructor;
import ma.formation.entities.Consultation;
import ma.formation.entities.RendezVous;
import ma.formation.repositories.ConsultationRepository;
import ma.formation.repositories.RendezVousRepository;
import ma.formation.service.IHopitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
public class ConsultationController {
    @Autowired
    ConsultationRepository consultationRepository;
    IHopitalService hopitalService;
    RendezVousRepository rendezVousRepository;
    @GetMapping(path= "/user/consultations")
    public String consultation (Model model,
                           @RequestParam(name= "page", defaultValue = "0") int page,
                           @RequestParam(name="size", defaultValue = "5") int size){
        Page<Consultation> pageconsultation= consultationRepository.findAll(PageRequest.of(page, size));

        model.addAttribute("listConsultation",pageconsultation);
        model.addAttribute("pages", new int[pageconsultation.getTotalPages()]);
        model.addAttribute("currentPage", page);
        return "consultation/consultations";
    }

    @GetMapping(path="/admin/deleteConsultation")
    public String deleteConsultation(Long id,  int page){
        consultationRepository.deleteById(id);
        return "redirect:/user/consultations?page="+page;
    }

    @PostMapping(path="/admin/saveConsultation")
    public String saveConsultation(Model model,
                                   @Valid Consultation consultation,
                                   BindingResult bindingResult,
                                   @RequestParam(name = "rendezVous", defaultValue = "0") Long rendezVousId,
                                   @RequestParam(defaultValue = "0") int page){
        if (bindingResult.hasErrors()) {
            return "formConsultation";
        }

        // Récupérer le rendez-vous à partir de son ID
        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rendez-vous Id: " + rendezVousId));

        // Associer le rendez-vous à la consultation
        consultation.setRendezVous(rendezVous);

        // Enregistrer la consultation
        hopitalService.saveConsultation(consultation);

        return "redirect:/user/consultations?page=" + page;
    }


    @GetMapping(path="/admin/formConsultation")
    public String formConsultation(Model model){
        model.addAttribute("consultation",new Consultation());
        model.addAttribute("rendezvous",rendezVousRepository.findAll());
        return "consultation/formConsultation";
    }
    @GetMapping(path="/admin/EditConsultation")
    public String EditConsultation(Model model, Long id,int page){
        Consultation consultation = consultationRepository.findById(id).orElse(null); // avec .get je le recuper s'il existe mais on peut utiliser orElse(null) null s'il ne trouve pas le patient
        if(consultation==null) throw new RuntimeException("Consultation introuvable");
        model.addAttribute("consultation", consultation);
        model.addAttribute("rendezvous",rendezVousRepository.findAll());
        model.addAttribute("page", page);
        return "consultation/EditConsultation";
    }


    @GetMapping(path = "/user/mafacture")
    public String afficherFacture(Model model, Long id) {
        Consultation consultation = consultationRepository.findById(id).orElse(null);
        if (consultation == null) throw new RuntimeException("Consultation introuvable");

        model.addAttribute("consultation", consultation);
        return "facture";
    }


}
