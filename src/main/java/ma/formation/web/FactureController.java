package ma.formation.web;

import lombok.AllArgsConstructor;
import ma.formation.entities.Patient;
import ma.formation.repositories.FactureRepository;
import ma.formation.service.FactureService;
import ma.formation.service.IHospitalServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class FactureController {
FactureRepository factureRepository;
    IHospitalServiceImpl iHospitalService;
    FactureService factureService;

    @GetMapping(path="/user/facture")
    public String pdf(){
        return "facture";
    }
}
