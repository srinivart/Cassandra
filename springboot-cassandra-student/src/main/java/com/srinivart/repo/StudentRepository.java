package com.srinivart.repo;

import com.srinivart.model.Student;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface StudentRepository extends CassandraRepository<Student,Integer> {

}
