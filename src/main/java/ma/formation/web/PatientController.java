package ma.formation.web;

import lombok.AllArgsConstructor;
import ma.formation.entities.Patient;
import ma.formation.entities.Titre;
import ma.formation.repositories.PatientRepository;
import ma.formation.service.IHospitalServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

//classe qui va gérer les requetés http
@Controller
@AllArgsConstructor
public class PatientController {
    private PatientRepository patientRepository;
    IHospitalServiceImpl iHospitalService;

    @GetMapping(path= "/user/patients") //route
    // fct patients() appelée => si une requête de type GET est envoyée sur GetMapping
    public String patients(Model model,
                           @RequestParam(name= "page", defaultValue = "0") int page, // parametre d'url : request.getparametre(page), si on specifie pas le parametre il va prendre la valeur 0 par defaut
                           @RequestParam(name= "size", defaultValue = "5") int size,
                           @RequestParam(name="keyword", defaultValue = "") String keyword){
        Page<Patient> pagePatients = patientRepository.findByNomContains(keyword, PageRequest.of(page,size));// charger les patients a partir de la db
        //stocker la liste dans le model
        //model creer des variables, permet de recuperer les données aupres de la vue
        //  je veux les patients de la page 0 et size 5
        model.addAttribute("patientService", iHospitalService);
        model.addAttribute("listpatients", pagePatients.getContent()); // getcontent donne la liste des patients de la page
        model.addAttribute("pages", new int[pagePatients.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword",keyword);
        return "patient/patients";// nom de la vue
    }

    //
    @GetMapping(path = "/user/patientss")
    @ResponseBody
    public  List<Patient> listPatients(){
        return  patientRepository.findAll();
    }

    @GetMapping(path="/admin/delete")
    public String delete(Long id, String keyword, int page){
        patientRepository.deleteById(id);
        return "redirect:/user/patients?page="+page+"&keyword="+keyword;
    }

    @GetMapping(path="/admin/formPatients")
    public String formPatients(Model model){
        model.addAttribute("patient",new Patient());
        return "patient/formPatients";
    }
    @PostMapping(path="/admin/save")
    public String save(Model model,// =>BindingResult place les erreurs dans le model
                       @Valid Patient patient,
                       BindingResult bindingResult, // gener la liste des erreurs
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "") String keyword){
        if (bindingResult.hasErrors()) return "formPatients";
        patientRepository.save(patient);
        return "redirect:/user/patients?page="+page+"&keyword="+keyword;
    }


    @GetMapping(path="/admin/EditPatient")
    public String EditPatient(Model model, Long id, String keyword, int page){
        Patient patient = patientRepository.findById(id).orElse(null); // avec .get je le recuper s'il existe mais on peut utiliser orElse(null) null s'il ne trouve pas le patient
        if(patient==null) throw new RuntimeException("Patient introuvable");
        model.addAttribute("patient", patient);
        model.addAttribute("page", page);
        model.addAttribute("keyword",keyword);
        return "patient/EditPatient";
    }
    @GetMapping(path="/user/listPatient")
    public String listPatient(Model model, Long id,
                              @RequestParam(defaultValue = "") String keyword,
                              @RequestParam(defaultValue = "0") int page){
        Patient patient = patientRepository.findById(id).get();
        model.addAttribute("patient",patient);
        model.addAttribute("keyword",keyword);
        model.addAttribute("page",page);
        return "patient/listPatient";
    }

}
