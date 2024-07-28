package project_biu.graph;
import java.util.Date;

/**
 * The Message class represents a message that can be sent between agents.
 * It supports different types of data including text, byte array, and double.
 */
public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    /**
     * Constructs a Message with a string.
     *
     * @param asText the message content as a string
     */
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
    /**
     * Constructs a Message with a byte array.
     *
     * @param data the message content as a byte array
     */
    public Message(byte[] data) {
        this(new String(data));
    }

    /**
     * Constructs a Message with a double.
     *
     * @param data the message content as a double
     */
    public Message(double data) {
        this(Double.toString(data));
    }
}

