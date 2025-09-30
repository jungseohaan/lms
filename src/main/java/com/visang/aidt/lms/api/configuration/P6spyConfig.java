package com.visang.aidt.lms.api.configuration;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.util.Locale;

@Profile({"visang", "dev"})
@Configuration
public class P6spyConfig implements MessageFormattingStrategy {

    @PostConstruct
    public void setLogMessageFormat() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(this.getClass().getName());
    }

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        return String.format("[%s] | %d ms | %s", category, elapsed, highlight(format(category, sql)));
    }

    private String format(String category, String sql) {

        if(sql.isBlank()) {
            return sql;
        }

        if (Category.STATEMENT.getName().equals(category)) {
            sql = sql.trim().toLowerCase(Locale.ROOT);

            if (isDDL(sql)) {
                sql = FormatStyle.DDL.getFormatter().format(sql);
            } else if(isBASIC(sql)) {
                sql = FormatStyle.BASIC.getFormatter().format(sql);
            }
        }

        return sql;
    }

    private String highlight(String sql) {
        return FormatStyle.HIGHLIGHT.getFormatter().format(sql);
    }

    private boolean isDDL(String sql) {
        return sql.startsWith("create") || sql.startsWith("alter") || sql.startsWith("comment");
    }

    private boolean isBASIC(String sql) {
        return sql.startsWith("select") || sql.startsWith("insert") || sql.startsWith("update") || sql.startsWith("delete");
    }
}
