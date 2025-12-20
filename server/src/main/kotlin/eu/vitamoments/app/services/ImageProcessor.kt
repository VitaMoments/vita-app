package eu.vitamoments.app.services

import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

data class CropRect(val x: Int, val y: Int, val w: Int, val h: Int)

class ImageProcessor(
    private val jpegQuality: Float = 0.85f,
    private val background: Color = Color.WHITE
) {
    /**
     * Lees image van disk (temp file) en decode. Gooit IllegalArgumentException bij invalid image.
     */
    fun read(file: File): BufferedImage {
        return ImageIO.read(file) ?: throw IllegalArgumentException("Invalid image")
    }

    /**
     * Crop met bounds clamp. Als crop rect ongeldig is => fallback center square.
     */
    fun crop(img: BufferedImage, crop: CropRect?): BufferedImage {
        if (crop == null) return cropCenterSquare(img)
        if (crop.w <= 0 || crop.h <= 0) return cropCenterSquare(img)

        val xx = crop.x.coerceIn(0, img.width - 1)
        val yy = crop.y.coerceIn(0, img.height - 1)
        val ww = crop.w.coerceIn(1, img.width - xx)
        val hh = crop.h.coerceIn(1, img.height - yy)

        return img.getSubimage(xx, yy, ww, hh)
    }

    /**
     * Resize naar exact w/h (voor avatar meestal square).
     */
    fun resize(img: BufferedImage, w: Int, h: Int): BufferedImage {
        val out = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        val g2 = out.createGraphics()
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.drawImage(img, 0, 0, w, h, null)
        g2.dispose()
        return out
    }

    /**
     * JPEG kan geen alpha. PNG transparantie => background fill.
     */
    fun toOpaqueRgb(img: BufferedImage): BufferedImage {
        val rgb = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_RGB)
        val g = rgb.createGraphics()
        g.color = background
        g.fillRect(0, 0, rgb.width, rgb.height)
        g.drawImage(img, 0, 0, null)
        g.dispose()
        return rgb
    }

    /**
     * Schrijf JPEG naar disk.
     */
    fun writeJpeg(img: BufferedImage, outFile: File) {
        val writer = ImageIO.getImageWritersByFormatName("jpg").next()
        outFile.outputStream().use { os ->
            val ios = ImageIO.createImageOutputStream(os)
            writer.output = ios

            val param = writer.defaultWriteParam
            if (param.canWriteCompressed()) {
                param.compressionMode = ImageWriteParam.MODE_EXPLICIT
                param.compressionQuality = jpegQuality.coerceIn(0f, 1f)
            }

            writer.write(null, IIOImage(img, null, null), param)
            ios.close()
            writer.dispose()
        }
    }

    private fun cropCenterSquare(img: BufferedImage): BufferedImage {
        val size = minOf(img.width, img.height)
        val x = (img.width - size) / 2
        val y = (img.height - size) / 2
        return img.getSubimage(x, y, size, size)
    }
}
