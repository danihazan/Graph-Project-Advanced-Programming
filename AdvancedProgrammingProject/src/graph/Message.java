package graph;
import java.util.Date;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    public Message(String asText) {
        this.asText = new String(asText);
        this.data = asText.getBytes();
        double temp;
        try {
            temp = Double.parseDouble(asText);
        } catch (NumberFormatException e) {
            temp = Double.NaN;
        }
        this.asDouble = temp;
        this.date = new Date();

    }
    public Message(byte[] data) {
        this(new String(data));
    }
    public Message(double data) {
        this(Double.toString(data));
    }
}

