package net.sf.robocode.serialization;

import java.io.IOException;
import java.io.Writer;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.StringCharacterIterator;
import java.util.Locale;

// https://tools.ietf.org/html/rfc4180
public class CsvWriter {
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.####", new DecimalFormatSymbols(Locale.US));
    private Writer writer;
    private boolean header;
    private boolean isStartLine;

    public CsvWriter(Writer writer, boolean header) {
        this.writer = writer;
        this.header = header;
    }

    public void startDocument(String header) throws IOException {
        writer.write(header);
        writer.write('\n');
        isStartLine=true;
    }

    public void writeValue(String value) throws IOException {
        writeRaw(encode(value));
    }

    public void writeValue(boolean value) throws IOException {
        writeRaw(Boolean.toString(value));
    }

    public void writeValue(long value) throws IOException {
        writeRaw(Long.toString(value));
    }

    public void writeValue(double value, boolean trim) throws IOException {
        if (trim) {
            writeRaw(decimalFormat.format(value));
        } else {
            writeRaw(Double.toString(value));
        }
    }

    public void endLine() throws IOException {
        writer.write("\r\n");
        isStartLine=true;
    }

    private void writeRaw(String value) throws IOException {
        if(isStartLine){
            isStartLine=false;
        }
        else{
            writer.write(',');
        }
        if (value != null) {
            writer.write(value);
        }
    }

    private static String encode(String text) {
        if(text==null){
            return null;
        }
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(text);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            if (character == '\n') {
                result.append(" ");
            } else if (character == '\r') {
                result.append(" ");
            } else if (character == '"') {
                result.append("'");
            } else {
                // the char is not a special one
                // add it to the result as is
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }

}
