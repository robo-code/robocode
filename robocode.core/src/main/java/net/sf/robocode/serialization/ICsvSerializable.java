package net.sf.robocode.serialization;

import java.io.IOException;

public interface ICsvSerializable {
    void writeCsv(CsvWriter writer, SerializableOptions options) throws IOException;
}
