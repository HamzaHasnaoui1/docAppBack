package ma.formation.service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class FactureService {

    public byte[] generateFacturePdf(Long patientId) {
        String htmlContent = generateHtmlFacture();

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

    private String generateHtmlFacture() {
        return """
            <html>
                <head>
                    <style>
                        body { font-family: Arial; margin: 20px; }
                        h1 { color: #0066cc; }
                        table { width: 100%; border-collapse: collapse; }
                        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                    </style>
                </head>
                <body>
                    <h1>Facture Médicale</h1>
                    <!-- Contenu dynamique à ajouter -->
                </body>
            </html>
            """;
    }
}