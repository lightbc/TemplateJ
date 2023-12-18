package com.lightbc.templatej.utils;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;
import com.lightbc.templatej.interfaces.ConfigInterface;
import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;

/**
 * Description:转换成pdf工具类
 * Package:com.lightbc.templatej.utils
 *
 * @author lightbc
 * @version 1.0
 */
public class PdfUtil {

    /**
     * html内容转换成pdf
     *
     * @param savePath    pdf保存位置
     * @param htmlContent html文本内容
     * @return Boolean true-转换成功，false-转换失败
     */
    public boolean htmlToPdf(String savePath, String htmlContent) {
        boolean b = false;
        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(savePath));
            PdfDocument document = new PdfDocument(writer);
            // 设置A4大小
            document.setDefaultPageSize(PageSize.A4);

            // 设置中文字体支持
            ConverterProperties prop = new ConverterProperties();
            FontProvider provider = new FontProvider();
            // 字体目录路径
            String fontDir = PluginUtil.getWindowsFontsDir();
            if (StringUtils.isNotBlank(fontDir)) {
                provider.addDirectory(fontDir);
            }
            prop.setFontProvider(provider);
            prop.setCharset(ConfigInterface.ENCODE_VALUE);

            // 转换成pdf
            HtmlConverter.convertToPdf(htmlContent, document, prop);
            document.close();
            writer.close();
            b = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return b;
        }
    }
}
