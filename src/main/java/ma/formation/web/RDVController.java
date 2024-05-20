package ma.formation.web;

import lombok.AllArgsConstructor;
import ma.formation.entities.Medecin;
import ma.formation.entities.Patient;
import ma.formation.entities.RendezVous;
import ma.formation.repositories.MedecinRepository;
import ma.formation.repositories.PatientRepository;
import ma.formation.repositories.RendezVousRepository;
import ma.formation.service.IHopitalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class RDVController {
    RendezVousRepository rendezVousRepository;
    PatientRepository patientRepository;
    MedecinRepository medecinRepository;
    IHopitalService hopitalService;

    @GetMapping(path = "user/rdv")
    public String Rendezvous(Model model,
                             @RequestParam(name = "page", defaultValue = "0") int page, // parametre d'url : request.getparametre(page), si on specifie pas le parametre il va prendre la valeur 0 par defaut
                             @RequestParam(name = "size", defaultValue = "5") int size

    ) {
        Page<RendezVous> pageRDV = rendezVousRepository.findAll(PageRequest.of(page, size));
        model.addAttribute("listRDV", pageRDV.getContent()); // getcontent donne la liste des patients de la page
        model.addAttribute("pages", new int[pageRDV.getTotalPages()]);
        model.addAttribute("currentPage", page);
        return "RDV/Rendezvous";// nom de la vue
    }

    @GetMapping(path = "/admin/formRendezVous")
    public String formRendezVous(Model model/*,String nomPatient,String nomMedecin*/) {
        model.addAttribute("rendezvous", new RendezVous());
        /*model.addAttribute("nomPatient",nomPatient);
        model.addAttribute("nomMedecin",nomMedecin);*/
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("medecins", medecinRepository.findAll());
        return "RDV/formRDV";
    }
    @PostMapping(path="/admin/saveRDV")
    public String saveRDV(Model model, @Valid RendezVous rendezVous,String nomPatient,String nomMedecin,
                       BindingResult bindingResult, // =>stock les erreurs
                       @RequestParam(defaultValue = "0") int page){
        if (bindingResult.hasErrors()) return "RDV/formRDV";

       /* Long pid= (Long) model.getAttribute("monPid");
        Long mid= (Long) model.getAttribute("monMid");
        System.out.println("printing pid and mid to check if we get them");
        System.out.println("pid="+ pid);
        System.out.println("mid="+ mid);

        Patient patient= patientRepository.findById(pid).orElse(null);
        Medecin medecin= medecinRepository.findById(mid).orElse(null);
        rendezVous.setPatient(patient);
        rendezVous.setMedecin(medecin);
        System.out.println(patient);
       Patient patient= (Patient) model.getAttribute("patient");
        Medecin medecin = (Medecin) model.getAttribute("medecin");
        rendezVous.setPatient(patient);
        rendezVous.setMedecin(medecin);*/

       /* Patient patient = patientRepository.findByNom(nomPatient);
        Medecin medecin = medecinRepository.findByNom(nomMedecin);
        if (patient != null && rendezVous.getPatient()==null && medecin != null && rendezVous.getMedecin()==null){
            rendezVous.setPatient(patient);
            rendezVous.setMedecin(medecin);
            rendezVousRepository.save(rendezVous);
            return "redirect:/user/rdv";

        }
        else{
            return "redirect:/admin/formRendezVous";
        }
        */
        hopitalService.saveRendezVous(rendezVous);
        return "redirect:/user/rdv?page="+page;
    }
    @GetMapping(path="/admin/deleteRDV")
    public String deleteRDV(Long id,  int page){
        rendezVousRepository.deleteById(id);
        return "redirect:/user/rdv?page="+page;
    }
    @GetMapping(path="/admin/EditRDV")
    public String EditRDV(Model model, Long id,int page){
        RendezVous rendezVous = rendezVousRepository.findById(id).orElse(null); // avec .get je le recuper s'il existe mais on peut utiliser orElse(null) null s'il ne trouve pas le patient
        List<Patient> listeDesPatients= patientRepository.findAll();
        List<Medecin> listeDesMedecins=medecinRepository.findAll();
        if(rendezVous==null) throw new RuntimeException("Rendez-vous introuvable");
        model.addAttribute("rendezVous", rendezVous);
        model.addAttribute("page", page);
        model.addAttribute("listeDesPatients", listeDesPatients );
        model.addAttribute("listeDesMedecins",listeDesMedecins );
        return "RDV/EditRDV";
    }

}


