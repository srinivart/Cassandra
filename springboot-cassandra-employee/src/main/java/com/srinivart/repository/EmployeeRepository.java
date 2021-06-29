package com.srinivart.repository;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.srinivart.model.Employee;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {

    private CqlSession session;

    public EmployeeRepository() {
    }

    public EmployeeRepository(CqlSession session) {
        this.session = session;
    }

    public void createKeyspace(String keyspaceName, int numberOfReplicas) {
        CreateKeyspace createKeyspace = SchemaBuilder.createKeyspace(keyspaceName)
                .ifNotExists()
                .withSimpleStrategy(numberOfReplicas);

        session.execute(createKeyspace.build());
    }

    public void useKeyspace(String keyspace) {
        session.execute("USE " + CqlIdentifier.fromCql(keyspace));
    }

    public void createTable(String keyspace, String tableName) {
        CreateTable createTable = SchemaBuilder.createTable(tableName).ifNotExists()
                .withPartitionKey("id", DataTypes.INT)
                .withColumn("name", DataTypes.TEXT)
                .withColumn("designation", DataTypes.TEXT)
                .withColumn("salary", DataTypes.INT);

        executeStatement(createTable.build(), keyspace);
    }

    private ResultSet executeStatement(SimpleStatement statement, String keyspace) {
        if (keyspace != null) {
            statement.setKeyspace(CqlIdentifier.fromCql(keyspace));
        }
        return session.execute(statement);
    }


    public CqlSession connector(String node, Integer port,final String dataCenter){
        CqlSessionBuilder builder = CqlSession.builder();
        builder.addContactPoint(new InetSocketAddress(node, port));
        builder.withLocalDatacenter(dataCenter);
        this.session = builder.build();
        return session;
    }

    public void insertData(Employee employee, String keyspace, String tableName){
        RegularInsert insertInto = QueryBuilder.insertInto(tableName)
                .value("id", QueryBuilder.bindMarker())
                .value("name", QueryBuilder.bindMarker())
                .value("designation", QueryBuilder.bindMarker())
                .value("salary", QueryBuilder.bindMarker());

        SimpleStatement insertStatement = insertInto.build();

        if (keyspace != null) {
            insertStatement = insertStatement.setKeyspace(keyspace);
        }

        PreparedStatement preparedStatement = session.prepare(insertStatement);

        BoundStatement statement = preparedStatement.bind()
                .setInt(0,employee.getId())
                .setString(1,employee.getName())
                .setString(2,employee.getDesignation())
                .setInt(3,employee.getSalary());

        session.execute(statement);
    }

    public CqlSession getSession() {
        return this.session;
    }

    public void close() {
        session.close();
    }

    public void connect() {
        connector("127.0.0.1", 9042, "datacenter1");
        session = getSession();
    }


    public List<Employee> selectAll(String keyspace,String tableName) {
        Select select = QueryBuilder.selectFrom(tableName).all();
        ResultSet resultSet = executeStatement(select.build(), keyspace);

        List<Employee> result = new ArrayList<>();
        resultSet.forEach(x -> result.add(
                new Employee(x.getInt("id"),x.getString("name"),x.getString("designation"),x.getInt("salary"))
        ));
        return result;
    }
    
}
