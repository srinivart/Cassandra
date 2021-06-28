package com.srinivart;

import com.datastax.driver.core.Session;
import com.datastax.oss.driver.api.core.CqlSession;
import com.srinivart.model.Student;
import com.srinivart.read.CassandraConnector;
import com.srinivart.read.KeyspaceRepository;
import com.srinivart.repo.StudentRepository;
import com.srinivart.repo.StudentRepositoryy;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SpringBootTest
class DemoApplicationTests {

	private static final Logger LOG = LoggerFactory.getLogger(DemoApplicationTests.class);
	//private KeyspaceRepository schemaRepository;
	//private CqlSession session;

	CassandraConnector client;
	private KeyspaceRepository keyspaceRepository;
	private CqlSession session;



	@Before
	public void connect() {
		client = new CassandraConnector();
		client.connect("127.0.0.1", 9042, "datacenter1");
		 session = client.getSession();
		 keyspaceRepository = new KeyspaceRepository(session);
	}

	@Test
	public void readFromDB(){
		client = new CassandraConnector();
		client.connect("127.0.0.1", 9042, "datacenter1");
		session = client.getSession();
		keyspaceRepository = new KeyspaceRepository(session);
		//keyspaceRepository.createKeyspace("Bookstore", 1);
		keyspaceRepository.useKeyspace("srinivart");
		StudentRepositoryy studentRepository = new StudentRepositoryy(session);
		studentRepository.createTable();

		studentRepository.insertLibrary(new Student(1,"srini"));
		studentRepository.insertLibrary(new Student(2,"sahi"));
		studentRepository.insertLibrary(new Student(3,"teju"));

		List<Student> students = studentRepository.selectAll();

		students.forEach(x -> LOG.info(x.toString()));

		client.close();

	}



}
