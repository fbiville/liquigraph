package org.liquigraph.ogm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.liquigraph.ogm.entities.SomeEntity;
import org.neo4j.ogm.context.EntityGraphMapper;
import org.neo4j.ogm.context.MappingContext;
import org.neo4j.ogm.cypher.compiler.Compiler;
import org.neo4j.ogm.metadata.MetaData;
import org.neo4j.ogm.request.Statement;
import org.neo4j.ogm.session.request.RowStatementFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Poc {

    public static void main(String[] args) {
        // Liquigraph OGM XML -> ?
        String ogmXml = "";
        
        // entity -> Liquigraph core XML
        CrappyStatementXmlSerializer statementXmlSerializer = new CrappyStatementXmlSerializer();
        MetaData metaData = new MetaData("org.liquigraph.ogm.entities");
        MappingContext mappingContext = new MappingContext(metaData);
        EntityGraphMapper entityGraphMapper = new EntityGraphMapper(metaData, mappingContext);
        Compiler compiler = entityGraphMapper.compileContext().getCompiler();
        compiler.useStatementFactory(new RowStatementFactory());
        entityGraphMapper.map(someEntity());
        compiler
            .getAllStatements()
            .stream()
            .map(statementXmlSerializer)
            .forEach(System.out::println);
    }

    private static SomeEntity someEntity() {
        SomeEntity result = new SomeEntity();
//        result.setId(42L);
        result.setName("some name");
        result.setSomeOtherName("some other name");
        return result;
    }

    private static class CrappyStatementXmlSerializer implements Function<Statement, String> {

        private final ObjectMapper mapper;

        public CrappyStatementXmlSerializer() {
            mapper = new ObjectMapper();
        }

        @Override
        public String apply(Statement statement) {

            return String.format(
            "<parameterized-query>%n" +
            "    <query>%s</query>%n" +
            "    <parameters>%s</parameters>%n" +
            "</parameterized-query>",
            statement.getStatement(),
            writeParameters(statement.getParameters()).stream().collect(Collectors.joining("\n\t\t", "\n\t\t", "\n\t"))
            );
        }

        private List<String> writeParameters(Map<String, Object> parameters) {
            return parameters
            .entrySet()
            .stream()
            .sorted(Comparator.comparing(Map.Entry::getKey))
            .map(e -> String.format("<parameter name=\"%s\">%s</parameter>", e.getKey(), writeParameterValue(e.getValue())))
            .collect(Collectors.toList());
        }

        private String writeParameterValue(Object value) {
            try {
                return mapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
