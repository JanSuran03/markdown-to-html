import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;

public class Parser {
    public static String parseHTML(String markdown) throws IOException {
        return parseHTML(new StringBuilder(), new StringReader(markdown));
    }

    static void closeTags(StringBuilder sb, Stack<String> closingTags) {
        while (!closingTags.isEmpty()) {
            sb.append("</").append(closingTags.pop()).append(">");
        }
    }

    static String parseHTML(StringBuilder sb, StringReader sr) throws IOException {
        boolean strikeThrough = false;
        boolean bold = false;
        boolean monospace = false;
        boolean italic = false;
        boolean paragraph = false;
        int ch;
        int ch2;
        int ch3;
        Stack<String> openTags = new Stack<>();
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
                    closeTags(sb, openTags);
                    return sb.toString();
                // strikethrough
                case '~':
                    ch2 = sr.read();
                    if (ch2 == '~') {
                        if (strikeThrough) {
                            sb.append("</strike>");
                            openTags.pop();
                        } else {
                            sb.append("<strike>");
                            openTags.push("strike");
                        }
                        strikeThrough = !strikeThrough;
                    } else if (ch2 == -1) {
                        sb.append('~');
                        closeTags(sb, openTags);
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
                            closeTags(sb, openTags);
                            return sb.toString();
                        case '*':
                            if (bold) {
                                sb.append("</strong>");
                                openTags.pop();
                            } else {
                                sb.append("<strong>");
                                openTags.push("strong");
                            }
                            bold = !bold;
                            break;
                        default:
                            sb.append('*').append((char) ch2);
                            break;
                    }
                    break;
                // italic
                case '_':
                    if (italic) {
                        sb.append("</em>");
                        openTags.pop();
                    } else {
                        sb.append("<em>");
                        openTags.push("em");
                    }
                    italic = !italic;
                    break;
                // monospace
                case '`':
                    if (monospace) {
                        sb.append("</code>");
                        openTags.pop();
                    } else {
                        sb.append("<code>");
                        openTags.push("code");
                    }
                    monospace = !monospace;
                    break;
                // horizontal rule
                case '-':
                    ch2 = sr.read();
                    switch (ch2) {
                        case -1:
                            sb.append('-');
                            closeTags(sb, openTags);
                            return sb.toString();
                        case '-':
                            ch3 = sr.read();
                            switch (ch3) {
                                case -1:
                                    sb.append("--");
                                    closeTags(sb, openTags);
                                    return sb.toString();
                                case ('-'):
                                    if (paragraph) {
                                        sb.append("</p>");
                                        openTags.pop();
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
                            closeTags(sb, openTags);
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
                                openTags.pop();
                                paragraph = false;
                            } else {
                                sb.append((char) ch);
                                lastch = ch2;
                            }
                        } else
                            sb.append((char) ch);
                        lastch = ch2;
                    } else {
                        if (!paragraph) {
                            sb.append("<p>").append((char) ch);
                            openTags.push("p");
                        } else
                            sb.append((char) ch);
                        paragraph = true;

                    }
                    break;
            }
        }
    }
}
