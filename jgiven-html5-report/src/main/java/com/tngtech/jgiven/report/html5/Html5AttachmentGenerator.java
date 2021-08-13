package com.tngtech.jgiven.report.html5;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.io.BaseEncoding;
import com.google.common.io.Files;
import com.google.common.net.MediaType;
import com.tngtech.jgiven.exception.JGivenInstallationException;
import com.tngtech.jgiven.report.model.AttachmentModel;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelVisitor;
import com.tngtech.jgiven.report.model.StepModel;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Html5AttachmentGenerator extends ReportModelVisitor {
    private static final Logger log = LoggerFactory.getLogger(Html5AttachmentGenerator.class);
    private static final String ATTACHMENT_DIRNAME = "attachments";
    public static final int MINIMAL_THUMBNAIL_SIZE = 20;

    private File attachmentsDir;
    private String subDir;
    private final Multiset<String> fileCounter = HashMultiset.create();
    private final Set<String> usedFileNames = Sets.newHashSet();
    private String htmlSubDir;

    public Html5AttachmentGenerator() {
    }

    @VisibleForTesting
    public Html5AttachmentGenerator(File attachmentsDir) {
        this.attachmentsDir = attachmentsDir;
    }

    public void generateAttachments(File targetDir, ReportModel model) {
        subDir = ATTACHMENT_DIRNAME + File.separatorChar + model.getClassName().replace('.', File.separatorChar);
        htmlSubDir = subDir.replace(File.separatorChar, '/');
        attachmentsDir = new File(targetDir, subDir);

        if (!attachmentsDir.exists() && !attachmentsDir.mkdirs()) {
            throw new JGivenInstallationException("Could not create directory " + attachmentsDir);
        }
        model.accept(this);
    }

    @Override
    public void visit(StepModel stepModel) {
        List<AttachmentModel> attachments = stepModel.getAttachments();
        for (AttachmentModel attachment : attachments) {
            writeAttachment(attachment);
        }
    }

    private void writeAttachment(AttachmentModel attachment) {
        String mimeType = attachment.getMediaType();
        MediaType mediaType = MediaType.parse(mimeType);
        File targetFile = writeFile(attachment, mediaType);
        attachment.setValue(htmlSubDir + "/" + targetFile.getName());
        log.debug("Attachment written to " + targetFile);
    }

    private String getExtension(MediaType mediaType) {
        if (mediaType.is(MediaType.SVG_UTF_8)) {
            return "svg";
        }

        if (mediaType.is(MediaType.ICO)) {
            return "ico";
        }

        if (mediaType.is(MediaType.BMP)) {
            return "bmp";
        }

        return mediaType.subtype();
    }

    File getTargetFile(String fileName, String extension) {
        if (fileName == null) {
            fileName = "attachment";
        }

        int count = fileCounter.count(fileName);
        fileCounter.add(fileName);

        String suffix = "";
        if (count > 0) {
            count += 1;
            suffix = String.valueOf(count);
        }

        String fileNameWithExtension = fileName + suffix + "." + extension;

        while (usedFileNames.contains(fileNameWithExtension)) {
            fileCounter.add(fileName);
            count++;
            suffix = String.valueOf(count);
            fileNameWithExtension = fileName + suffix + "." + extension;
        }
        usedFileNames.add(fileNameWithExtension);
        return new File(attachmentsDir, fileNameWithExtension);
    }

    private File getThumbnailFileFor(File originalImage) {
        String orgName = originalImage.getName();
        String newName = "";

        int dotIndex = orgName.lastIndexOf(".");

        if (dotIndex == -1) {
            newName = orgName + "-thumb";
        } else {
            String extension = orgName.subSequence(dotIndex + 1, orgName.length()).toString();
            newName = orgName.substring(0, dotIndex) + "-thumb." + extension;
        }
        return new File(attachmentsDir, newName);

    }

    private File writeFile(AttachmentModel attachment, MediaType mediaType) {
        String extension = getExtension(mediaType);
        File targetFile = getTargetFile(attachment.getFileName(), extension);
        try {
            if (attachment.isBinary()) {
                if (mediaType.is(MediaType.ANY_IMAGE_TYPE)) {
                    File thumbFile = getThumbnailFileFor(targetFile);
                    byte[] thumbnail = compressToThumbnail(attachment.getValue(), extension);
                    Files.write(thumbnail, thumbFile);
                }
                Files.write(BaseEncoding.base64().decode(attachment.getValue()), targetFile);
            } else {
                Files.write(attachment.getValue().getBytes(Charsets.UTF_8), targetFile);
                if (com.tngtech.jgiven.attachment.MediaType.SVG_UTF_8.toString().equals(attachment.getMediaType())) {
                    File thumbFile = getThumbnailFileFor(targetFile);
                    writeThumbnailForSVG(targetFile, thumbFile);
                }
            }
        } catch (IOException e) {
            log.error("Error while trying to write attachment to file " + targetFile, e);
        }
        return targetFile;
    }

    private byte[] compressToThumbnail(String base64content, String extension) {
        byte[] imageBytes = BaseEncoding.base64().decode(base64content);
        byte[] base64thumb = {};
        try {
            BufferedImage before = ImageIO.read(new ByteArrayInputStream(imageBytes));
            BufferedImage after = scaleDown(before);
            base64thumb = bufferedImageToBase64(after, extension);
        } catch (IOException e) {
            log.error("Error while decoding the attachment to BufferedImage ", e);
        }
        return base64thumb;
    }

    static BufferedImage scaleDown(BufferedImage before) {
        double xFactor = Math.min(1.0, MINIMAL_THUMBNAIL_SIZE / (double) before.getWidth());
        double yFactor = Math.min(1.0, MINIMAL_THUMBNAIL_SIZE / (double) before.getHeight());

        double factor = Math.max(xFactor, yFactor);

        int width = (int) Math.round(before.getWidth() * factor);
        int height = (int) Math.round(before.getHeight() * factor);
        BufferedImage after = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        AffineTransform at = new AffineTransform();
        at.scale(factor, factor);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

        return scaleOp.filter(before, after);
    }

    private byte[] bufferedImageToBase64(BufferedImage bi, String extension) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageArray = {};
        try {
            ImageIO.write(bi, extension, baos);
            imageArray = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            log.error("Error while decoding the compressed BufferedImage to base64 ", e);
        }
        return imageArray;
    }

    private void writeThumbnailForSVG(File initialSVG, File thumbnailSVG) {
        String base64PNGImage = getPNGFromSVG(initialSVG);
        byte[] scaledDownInBytes = compressToThumbnail(base64PNGImage, "png");
        Dimension imageDimension = getImageDimension(scaledDownInBytes);
        String base64ScaledDownContent = BaseEncoding.base64().encode(scaledDownInBytes);
        createSVGThumbFile(thumbnailSVG, base64ScaledDownContent, imageDimension);
    }

    Dimension getImageDimension(byte[] givenImage) {
        Dimension dimension = new Dimension();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(givenImage)) {
            BufferedImage image = ImageIO.read(bais);
            dimension.height = image.getHeight();
            dimension.width = image.getWidth();
        } catch (IOException e) {
            log.error("The converted png image cannot be read.");
        }

        return dimension;
    }

    void createSVGThumbFile(File targetFile, String base64Image, Dimension dimension) {
        String xmlFormat = getXMLFormat(base64Image, dimension);
        try (FileWriter fileWriter = new FileWriter(targetFile)) {
            fileWriter.write(xmlFormat);
        } catch (IOException e) {
            log.error("Error writing the thumbnail svg to " + targetFile);
        }
    }

    private String getXMLFormat(String base64Image, Dimension dimension) {
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
                + "height=\"" + dimension.getHeight() + "px\" width=\"" + dimension.getWidth() + "px\">"
                + "<image height=\"100%\" width=\"100%\" xlink:href=\"data:image/png;base64, " + base64Image + "\"/>"
                + "</svg>";
    }

    String getPNGFromSVG(File givenSVG) {
        PNGTranscoder transcoder = new PNGTranscoder();
        TranscoderInput transcoderInput = new TranscoderInput();
        TranscoderOutput transcoderOutput = new TranscoderOutput();

        try (FileInputStream fis = new FileInputStream(givenSVG);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            transcoderInput.setInputStream(fis);
            transcoderOutput.setOutputStream(baos);

            transcoder.transcode(transcoderInput, transcoderOutput);
            return BaseEncoding.base64().encode(baos.toByteArray());
        } catch (FileNotFoundException e) {
            log.error("Error while reading the initial svg file.");
        } catch (IOException e) {
            log.error("Error closing the {} file", givenSVG);
        } catch (TranscoderException e) {
            log.error("Error while transcoding the svg file to png. Is the svg formatted correctly?");
        }

        return null;
    }
}
