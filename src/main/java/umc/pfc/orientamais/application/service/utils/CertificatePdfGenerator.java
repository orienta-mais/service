package umc.pfc.orientamais.application.service.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import umc.pfc.orientamais.domain.model.clazz.Lesson;
import umc.pfc.orientamais.domain.model.mentored.Mentored;

@Component
public class CertificatePdfGenerator {

  private static final String CERTIFICATE_VALIDATION_URL =
      "https://orienta.org/certificados/validar/";
  private static final float PAGE_WIDTH = PageSize.A4.rotate().getWidth();
  private static final float PAGE_HEIGHT = PageSize.A4.rotate().getHeight();

  public byte[] generateCertificate(Mentored mentored, Lesson lesson) {
    try {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
      PdfWriter writer = PdfWriter.getInstance(document, output);
      document.open();

      PdfContentByte canvas = writer.getDirectContentUnder();
      addBorder(canvas);
      addInnerShadow(canvas);

      Image badge = loadLogo();
      badge.scaleToFit(110, 110);
      float badgeX = (PAGE_WIDTH / 2) - (badge.getScaledWidth() / 2);
      float badgeY = PAGE_HEIGHT - 100;
      badge.setAbsolutePosition(badgeX, badgeY);
      document.add(badge);

      Font titleFont =
          new Font(Font.FontFamily.HELVETICA, 32, Font.BOLD, new BaseColor(25, 25, 25));
      Paragraph title = new Paragraph("Certificado de Participação", titleFont);
      title.setAlignment(Element.ALIGN_CENTER);
      title.setSpacingBefore(80);
      document.add(title);

      Font subtitleFont =
          new Font(Font.FontFamily.HELVETICA, 15, Font.NORMAL, new BaseColor(100, 100, 100));
      Paragraph subtitle = new Paragraph("Concedido a", subtitleFont);
      subtitle.setAlignment(Element.ALIGN_CENTER);
      subtitle.setSpacingBefore(30);
      document.add(subtitle);

      Font nameFont = new Font(Font.FontFamily.HELVETICA, 30, Font.BOLD, new BaseColor(0, 0, 0));
      Paragraph name = new Paragraph(mentored.getName() + " " + mentored.getLastName(), nameFont);
      name.setAlignment(Element.ALIGN_CENTER);
      // name.setSpacingBefore(5);
      document.add(name);

      String date =
          lesson
              .getStartTime()
              .toLocalDate()
              .format(DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("pt", "BR")));
      long duration = Duration.between(lesson.getStartTime(), lesson.getEndTime()).toHours();

      Font bodyFont =
          new Font(Font.FontFamily.HELVETICA, 15, Font.NORMAL, new BaseColor(70, 70, 70));
      Paragraph body =
          new Paragraph(
              "Participou da aula \""
                  + lesson.getTitle()
                  + "\", ministrada por "
                  + lesson.getMentor().getName()
                  + ", realizada em "
                  + date
                  + ", com duração de "
                  + duration
                  + " hora(s).",
              bodyFont);
      body.setAlignment(Element.ALIGN_CENTER);
      body.setSpacingBefore(20);
      body.setSpacingAfter(70);
      body.setLeading(22f);
      document.add(body);

      addFooter(document, lesson);
      addQrCode(document, mentored, lesson);

      document.close();
      return applyDigitalSignature(output.toByteArray());
    } catch (Exception e) {
      throw new RuntimeException("Erro ao gerar PDF do certificado", e);
    }
  }

  private void addBorder(PdfContentByte canvas) {
    Rectangle outer =
        new Rectangle(
            18, 18, PageSize.A4.rotate().getWidth() - 18, PageSize.A4.rotate().getHeight() - 18);
    outer.setBorder(Rectangle.BOX);
    outer.setBorderWidth(2);
    outer.setBorderColor(new BaseColor(200, 200, 200));
    canvas.rectangle(outer);

    Rectangle inner =
        new Rectangle(
            28, 28, PageSize.A4.rotate().getWidth() - 28, PageSize.A4.rotate().getHeight() - 28);
    inner.setBorder(Rectangle.BOX);
    inner.setBorderWidth(1);
    inner.setBorderColor(new BaseColor(230, 230, 230));
    canvas.rectangle(inner);
  }

  private void addInnerShadow(PdfContentByte canvas) {
    PdfShading shading =
        PdfShading.simpleAxial(
            canvas.getPdfWriter(),
            0,
            0,
            0,
            100,
            new BaseColor(255, 255, 255),
            new BaseColor(245, 245, 245));
    PdfShadingPattern pattern = new PdfShadingPattern(shading);
    canvas.setShadingFill(pattern);
    canvas.rectangle(0, 0, PageSize.A4.rotate().getWidth(), PageSize.A4.rotate().getHeight());
    canvas.fill();
  }

  private void addFooter(Document document, Lesson lesson) throws DocumentException {
    PdfPTable footer = new PdfPTable(2);
    footer.setWidthPercentage(80);
    footer.setWidths(new int[] {1, 1});
    footer.setSpacingBefore(40);
    footer.setHorizontalAlignment(Element.ALIGN_CENTER);

    Font footerFont =
        new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, new BaseColor(90, 90, 90));

    PdfPCell mentorCell =
        new PdfPCell(
            new Phrase(
                "__________________________\n\n" + lesson.getMentor().getName() + "\nMentor(a)",
                footerFont));
    mentorCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    mentorCell.setBorder(Rectangle.NO_BORDER);

    PdfPCell platformCell =
        new PdfPCell(
            new Phrase(
                "__________________________\n\nOrienta+\nPlataforma de Mentorias", footerFont));
    platformCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    platformCell.setBorder(Rectangle.NO_BORDER);

    footer.addCell(mentorCell);
    footer.addCell(platformCell);
    document.add(footer);
  }

  private void addQrCode(Document document, Mentored mentored, Lesson lesson)
      throws IOException, WriterException, DocumentException {
    String validationUrl = CERTIFICATE_VALIDATION_URL + mentored.getId() + "-" + lesson.getId();
    Image qrCodeImage = generateQrCodeImage(validationUrl);
    qrCodeImage.scaleToFit(80, 80);
    qrCodeImage.setAbsolutePosition(PageSize.A4.rotate().getWidth() - 130, 60);
    document.add(qrCodeImage);

    Font qrFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, new BaseColor(120, 120, 120));
    Paragraph qrText = new Paragraph("Verifique autenticidade:\n" + validationUrl, qrFont);
    qrText.setAlignment(Element.ALIGN_RIGHT);
    qrText.setSpacingBefore(47);
    document.add(qrText);
  }

  private Image generateQrCodeImage(String text)
      throws WriterException, IOException, BadElementException {
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 150, 150);
    ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutput);
    return Image.getInstance(pngOutput.toByteArray());
  }

  private Image loadLogo() throws IOException, BadElementException {
    String[] possibleFiles = {
      "static/logo.png", "static/logo.png", "static/Logo.webp", "static/logo.jpg"
    };
    for (String path : possibleFiles) {
      try {
        ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) continue;
        return Image.getInstance(resource.getURL());
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
    throw new IOException("Logo não encontrado para o selo do certificado.");
  }

  private byte[] applyDigitalSignature(byte[] pdfBytes) {
    try {
      File p12File = new ClassPathResource("static/certificado_assinatura.p12").getFile();
      if (!p12File.exists()) return pdfBytes;

      char[] password = "senha-secreta".toCharArray();
      KeyStore ks = KeyStore.getInstance("PKCS12");
      ks.load(new FileInputStream(p12File), password);

      String alias = ks.aliases().nextElement();
      PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password);
      Certificate[] chain = ks.getCertificateChain(alias);

      ByteArrayOutputStream signedOutput = new ByteArrayOutputStream();
      PdfReader reader = new PdfReader(pdfBytes);
      PdfStamper stamper = PdfStamper.createSignature(reader, signedOutput, '\0');
      PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
      appearance.setReason("Assinatura digital de certificado Orienta+");
      appearance.setLocation("Brasil");
      appearance.setVisibleSignature(new Rectangle(40, 40, 200, 100), 1, "signature");

      ExternalSignature pks = new PrivateKeySignature(privateKey, "SHA-256", null);
      ExternalDigest digest = new BouncyCastleDigest();
      MakeSignature.signDetached(
          appearance, digest, pks, chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);

      return signedOutput.toByteArray();
    } catch (Exception e) {
      return pdfBytes;
    }
  }
}
