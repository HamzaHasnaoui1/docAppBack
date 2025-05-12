package ma.formation.service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import ma.formation.entities.Facture;
import ma.formation.entities.Patient;
import ma.formation.entities.RendezVous;
import ma.formation.exceptions.ResourceNotFoundException;
import ma.formation.repositories.FactureRepository;
import ma.formation.repositories.PatientRepository;
import ma.formation.repositories.RendezVousRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FactureService {
    private final PatientRepository patientRepository;
    private final RendezVousRepository consultationRepository;
    private final FactureRepository factureRepository;

    @Transactional
    public byte[] generateFacturePdf(Long consultationId, boolean withTVA) {
        RendezVous consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation", "id", consultationId));

        Patient patient = consultation.getPatient();
        double prixBase = consultation.getPrix();
        double montantTVA = withTVA ? prixBase * 0.20 : 0; // Exemple de TVA à 20%
        double totalTTC = prixBase + montantTVA;

        // Créer la facture en base de données
        Facture facture = new Facture();
        facture.setNumeroFacture(generateInvoiceNumber());
        facture.setDateFacture(new Date());
        facture.setPrixFacture(totalTTC);
        Facture savedFacture = factureRepository.save(facture);

        // Générer le PDF
        String htmlContent = generateHtmlFacture(patient, consultation, savedFacture, withTVA, prixBase, montantTVA, totalTTC);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            DefaultFontProvider fontProvider = new DefaultFontProvider();
            ConverterProperties properties = new ConverterProperties();
            properties.setFontProvider(fontProvider);

            HtmlConverter.convertToPdf(htmlContent, writer, properties);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Facture> getAllFactures() {
        return factureRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Facture getFactureById(Long id) {
        return factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture", "id", id));
    }

    private String generateInvoiceNumber() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String datePart = dateFormat.format(new Date());
        String uniquePart = UUID.randomUUID().toString().substring(0, 8);
        return "FACT-" + datePart + "-" + uniquePart;
    }

    private String generateHtmlFacture(Patient patient, RendezVous consultation, Facture facture,
                                       boolean withTVA, double prixBase, double montantTVA, double totalTTC) {

        RendezVous rdv = consultation;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);

        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>\n");
        htmlBuilder.append("<html>\n");
        htmlBuilder.append("<head>\n");
        htmlBuilder.append("    <meta charset=\"UTF-8\">\n");
        htmlBuilder.append("    <title>Facture Médicale</title>\n");
        htmlBuilder.append("    <style>\n");
        htmlBuilder.append("        body { font-family: Arial, sans-serif; margin: 20px; }\n");
        htmlBuilder.append("        .header { display: flex; justify-content: space-between; margin-bottom: 30px; }\n");
        htmlBuilder.append("        .logo { font-size: 24px; font-weight: bold; color: #2c3e50; }\n");
        htmlBuilder.append("        .invoice-details { text-align: right; }\n");
        htmlBuilder.append("        .title { text-align: center; color: #3498db; margin: 20px 0; }\n");
        htmlBuilder.append("        .info-section { margin-bottom: 20px; }\n");
        htmlBuilder.append("        .info-title { font-weight: bold; margin-bottom: 5px; }\n");
        htmlBuilder.append("        table { width: 100%; border-collapse: collapse; margin: 20px 0; }\n");
        htmlBuilder.append("        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }\n");
        htmlBuilder.append("        th { background-color: #f2f2f2; }\n");
        htmlBuilder.append("        .total-section { margin-top: 30px; text-align: right; }\n");
        htmlBuilder.append("        .total { font-weight: bold; font-size: 18px; }\n");
        htmlBuilder.append("        .footer { margin-top: 50px; text-align: center; font-size: 12px; color: #7f8c8d; }\n");
        htmlBuilder.append("    </style>\n");
        htmlBuilder.append("</head>\n");
        htmlBuilder.append("<body>\n");

        // En-tête
        htmlBuilder.append("    <div class=\"header\">\n");
        htmlBuilder.append("        <div class=\"logo\">Cabinet Médical</div>\n");
        htmlBuilder.append("        <div class=\"invoice-details\">\n");
        htmlBuilder.append("            <div>Facture N° " + facture.getNumeroFacture() + "</div>\n");
        htmlBuilder.append("            <div>Date: " + dateFormat.format(facture.getDateFacture()) + "</div>\n");
        htmlBuilder.append("        </div>\n");
        htmlBuilder.append("    </div>\n");

        // Titre
        htmlBuilder.append("    <h1 class=\"title\">Facture de Consultation</h1>\n");

        // Informations patient
        htmlBuilder.append("    <div class=\"info-section\">\n");
        htmlBuilder.append("        <div class=\"info-title\">Informations Patient:</div>\n");
        htmlBuilder.append("        <div>Nom: " + patient.getNom() + "</div>\n");
        htmlBuilder.append("        <div>Adresse: " + patient.getAdresse() + "</div>\n");
        htmlBuilder.append("        <div>Code Postal: " + patient.getCodePostal() + "</div>\n");
        htmlBuilder.append("        <div>Téléphone: " + patient.getNumeroTelephone() + "</div>\n");
        htmlBuilder.append("    </div>\n");

        // Informations consultation
        htmlBuilder.append("    <div class=\"info-section\">\n");
        htmlBuilder.append("        <div class=\"info-title\">Informations Consultation:</div>\n");
        htmlBuilder.append("        <div>Date de Consultation: " + dateFormat.format(consultation.getDate()) + "</div>\n");
        htmlBuilder.append("        <div>Médecin: Dr. " + consultation.getMedecin().getNom() + "</div>\n");
        htmlBuilder.append("        <div>Spécialité: " + consultation.getMedecin().getSpecialite() + "</div>\n");
        htmlBuilder.append("    </div>\n");

        // Tableau détaillé
        htmlBuilder.append("    <table>\n");
        htmlBuilder.append("        <tr>\n");
        htmlBuilder.append("            <th>Description</th>\n");
        htmlBuilder.append("            <th>Montant</th>\n");
        htmlBuilder.append("        </tr>\n");
        htmlBuilder.append("        <tr>\n");
        htmlBuilder.append("            <td>Consultation médicale</td>\n");
        htmlBuilder.append("            <td>" + currencyFormat.format(prixBase) + "</td>\n");
        htmlBuilder.append("        </tr>\n");

        if (withTVA) {
            htmlBuilder.append("        <tr>\n");
            htmlBuilder.append("            <td>TVA (20%)</td>\n");
            htmlBuilder.append("            <td>" + currencyFormat.format(montantTVA) + "</td>\n");
            htmlBuilder.append("        </tr>\n");
        }

        htmlBuilder.append("    </table>\n");

        // Total
        htmlBuilder.append("    <div class=\"total-section\">\n");
        htmlBuilder.append("        <div class=\"total\">Total " + (withTVA ? "TTC" : "") + ": " + currencyFormat.format(totalTTC) + "</div>\n");
        htmlBuilder.append("    </div>\n");

        // Pied de page
        htmlBuilder.append("    <div class=\"footer\">\n");
        htmlBuilder.append("        <p>Merci de votre confiance. Pour toute question concernant cette facture, veuillez nous contacter.</p>\n");
        htmlBuilder.append("        <p>Cabinet Médical - 123 Rue de la Santé - 75000 Paris - Tel: 01 23 45 67 89</p>\n");
        htmlBuilder.append("    </div>\n");

        htmlBuilder.append("</body>\n");
        htmlBuilder.append("</html>");

        return htmlBuilder.toString();
    }
}