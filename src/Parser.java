import java.io.IOException;
import java.io.StringReader;

public class Parser {
    public static String parseHTML(String markdown) throws IOException {
        return parseHTML(new StringBuilder(), new StringReader(markdown));
    }

    static void closeTags(StringBuilder sb, boolean strikeThrough, boolean bold, boolean monospace,
                          boolean italic, boolean paragraph) {
        if (strikeThrough)
            sb.append("</strike>");
        if (bold)
            sb.append("</strong>");
        if (monospace)
            sb.append("</code>");
        if (italic)
            sb.append("</em>");
        if (paragraph)
            sb.append("</p>");
    }

    static String parseHTML(StringBuilder sb, StringReader sr) throws IOException {
        boolean strikeThrough = false;
        boolean bold = false;
        boolean monospace = false;
        boolean italic = false;
        boolean paragraph = false;
        int ch = -1;
        int ch2;
        int ch3;
        Integer lastch = null;
        for (; ; ) {
            if (lastch != null) {
                ch = lastch;
                lastch = null;
            } else
                ch = sr.read();
            switch (ch) {
                // read ended
                case -1:
                    closeTags(sb, strikeThrough, bold, monospace, italic, paragraph);
                    return sb.toString();
                // strikethrough
                case '~':
                    ch2 = sr.read();
                    if (ch2 == '~') {
                        if (strikeThrough)
                            sb.append("</strike>");
                        else
                            sb.append("<strike>");
                        strikeThrough = !strikeThrough;
                    } else if (ch2 == -1) {
                        sb.append('~');
                        closeTags(sb, strikeThrough, bold, monospace, italic, paragraph);
                        return sb.toString();
                    } else {
                        sb.append('~').append((char) ch2);
                    }
                    break;
                // bold
                case '*':
                    ch2 = sr.read();
                    switch (ch2) {
                        case -1:
                            sb.append('*');
                            closeTags(sb, strikeThrough, bold, monospace, italic, paragraph);
                            return sb.toString();
                        case '*':
                            if (bold)
                                sb.append("</strong>");
                            else
                                sb.append("<strong>");
                            bold = !bold;
                            break;
                        default:
                            sb.append('*').append((char) ch2);
                            break;
                    }
                    break;
                // italic
                case '_':
                    if (italic)
                        sb.append("</em>");
                    else
                        sb.append("<em>");
                    italic = !italic;
                    break;
                // monospace
                case '`':
                    if (monospace)
                        sb.append("</code>");
                    else
                        sb.append("<code>");
                    monospace = !monospace;
                    break;
                // horizontal rule
                case '-':
                    ch2 = sr.read();
                    switch (ch2) {
                        case -1:
                            sb.append('-');
                            closeTags(sb, strikeThrough, bold, monospace, italic, paragraph);
                            return sb.toString();
                        case '-':
                            ch3 = sr.read();
                            switch (ch3) {
                                case -1:
                                    sb.append("--");
                                    closeTags(sb, strikeThrough, bold, monospace, italic, paragraph);
                                    return sb.toString();
                                case ('-'):
                                    if (paragraph) {
                                        sb.append("</p>");
                                        paragraph = false;
                                    }
                                    sb.append("<hr />");
                                    break;
                                default:
                                    sb.append("--").append((char) ch3);
                                    break;
                            }
                            break;
                        default:
                            sb.append('-').append((char) ch2);
                            break;
                    }
                    break;
                // anchor
                case '[':
                    StringBuilder anchorText = new StringBuilder();
                    StringBuilder anchorLink = new StringBuilder();
                    boolean finished_anchor = false;
                    while (!finished_anchor) {
                        int ch_temp = sr.read();
                        if (ch_temp == ']') {
                            sr.read(); // opening brackets
                            for (; ; ) {
                                ch_temp = sr.read();
                                if (ch_temp == ')') {
                                    sb.append("<a href=\"")
                                            .append(anchorLink)
                                            .append("\">")
                                            .append(anchorText)
                                            .append("</a>");
                                    finished_anchor = true;
                                    break;
                                } else {
                                    anchorLink.append((char) ch_temp);
                                }
                            }
                        } else {
                            anchorText.append((char) ch_temp);
                        }
                    }
                    break;
                // line break
                case ' ':
                    ch2 = sr.read();
                    switch (ch2) {
                        case -1:
                            sb.append(' ');
                            closeTags(sb, strikeThrough, bold, monospace, italic, paragraph);
                            return sb.toString();
                        case ' ':
                            sb.append("<br>");
                            break;
                        default:
                            sb.append(' ');
                            lastch = ch2;
                            break;
                    }
                    break;
                // default
                default:
                    if (ch == '\n' || ch == '\r') {
                        ch2 = sr.read();
                        if (ch2 == '\n' || ch2 == '\r') {
                            if (paragraph) {
                                sb.append("</p>");
                                paragraph = false;
                            } else {
                                sb.append((char) ch);
                                lastch = ch2;
                            }
                        } else
                            sb.append((char) ch);
                        lastch = ch2;
                    } else {
                        if (!paragraph)
                            sb.append("<p>").append((char) ch);
                        else
                            sb.append((char) ch);
                        paragraph = true;

                    }
                    break;
            }
        }
    }
}
