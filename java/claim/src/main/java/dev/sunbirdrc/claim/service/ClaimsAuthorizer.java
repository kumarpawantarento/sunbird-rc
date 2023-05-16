package dev.sunbirdrc.claim.service;

import com.fasterxml.jackson.databind.JsonNode;
import dev.sunbirdrc.claim.entity.Claim;
import dev.sunbirdrc.registry.middleware.service.ConditionResolverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ClaimsAuthorizer {

    private static final String ATTESTOR = "ATTESTOR";
    private static final String UUID_PROPERTY_NAME = "osid";
    private static final Logger logger = LoggerFactory.getLogger(ClaimsAuthorizer.class);

    private final ConditionResolverService conditionResolverService;

    public ClaimsAuthorizer(ConditionResolverService conditionResolverService) {
        this.conditionResolverService = conditionResolverService;
    }

    public boolean isAuthorizedAttestor(Claim claim, JsonNode attestorNode) {
        if(attestorNode.isNull()) {
            return false;
        }
        try {
            String resolvedCondition = conditionResolverService.resolve(
                    attestorNode,
                    ATTESTOR,
                    claim.getConditions(),
                    Collections.emptyList()
            );
            return conditionResolverService.evaluate(resolvedCondition);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public boolean isAuthorizedRequestor(Claim claim, JsonNode attestorNode) {
        if(!attestorNode.isNull() && attestorNode.has(UUID_PROPERTY_NAME)) {
            String userEntityId = attestorNode.get(UUID_PROPERTY_NAME).asText();
            return claim.getEntityId().equals(userEntityId);
        }
        return false;
    }
}
