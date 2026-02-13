package service;

import domain.address.State;
import domain.person.Employee;
import dto.EmployeePersonDTO;
import dto.ProfessionDTO;
import repository.EmployeeProfessionRepository;
import repository.EmployeeRepository;
import repository.ProfessionRepository;

import java.util.List;

public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeProfessionRepository employeeProfessionRepository;
    private final ProfessionRepository professionRepository;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeProfessionRepository employeeProfessionRepository, ProfessionRepository professionRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeProfessionRepository = employeeProfessionRepository;
        this.professionRepository = professionRepository;
    }

    public void register(Integer userId) {
        if (employeeRepository.existsByUserId(userId)) {
            throw new RuntimeException("User is already an employee");
        }

        Employee employee = Employee.create(userId);
        employeeRepository.save(employee);
    }

    public void deactivating(Integer employeeId) {
        EmployeePersonDTO employee = findOrThrow(employeeId);

        if (!employee.active()) {
            throw new RuntimeException("Employee already inactive");
        }

        if (employee.working()) {
            throw new RuntimeException("Cannot deactivate employee while working");
        }

        employeeRepository.deactivating(employeeId);
    }

    public void activating(Integer employeeId) {
        EmployeePersonDTO employee = findOrThrow(employeeId);

        if (employee.active()) {
            throw new RuntimeException("Employee already active");
        }

        employeeRepository.activating(employeeId);
    }

    public List<EmployeePersonDTO> findByName(String name) {
        List<EmployeePersonDTO> employees = employeeRepository.findByName(name);

        loadProfessions(employees);

        return employees;
    }

    private void loadProfessions(List<EmployeePersonDTO> employees) {
        for (EmployeePersonDTO employee : employees) {

            List<ProfessionDTO> professions =
                    professionRepository.findByEmployeeId(employee.employeeId());

            employee.professions().addAll(professions);
        }
    }

    public List<EmployeePersonDTO> findByAddress(int codeUf, String city) {
        String state = State.fromCode(codeUf).name();

        if (city == null || city.isBlank()) {
            throw new RuntimeException("City is required");
        }

        city = city.trim();

        List<EmployeePersonDTO> employees = employeeRepository.findByAddress(state, city);

        loadProfessions(employees);

        return employees;
    }

    public List<EmployeePersonDTO> findByTagAndCityById(Integer id, int codeUf, String city) {


        String state = State.fromCode(codeUf).name();

        if (city == null || city.isBlank()) {
            throw new RuntimeException("City is required");
        }

        city = city.trim();

        List<EmployeePersonDTO> employees = employeeRepository.findByCodeAndCity(id, city, state);
        loadProfessions(employees);
        return employees;
    }

    private EmployeePersonDTO findOrThrow(Integer employeeId) {
        return employeeRepository.findEmployeeDetailsById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public EmployeePersonDTO findById(Integer employeeId) {
        EmployeePersonDTO employee = findOrThrow(employeeId);

        List<Integer> professionsIds = employeeProfessionRepository.findProfessionIdsByEmployeeId(employeeId);

        List<ProfessionDTO> professions = professionRepository.findByIds(professionsIds);

        return EmployeePersonDTO.builder()
                .employeeId(employee.employeeId())
                .personId(employee.personId())
                .userId(employee.userId())
                .name(employee.name())
                .documentValue(employee.documentValue())
                .documentType(employee.documentType())
                .working(employee.working())
                .active(employee.active())
                .createdAt(employee.createdAt())
                .address(employee.address())
                .professions(professions)
                .build();
    }

    public void addProfession(Integer employeeId, Integer professionId) {
        EmployeePersonDTO employee = findOrThrow(employeeId);

        if (!employee.active()) {
            throw new RuntimeException("Cannot add profession to inactive employee");
        }

        if (!professionRepository.existsById(professionId)) {
            throw new RuntimeException("Profession not found");
        }

        if (employeeProfessionRepository.existsEmployeeIdAndProfessionId(employeeId, professionId)) {
            throw new RuntimeException("Profession already has this profession");
        }

        employeeProfessionRepository.addProfessionToEmployee(employeeId, professionId);
    }

    public void removeProfession(Integer employeeId, Integer professionId) {
        EmployeePersonDTO employee = findOrThrow(employeeId);

        if (!employee.active()) {
            throw new RuntimeException("Cannot remove profession from inactive employee");
        }

        if (!professionRepository.existsById(professionId)) {
            throw new RuntimeException("Profession not found");
        }

        if (!employeeProfessionRepository.existsEmployeeIdAndProfessionId(employeeId, professionId)) {
            throw new RuntimeException("Profession does not have this profession");
        }

        employeeProfessionRepository.removeProfessionFromEmployee(employeeId, professionId);
    }

    public List<ProfessionDTO> findAllProfessionsFromEmployee(Integer employeeId) {
        List<Integer> professionIds = employeeProfessionRepository.findProfessionIdsByEmployeeId(employeeId);
        return professionRepository.findByIds(professionIds);
    }
}
