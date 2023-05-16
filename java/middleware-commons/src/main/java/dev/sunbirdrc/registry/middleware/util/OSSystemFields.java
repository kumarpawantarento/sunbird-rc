package dev.sunbirdrc.registry.middleware.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.collections4.ListUtils;

import java.util.EnumSet;
import java.util.List;

/**
 * System fields for created, updated time and userId appended to the json node.
 */
public enum OSSystemFields {

    osCreatedAt {
        @Override
        public void createdAt(JsonNode node, String timeStamp) {
            JSONUtil.addField((ObjectNode) node, osCreatedAt.toString(), timeStamp);
        }
    },
    osUpdatedAt {
        @Override
        public void updatedAt(JsonNode node, String timeStamp) {
            JSONUtil.addField((ObjectNode) node, osUpdatedAt.toString(), timeStamp);
        }

    },
    osCreatedBy {
        @Override
        public void createdBy(JsonNode node, String userId) {
            JSONUtil.addField((ObjectNode) node, osCreatedBy.toString(), userId != null ? userId : "");
        }
    },
    osUpdatedBy {
        @Override
        public void updatedBy(JsonNode node, String userId) {
            JSONUtil.addField((ObjectNode) node, osUpdatedBy.toString(), userId != null ? userId : "");
        }
    },
    osOwner {
      @Override
      public void setOsOwner(JsonNode node, List<String> owners) {
          JSONUtil.addField((ObjectNode) node, osOwner.toString(), ListUtils.emptyIfNull(owners));
      }
    },
    _osState, _osClaimId, _osAttestedData, _osSignedData;

    public void createdBy(JsonNode node, String userId){};

    public void updatedBy(JsonNode node, String userId){};

    public void createdAt(JsonNode node, String timeStamp){};

    public void updatedAt(JsonNode node, String timeStamp){};

    public void setOsOwner(JsonNode node, List<String> owner) {};

    public static OSSystemFields getByValue(String value) {
        for (final OSSystemFields element : EnumSet.allOf(OSSystemFields.class)) {
            if (element.toString().equals(value)) {
                return element;
            }
        }
        return null;
    }

}
