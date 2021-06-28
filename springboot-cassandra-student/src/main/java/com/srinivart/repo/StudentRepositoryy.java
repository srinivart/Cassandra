package com.srinivart.repo;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert;
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.srinivart.model.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentRepositoryy {

    private static final String TABLE_NAME = "Library";
    private final CqlSession session;

    public StudentRepositoryy(CqlSession session) {
        this.session = session;
    }

    public void createTable() {
        createTable(null);
    }

    public void createTable(String keyspace) {
        CreateTable createTable = SchemaBuilder.createTable(TABLE_NAME).ifNotExists()
                .withPartitionKey("id", DataTypes.INT)
                .withColumn("name", DataTypes.TEXT);

        executeStatement(createTable.build(), keyspace);
    }

    public int insertLibrary(Student student){
           return insertLibrary(student,null);
    }

    public int insertLibrary(Student student,String keyspace){
        RegularInsert insertInto = QueryBuilder.insertInto(TABLE_NAME)
                .value("id", QueryBuilder.bindMarker())
                .value("name", QueryBuilder.bindMarker());

        SimpleStatement insertStatement = insertInto.build();

        if (keyspace != null) {
            insertStatement = insertStatement.setKeyspace(keyspace);
        }

        PreparedStatement preparedStatement = session.prepare(insertStatement);

        BoundStatement statement = preparedStatement.bind()
                .setInt(0,student.getId())
                .setString(1,student.getName());

             session.execute(statement);

               return student.getId();
    }

    public List<Student> selectAll() {
        return selectAll(null);
    }

    public List<Student> selectAll(String keyspace) {
        Select select = QueryBuilder.selectFrom(TABLE_NAME).all();
        ResultSet resultSet = executeStatement(select.build(), keyspace);

        List<Student> result = new ArrayList<>();
        resultSet.forEach(x -> result.add(
          new Student(x.getInt("id"),x.getString("name"))
        ));
        return result;
    }

    private ResultSet executeStatement(SimpleStatement statement, String keyspace) {
        if (keyspace != null) {
            statement.setKeyspace(CqlIdentifier.fromCql(keyspace));
        }

        return session.execute(statement);
    }

}
