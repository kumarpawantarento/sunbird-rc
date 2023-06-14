package dev.sunbirdrc.registry.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dev.sunbirdrc.elastic.IElasticService;
import dev.sunbirdrc.pojos.AuditRecord;
import dev.sunbirdrc.registry.exception.RecordNotFoundException;
import dev.sunbirdrc.registry.middleware.util.Constants;
import dev.sunbirdrc.registry.middleware.util.JSONUtil;
import dev.sunbirdrc.registry.sink.shard.Shard;
import dev.sunbirdrc.registry.util.ReadConfigurator;

/**
 * This class provide read option with Elastic search
 * database to operate
 *
 */
@Component
public class ElasticReadService implements IReadService {

    private static Logger logger = LoggerFactory.getLogger(ElasticReadService.class);

    @Autowired
    private IElasticService elasticService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IAuditService auditService;

    @Value("${audit.enabled}")
    private boolean auditEnabled;
    /**
     * This method interacts with the Elasticsearch and reads the record
     *
     * @param id           - osid
     * @param entityType   - elastic-search index
     * @param configurator
     * @return
     * @throws Exception
     */
    @Override
    public JsonNode getEntity(Shard shard, String userId, String id, String entityType, ReadConfigurator configurator) throws Exception {
        JsonNode result = null;
        Map<String, Object> response = null;
        try {
            response = elasticService.readEntity(entityType.toLowerCase(), id);
        } catch (IOException e) {
            logger.error("Exception in reading a record to ElasticSearch", e);
        }
        
        if (response == null || Constants.STATUS_INACTIVE.equals(response.get(Constants.STATUS_KEYWORD)) ) {
            throw new RecordNotFoundException("Record with " + id + " not found in Elastic-search");
        }
        
        result = objectMapper.convertValue(response, JsonNode.class);
        if (!configurator.isIncludeSignatures()) {
            JSONUtil.removeNode((ObjectNode) result, Constants.SIGNATURES_STR);
        }     
        
        auditService.auditRead(auditService.createAuditRecord(userId, id, entityType), shard);


        ObjectNode resultNode = JsonNodeFactory.instance.objectNode();
        resultNode.set(entityType, result);
        return resultNode;
    }
    
    

}
