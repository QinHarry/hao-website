package com.hao.website.blog.utils;

import com.hao.website.blog.constant.WebConstant;
import com.hao.website.blog.entity.Content;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public final class ViewUtil {

    public static String THEME = "themes/distribution";

    /**
     * The website configuration
     * @param key
     * @param defalutValue
     * @return Final configuration
     */
    public static String siteOption(String key, String defalutValue) {
        if (StringUtils.isBlank(key)) {
            return "";
        }
        String str = WebConstant.initConfig.get(key);
        if (StringUtils.isNotBlank(str)) {
            return str;
        } else {
            return defalutValue;
        }
    }

    /**
     * The website configuration
     * @param key
     * @return Final configuration
     */
    public static String siteOption(String key) {
        return siteOption(key, "");
    }

    /**
     * Return the full address
     * @param sub
     * @return
     */
    public static String siteUrl(String sub) {
        return siteOption("siteUrl") + sub;
    }

    /**
     * This link
     * @return
     */
    public static String siteUrl() {
        return siteUrl("");
    }

    /**
     * @return
     */
    public static String siteTitle() {
        return siteOption("siteTitle");
    }

    /**
     * @param contentId
     * @return
     */
    public static String permalink(Integer contentId) {
        return siteUrl("/article/" + contentId.toString());
    }

    /**
     * @param content
     * @return
     */
    public static String permalink(Content content) {
        return permalink(content.getId());
    }

    /**
     * @param unixTime
     * @return
     */
    public static String fmtdate(Timestamp unixTime) {
        return fmtdate(unixTime, "yyyy-MM-dd");
    }

    /**
     * @param unixTime
     * @param pattern
     * @return
     */
    public static String fmtdate(Timestamp unixTime, String pattern) {
        if (null != unixTime && StringUtils.isNotBlank(pattern)) {
            return DateKit.formatDateByUnixTime(unixTime, pattern);
        }
        return "";
    }

    public static String gravatar(String email) {
        return "";
    }

    /**
     * @param content
     * @return
     */
    public static String showThumb(Content content) {
        int contentId = content.getId();
        int size = contentId % 20;
        size = size == 0 ? 1 : size;
        return "/user/img/rand/" + size + ".jpg";
    }

    private static final String[] ICONS = {"bg-ico-book", "bg-ico-game", "bg-ico-note", "bg-ico-chat", "bg-ico-code", "bg-ico-image", "bg-ico-web", "bg-ico-link", "bg-ico-design", "bg-ico-lock"};

    /**
     * @param cid
     * @return
     */
    public static String showIcon(int cid) {
        return ICONS[cid % ICONS.length];
    }

    /**
     * @param categories
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String showCategories(String categories) throws UnsupportedEncodingException {
        if (StringUtils.isNotBlank(categories)) {
            String[] arr = categories.split(",");
            StringBuffer sbuf = new StringBuffer();
            for (String c : arr) {
                sbuf.append("<a href=\"/category/" + URLEncoder.encode(c, "UTF-8") + "\">" + c + "</a>");
            }
            return sbuf.toString();
        }
        return showCategories("Default Category");
    }

    /**
     * @param tags
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String showTags(String tags) throws UnsupportedEncodingException {
        if (StringUtils.isNotBlank(tags)) {
            String[] arr = tags.split(",");
            StringBuffer sbuf = new StringBuffer();
            for (String c : arr) {
                sbuf.append("<a href=\"/tag/" + URLEncoder.encode(c, "UTF-8") + "\">" + c + "</a>");
            }
            return sbuf.toString();
        }
        return "";
    }

    public static String article(String value) {
        if (StringUtils.isNotBlank(value)) {
            value = value.replace("<!--more-->", "\r\n");
            return mdToHtml(value);
        }
        return "";
    }

    /**
     * Markdown converted to html
     * @param markdown
     * @return
     */
    public static String mdToHtml(String markdown) {
        if (StringUtils.isBlank(markdown)) {
            return "";
        }
        List<Extension> extensions = Arrays.asList(TablesExtension.create());
        Parser parser = Parser.builder().extensions(extensions).build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
        String content = renderer.render(document);
        content = emoji(content);
        return content;
    }

    /** An :grinning:awesome :smiley:string &#128516;with a few :wink:emojis!
     * <p>
     *     String converted to emoji
     * </p>
     * @param value
     * @return
     */
    public static String emoji(String value) {
        return EmojiParser.parseToUnicode(value);
    }

    /**
     * @return
     */
    public static Map<String, String> social() {
        final String prefix = "social_";
        Map<String, String> map = new HashMap<>();
        map.put("weibo", WebConstant.initConfig.get(prefix + "weibo"));
        map.put("zhihu", WebConstant.initConfig.get(prefix + "zhihu"));
        map.put("github", WebConstant.initConfig.get(prefix + "github"));
        map.put("twitter", WebConstant.initConfig.get(prefix + "twitter"));
        return map;
    }

    public static String random(int max, String str) {
        return UUID.random(1, max) + str;
    }

    public static boolean isEmpty(Page page) {
        return page == null || page.getContent() == null || page.getContent().size() == 0;
    }

    public static String substr(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len);
        }
        return str;
    }
}
