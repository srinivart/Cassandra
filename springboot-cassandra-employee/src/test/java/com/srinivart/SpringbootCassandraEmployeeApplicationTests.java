package com.srinivart;

import com.datastax.oss.driver.api.core.CqlSession;
import com.srinivart.model.Employee;
import com.srinivart.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.List;

@SpringBootTest()
class SpringbootCassandraEmployeeApplicationTests {


	EmployeeRepository employeeRepository = new EmployeeRepository();

	@Test
	void contextLoads() {
	}


	@Test
	void testTable(){
		CqlSession session= employeeRepository.connector("127.0.0.1", 9042, "datacenter1");
		employeeRepository.createKeyspace("Company",3);
		employeeRepository.useKeyspace("Company");
		employeeRepository.createTable("Company","Employee");

		employeeRepository.insertData(new Employee(1,"srini","Developer",65000),"Company","Employee");
		employeeRepository.insertData(new Employee(2,"sahi","QA",85000),"Company","Employee");
		employeeRepository.insertData(new Employee(3,"teju","Architect",95000),"Company","Employee");

		employeeRepository.close();
	}


	@Test
	void readFromTable(){
		CqlSession session= employeeRepository.connector("127.0.0.1", 9042, "datacenter1");
		employeeRepository.useKeyspace("Company");
		List<Employee> employeeList = employeeRepository.selectAll("Company","Employee");

		employeeList.forEach(x -> System.out.println(x.toString()));
	}
}
