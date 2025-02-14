package com.folioreader.ui.base;

import android.content.Context;

import com.folioreader.Config;
import com.folioreader.R;
import com.folioreader.util.FontFinder;

import java.io.File;

/**
 * @author gautam chibde on 14/6/17.
 */

public final class HtmlUtil {

    /**
     * Function modifies input html string by adding extra css,js and font information.
     *
     * @param context     Activity Context
     * @param htmlContent input html raw data
     * @return modified raw html string
     */
    public static String getHtmlContent(Context context, String htmlContent, Config config) {

        String cssPath =
                String.format(context.getString(R.string.css_tag), "file:///android_asset/css/Style.css");

        String jsPath = String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/jsface.min.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/jquery-3.4.1.min.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/rangy-core.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/rangy-highlighter.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/rangy-classapplier.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/rangy-serializer.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/Bridge.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/rangefix.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/readium-cfi.umd.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag_method_call),
                "setMediaOverlayStyleColors('#C0ED72','#C0ED72')") + "\n";

        jsPath = jsPath
                + "<meta name=\"viewport\" content=\"height=device-height, user-scalable=no\" />";

        String fontName = config.getFont();

        System.out.println("Font family: " + fontName);

        // Inject CSS & user font style
        String toInject = "\n" + cssPath + "\n" + jsPath + "\n";

        File userFontFile = FontFinder.getFontFile(fontName);
        if (userFontFile != null) {
            System.out.println("Injected user font into CSS");
            System.out.println("  - path: " + userFontFile.getAbsolutePath());
            System.out.println("  - family: '" + fontName + "'");
            toInject += "<style>\n";
            toInject += "@font-face {\n";
            toInject += "  font-family: '" + fontName + "';\n";
            toInject += "  src: url('file://" + userFontFile.getAbsolutePath() + "');\n";
            toInject += "}\n";
            toInject += ".custom-font {\n";
            toInject += "  font-family: '" + fontName + "', sans-serif;\n";
            toInject += "}\n";
            toInject += "\n</style>";
        }

        toInject += "</head>";

        htmlContent = htmlContent.replace("</head>", toInject);

        String classes = "custom-font";

        if (config.isNightMode()) {
            classes += " nightMode";
        }

        switch (config.getFontSize()) {
            case 0:
                classes += " textSizeOne";
                break;
            case 1:
                classes += " textSizeTwo";
                break;
            case 2:
                classes += " textSizeThree";
                break;
            case 3:
                classes += " textSizeFour";
                break;
            case 4:
                classes += " textSizeFive";
                break;
            default:
                break;
        }

        String styles = "font-family: '" + fontName + "';";
        htmlContent = checkClassAttr(htmlContent, classes);
        return htmlContent;
    }
     private static String checkClassAttr(String html, String classes) {
        //get the entry of <html>
        String s1 = html.substring(html.indexOf("<html"));
        String s2= s1.substring(0, s1.indexOf(">"));
        String classValue = "";

        //check if class attribute exists
        if(s2.contains("class=")) {
            String classVal = s2.substring(s2.indexOf("class="));
            String[] kvPairs = classVal.split(" ");

            //get the value
            for(String kvPair: kvPairs) {
                String[] kv = kvPair.split("=");
                String key = kv[0];

                if(key.equals("class")) {
                    classValue = kv[1]; //get the value of class attribute
                    break;
                }
            }

            //remove the class attribute since it will be readded later to avoid "attribute redefined error"
            html = html.replaceFirst("class=" + classValue, "");
            classValue = classValue.substring(1); //remove the first quotation


        }
        classValue =  classValue.equals("") ? "\"" : classValue;
        html = html.replace("<html", "<html class=\"" + classes + " " + classValue + " onclick=\"onClickHtml()\"");
        return html;
    }
}
