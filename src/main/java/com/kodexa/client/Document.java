package com.kodexa.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * A Kodexa document is a representation of a set of content which combines content, metadata
 * and features it is the core model for handling content
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {

    private final static ObjectMapper OBJECT_MAPPER;
    private final static ObjectMapper OBJECT_MAPPER_MSGPACK;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        OBJECT_MAPPER_MSGPACK = new ObjectMapper(new MessagePackFactory());
        OBJECT_MAPPER_MSGPACK.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private Map<String, Object> metadata = new HashMap<>();

    @JsonProperty("content_node")
    private ContentNode contentNode;
    private boolean virtual = false;
    private List<String> mixins = new ArrayList<>();
    private String uuid = UUID.randomUUID().toString();
    private String version;

    /**
     * Convert the document to JSON
     *
     * @param pretty include spacing and new lines if true
     * @return JSON representation of document
     */
    public String toJson(boolean pretty) {
        try {
            if (pretty) {
                return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(this);
            } else {
                return OBJECT_MAPPER.writeValueAsString(this);
            }
        } catch (JsonProcessingException e) {
            throw new KodexaException("Unable to convert Document to JSON", e);
        }
    }

    /**
     * Create a new instance of a Document from JSON string
     *
     * @param json String representation of the document JSON
     * @return De-serialized Document
     */
    public static Document fromJson(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, Document.class);
        } catch (JsonProcessingException e) {
            throw new KodexaException("Unable to convert to Document from JSON", e);
        }
    }

    /**
     * Create a Document from a message packed (kdx) representation
     *
     * @param bytes the bytes for the message packed document
     * @return An instance of a Document
     */
    public static Document fromMsgPack(byte[] bytes) {
        try {
            return OBJECT_MAPPER_MSGPACK.readValue(new ByteArrayInputStream(bytes), Document.class);
        } catch (IOException e) {
            throw new KodexaException("Unable to convert to Document from message pack", e);
        }
    }

    /**
     * Create a Document from a message packed (kdx) representation
     *
     * @param is Input stream containing the document
     * @return An instance of a Document
     */
    public static Document fromMsgPack(InputStream is) {
        try {
            return OBJECT_MAPPER_MSGPACK.readValue(is, Document.class);
        } catch (IOException e) {
            throw new KodexaException("Unable to convert to Document from message pack", e);
        }
    }

    /**
     * Create a Document from a message packed (kdx) representation
     *
     * @param file file containing the document
     * @return An instance of a Document
     */
    public static Document fromMsgPack(File file) {
        try {
            return OBJECT_MAPPER_MSGPACK.readValue(new ByteArrayInputStream(FileUtils.readFileToByteArray(file)), Document.class);
        } catch (IOException e) {
            throw new KodexaException("Unable to convert to Document from message pack", e);
        }
    }

    /**
     * Create a message pack representation of this document
     *
     * @return Byte arry of the document packed
     */
    public byte[] toMsgPack() {
        try {
            return OBJECT_MAPPER_MSGPACK.writeValueAsBytes(this);
        } catch (JsonProcessingException e) {
            throw new KodexaException("Unable to write this document to message pack", e);
        }
    }

    /**
     * Create a JSON representation of this document
     *
     * @return String containing the JSON representation
     */
    public String toJson() {
        return toJson(false);
    }

}
