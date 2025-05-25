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
        htmlBuilder.append("        .header-left { display: flex; }\n");
        htmlBuilder.append("        .logo { width: 80px; height: 80px; margin-right: 15px; }\n");
        htmlBuilder.append("        .clinic-name { font-size: 24px; font-weight: bold; color: #2c3e50; }\n");
        htmlBuilder.append("        .clinic-info { font-size: 12px; color: #7f8c8d; margin-top: 5px; }\n");
        htmlBuilder.append("        .invoice-details { text-align: right; }\n");
        htmlBuilder.append("        .title { text-align: center; color: #3498db; margin: 20px 0; }\n");
        htmlBuilder.append("        .info-section { margin-bottom: 20px; }\n");
        htmlBuilder.append("        .info-title { font-weight: bold; margin-bottom: 5px; }\n");
        htmlBuilder.append("        table { width: 100%; border-collapse: collapse; margin: 20px 0; }\n");
        htmlBuilder.append("        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }\n");
        htmlBuilder.append("        th { background-color: #f2f2f2; }\n");
        htmlBuilder.append("        .total-section { margin-top: 30px; text-align: right; }\n");
        htmlBuilder.append("        .total { font-weight: bold; font-size: 18px; }\n");
        htmlBuilder.append("        .footer { margin-top: 50px; text-align: center; font-size: 12px; color: #7f8c8d; border-top: 1px solid #eee; padding-top: 20px; }\n");
        htmlBuilder.append("        .footer-logo { width: 40px; height: 40px; margin-bottom: 10px; }\n");
        htmlBuilder.append("    </style>\n");
        htmlBuilder.append("</head>\n");
        htmlBuilder.append("<body>\n");

        // En-tête avec logo
        htmlBuilder.append("    <div class=\"header\">\n");
        htmlBuilder.append("        <div class=\"header-left\">\n");
        // Image encodée en base64 (à remplacer par votre propre logo)
        // Ici, nous utilisons une balise img avec src="data:image/png;base64,..." pour inclure l'image directement dans le HTML
        htmlBuilder.append("            <img class=\"logo\" src=\"data:image/png;base64,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\" alt=\"Logo Cabinet Médical\">\n");
        htmlBuilder.append("            <div>\n");
        htmlBuilder.append("                <div class=\"clinic-name\">Cabinet Médical</div>\n");
        htmlBuilder.append("                <div class=\"clinic-info\">123 Rue de la Santé, 75000 Paris<br>Tel: 01 23 45 67 89<br>Email: contact@cabinet-medical.fr</div>\n");
        htmlBuilder.append("            </div>\n");
        htmlBuilder.append("        </div>\n");
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

        // Pied de page avec logo
        htmlBuilder.append("    <div class=\"footer\">\n");
        htmlBuilder.append("        <img class=\"footer-logo\" src=\"data:image/png;base64,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\" alt=\"Logo\">\n");
        htmlBuilder.append("        <p>Merci de votre confiance. Pour toute question concernant cette facture, veuillez nous contacter.</p>\n");
        htmlBuilder.append("        <p>Cabinet Médical - 123 Rue de la Santé - 75000 Paris - Tel: 01 23 45 67 89</p>\n");
        htmlBuilder.append("    </div>\n");

        htmlBuilder.append("</body>\n");
        htmlBuilder.append("</html>");

        return htmlBuilder.toString();
    }
}