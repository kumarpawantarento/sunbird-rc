package dev.sunbirdrc.registry.dao.impl;

import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sunbirdrc.pojos.Filter;
import dev.sunbirdrc.pojos.FilterOperators;
import dev.sunbirdrc.pojos.SearchQuery;
import dev.sunbirdrc.registry.dao.IRegistryDao;
import dev.sunbirdrc.registry.dao.RegistryDaoImpl;
import dev.sunbirdrc.registry.dao.SearchDao;
import dev.sunbirdrc.registry.dao.SearchDaoImpl;
import dev.sunbirdrc.registry.dao.VertexWriter;
import dev.sunbirdrc.registry.exception.AuditFailedException;
import dev.sunbirdrc.registry.exception.EncryptionException;
import dev.sunbirdrc.registry.exception.RecordNotFoundException;
import dev.sunbirdrc.registry.middleware.util.Constants;
import dev.sunbirdrc.registry.model.DBConnectionInfoMgr;
import dev.sunbirdrc.registry.sink.DBProviderFactory;
import dev.sunbirdrc.registry.sink.DatabaseProvider;
import dev.sunbirdrc.registry.util.DefinitionsManager;
import dev.sunbirdrc.registry.util.OSResourceLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PreDestroy;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { DefinitionsManager.class, ObjectMapper.class, DBProviderFactory.class, DBConnectionInfoMgr.class, OSResourceLoader.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles(Constants.TEST_ENVIRONMENT)
public class SearchDaoImplTest {

    private static Graph graph;
    private SearchDao searchDao;
    private DatabaseProvider databaseProvider;
    @Autowired
    private DefinitionsManager definitionsManager;
    @Autowired
    private DBProviderFactory dbProviderFactory;
    @Autowired
    private DBConnectionInfoMgr dbConnectionInfoMgr;
    
    private final static String VALUE_NOT_PRESENT = "valueNotPresent";
    private final static int offset = 0;
    private final static int limit = 1;
    private final static boolean expandInternal = true;
    private List<String> entities = new ArrayList<>();

    
    @Before
    public void initializeGraph() throws IOException {
        dbConnectionInfoMgr.setUuidPropertyName("tid");

        databaseProvider = dbProviderFactory.getInstance(null);
        graph = databaseProvider.getOSGraph().getGraphStore();

        IRegistryDao registryDao = new RegistryDaoImpl(databaseProvider, definitionsManager, "tid");
        searchDao = new SearchDaoImpl(registryDao);
        populateGraph();
        
        entities.add("Teacher");

    }

    @Test
    public void test_search_no_response() throws AuditFailedException, EncryptionException, RecordNotFoundException {
        SearchQuery searchQuery = getSearchQuery(entities, "", "", FilterOperators.eq);//new SearchQuery("", 0, 0);
        JsonNode result = searchDao.search(graph, searchQuery, expandInternal);
        assertTrue(result.get("Teacher").size() == 0);
    }

    @Test
    public void testEqOperator() {
        SearchQuery searchQuery = getSearchQuery(entities, "teacherName", "marko", FilterOperators.eq);
        JsonNode result = searchDao.search(graph, searchQuery, expandInternal);
        assertTrue(result.get("Teacher").size() == 1);
    }

    @Test
    public void testNeqOperator() {
        SearchQuery searchQuery = getSearchQuery(entities, "teacherName", "marko", FilterOperators.neq);
        JsonNode result = searchDao.search(graph, searchQuery, expandInternal);
        assertTrue(result.get("Teacher").size() == 2);
    }

    @Test
    public void testRangeOperator() {
        List<Object> range = new ArrayList<>();
        range.add(1);
        range.add(3);
        SearchQuery searchQuery = getSearchQuery(entities, "serialNum", range, FilterOperators.between);
        JsonNode result = searchDao.search(graph, searchQuery, expandInternal);
        assertTrue(result.get("Teacher").size() == 2);
    }
    
    @Test
    public void testOrOperator() {
        List<Object> values = new ArrayList<>();
        values.add("marko");
        values.add("vedas");
        values.add(VALUE_NOT_PRESENT); 
        SearchQuery searchQuery = getSearchQuery(entities, "teacherName", values, FilterOperators.or);
        JsonNode result = searchDao.search(graph, searchQuery, expandInternal);
        assertTrue(result.get("Teacher").size() == 2);
    }

    @Test
    public void testStartsWithOperator() {
        SearchQuery searchQuery = getSearchQuery(entities, "teacherName", "ma", FilterOperators.startsWith);
        JsonNode result = searchDao.search(graph, searchQuery, expandInternal);
        assertTrue(result.get("Teacher").size() == 1);
    }
    @Test
    public void testNotStartsWithOperator() {
        SearchQuery searchQuery = getSearchQuery(entities, "teacherName", "ma", FilterOperators.notStartsWith);
        JsonNode result = searchDao.search(graph, searchQuery, expandInternal);
        assertTrue(result.get("Teacher").size() == 2);
    }

    @Test
    public void testEndsWithOperator() {
        SearchQuery searchQuery = getSearchQuery(entities, "teacherName", "as", FilterOperators.endsWith);
        JsonNode result = searchDao.search(graph, searchQuery, expandInternal);
        assertTrue(result.get("Teacher").size() == 2);
    }
    @Test
    public void testNotEndsWithOperator() {
        SearchQuery searchQuery = getSearchQuery(entities, "teacherName", "as", FilterOperators.notEndsWith);
        JsonNode result = searchDao.search(graph, searchQuery, expandInternal);
        assertTrue(result.get("Teacher").size() == 1);
    }
    @Test
    public void testMultiOperators() {
        SearchQuery searchQuery = getSearchQuery(entities, "teacherName", "a", FilterOperators.contains);
        //addes other filter
        searchQuery.getFilters().add(new Filter("serialNum", FilterOperators.lte, 1));
        searchQuery.getFilters().add(new Filter("serialNum", FilterOperators.lt, 3));
        searchQuery.getFilters().add(new Filter("serialNum", FilterOperators.gte, 3));
        searchQuery.getFilters().add(new Filter("serialNum", FilterOperators.gt, 1));

        JsonNode result = searchDao.search(graph, searchQuery, expandInternal);
        assertTrue(result.get("Teacher").size() == 0);
    }

    @Test
    public void testResponseLimit() {
        SearchQuery searchQuery = new SearchQuery(entities, offset, limit);
        JsonNode result = searchDao.search(graph, searchQuery, expandInternal);
        assertTrue(result.get("Teacher").size() == 1);
    }

    @PreDestroy
    public void shutdown() throws Exception {
        graph.close();
    }

    private SearchQuery getSearchQuery(List<String> rootTypes, String property, Object value, FilterOperators op) {
        SearchQuery searchQuery = new SearchQuery(rootTypes, 0, 100);
        List<Filter> filterList = new ArrayList<>();
        Filter filter = new Filter(property, op, value);
        filterList.add(filter);
        searchQuery.setFilters(filterList);
        return searchQuery;
    }

    private void populateGraph() {
        VertexWriter vertexWriter = new VertexWriter(graph, databaseProvider, "tid");
        Vertex v1 = vertexWriter.createVertex("Teacher");
        v1.property("serialNum", 1);
        v1.property("teacherName", "marko");
        Vertex v2 = vertexWriter.createVertex("Teacher");
        v2.property("serialNum", 2);
        v2.property("teacherName", "vedas");
        Vertex v3 = vertexWriter.createVertex("Teacher");
        v3.property("serialNum", 3);
        v3.property("teacherName", "jas");
    }

    private SearchQuery getSearchQuery(SearchQuery searchQuery, Filter filter, String type) {
        List<Filter> filterList = new ArrayList<Filter>();
        if (searchQuery.getFilters() != null) {
            filterList = searchQuery.getFilters();
        }
        filterList.add(filter);
        searchQuery.setFilters(filterList);
        searchQuery.setRootLabel(type);
        return searchQuery;
    }

    private Filter getFilterEqual(String property, String value) {
        Filter filter = new Filter(property, FilterOperators.eq, value);
        return filter;
    }
}
