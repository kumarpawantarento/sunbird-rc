package dev.sunbirdrc.registry.dao;

import com.fasterxml.jackson.databind.JsonNode;
import dev.sunbirdrc.registry.util.ReadConfigurator;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public interface IRegistryDao {

	String addEntity(Graph graph, JsonNode rootNode);
	JsonNode getEntity(Graph graph, String entityType, String uuid, ReadConfigurator readConfigurator) throws Exception;
	JsonNode getEntity(Graph graph, Vertex vertex, ReadConfigurator readConfigurator, boolean expandInternal) throws Exception;
	void updateVertex(Graph graph, Vertex rootVertex, JsonNode inputJsonNode, String parentName) throws Exception;
    void deleteEntity(Vertex uuid);

}
