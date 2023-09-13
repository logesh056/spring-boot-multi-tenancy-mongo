package com.tutorial.springbootmultitenancymongo.controller;

import com.tutorial.springbootmultitenancymongo.configuration.ApplicationProperties;
import com.tutorial.springbootmultitenancymongo.domain.Employee;
import com.tutorial.springbootmultitenancymongo.service.EmployeeService;
import com.tutorial.springbootmultitenancymongo.service.EncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;
    private final EncryptionService encryptionService;

    private final ApplicationProperties applicationProperties;

    public EmployeeController(EmployeeService employeeService, EncryptionService encryptionService, ApplicationProperties applicationProperties) {
        this.employeeService = employeeService;
        this.encryptionService = encryptionService;
        this.applicationProperties = applicationProperties;
    }


    @GetMapping()
    public List<Employee> get() {
        return employeeService.getAll();

    }

    @PostMapping()
    public Employee save(@RequestBody Employee employee) {
        logger.info("saving employee is " + employee.getEmail());
        return employeeService.save(employee);
    }

    @PutMapping(value = "/{id}")
    public Employee update(@PathVariable("id") String id, @RequestBody Employee employee) {

        Optional<Employee> emp = employeeService.getById(id);
        if (emp.isPresent()) {
            emp.get().setFirstName(employee.getFirstName());
            emp.get().setLastName(employee.getLastName());
            emp.get().setEmail(employee.getEmail());

            return employeeService.save(emp.get());
        }
        throw new RuntimeException("not found");

    }

    @GetMapping("/{id}")
    public String getEmployeeById(@PathVariable(value = "id") String id) {
        System.out.println("get method inside");
        return encryptionService.encrypt("mongodb+srv://logesh:LogeshInnoura@cluster0.9ibzjyp.mongodb.net/b4d34e42-79a6-478e-b3af-12ce7311fa09?retryWrites=true&w=majority",
                applicationProperties.getEncryption().getSecret(),
                applicationProperties.getEncryption().getSalt());
    }
    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable(value = "id") String id) {
        employeeService.deleteById(id);
    }

    @GetMapping("/encrypt/{srv}")
    public String encrypt(@PathVariable(value = "srv") String srv, @Value("${application.encryption.secret}") String secret,  @Value("${application.encryption.salt}") String salt) {
        System.out.println("inside srv");

        return encryptionService.decrypt("srv=mongodb+srv://logesh:LogeshInnoura@cluster0.9ibzjyp.mongodb.net/b4d34e42-79a6-478e-b3af-12ce7311fa09?retryWrites=true&w=majority",
                applicationProperties.getEncryption().getSecret(),
                applicationProperties.getEncryption().getSalt());
    }

}