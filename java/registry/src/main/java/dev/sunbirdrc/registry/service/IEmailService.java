package dev.sunbirdrc.registry.service;

import com.fasterxml.jackson.databind.JsonNode;
import dev.sunbirdrc.pojos.HealthIndicator;
import dev.sunbirdrc.pojos.Request;
import dev.sunbirdrc.registry.sink.shard.Shard;
import dev.sunbirdrc.registry.util.ReadConfigurator;

public interface IEmailService extends HealthIndicator {

    Object sendEmail(Request actorMessage);

}
