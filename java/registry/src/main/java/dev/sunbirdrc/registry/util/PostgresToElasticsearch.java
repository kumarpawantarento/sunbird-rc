package dev.sunbirdrc.registry.util;

import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresToElasticsearch {

    public static void main(String[] args) {
        // Configure PostgreSQL connection
        String pgHost = "localhost";
        int pgPort = 5432;
        String pgDatabase = "registry";
        String pgUsername = "postgres";
        String pgPassword = "postgres";

        // Configure Elasticsearch connection
        String esHost = "localhost";
        int esPort = 9200;
        String esIndex = "student";

        // Connect to PostgreSQL
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://" + pgHost + ":" + pgPort + "/" + pgDatabase,
                pgUsername, pgPassword)) {

            // Create Elasticsearch client
            RestHighLevelClient esClient = new RestHighLevelClient(RestClient.builder(HttpHost.create(esHost + ":" + esPort)));

            // Fetch data from PostgreSQL
            PreparedStatement statement = connection.prepareStatement("SELECT \"ID\", \"@type\", osid, \"identityDetails_osid\", \"educationDetails_arr_osid\", \"name\", \"courseKey\", email, \"examCenter\", \"rollNumber\", \"phoneNumber\", \"osOwner\", \"osCreatedAt\", \"osUpdatedAt\", \"osCreatedBy\", \"osUpdatedBy\", claims_osid, \"schoolId_osid\", \"StudentName\", \"MotherFullName\", \"FatherFullName\", \"Gender\", mobile, \"EmailId\", \"Institute\", \"StudentKey\", \"userId\", \"centerKey\", address, \"RollNo\", \"EnrollmentNo\", \"DateOfBirth\", \"CourseName\", \"IsActive\", \"studentRegulatorAttest_osid\", \"studentRegulatorAttest_arr_osid\", \"orgLogo\"\n" +
                    "FROM public.\"V_Student\";");
            ResultSet resultSet = statement.executeQuery();

            // Build Elasticsearch bulk request
            BulkRequest bulkRequest = new BulkRequest();
            while (resultSet.next()) {
                System.out.print("RSSS:::"+resultSet.toString());
                // Create an Elasticsearch index request for each row
                IndexRequest indexRequest = new IndexRequest(esIndex)
                        .id(resultSet.getString("osid"))
                        // need to fix this json data
                        .source(resultSet.getString("name"), XContentType.JSON);
                bulkRequest.add(indexRequest);
            }

            // Execute bulk request and handle response
            BulkResponse bulkResponse = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (bulkResponse.hasFailures()) {
                System.err.println("Error occurred while indexing documents: " + bulkResponse.buildFailureMessage());
            } else {
                System.out.println("Data successfully indexed in Elasticsearch.");
            }

            // Close Elasticsearch client
            esClient.close();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
