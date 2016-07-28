package com.cs.messaging.sftp;

import com.cs.util.DateFormatPatterns;

import org.springframework.integration.file.FileHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Joakim Gottzén
 */
public abstract class NetreferMessage implements Message<String>, Serializable {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("SpellCheckingInspection")
    public static final String FILE_ENDING = ".csv";
    static final String HEADER_DELIMITER = ",";
    static final String ROW_DELIMITER = HEADER_DELIMITER;

    private final Map<String, Object> headers = new HashMap<>();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatPatterns.DATE_ONLY);

    protected NetreferMessage(final String namePrefix, final Date activityDate) {
        headers.put(FileHeaders.FILENAME, namePrefix + dateFormat.format(activityDate) + FILE_ENDING);
    }

    protected abstract boolean isEmpty();

    void addHeader(final String name, final Object value) {
        headers.put(name, value);
    }

    @Override
    public MessageHeaders getHeaders() {
        return new MessageHeaders(headers);
    }

    SimpleDateFormat getDateFormat() {
        return dateFormat;
    }
}
