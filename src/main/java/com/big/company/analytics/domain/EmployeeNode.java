package com.big.company.analytics.domain;

import com.big.company.analytics.exception.EmployeeNodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a node in an employee hierarchy tree.
 */
public record EmployeeNode(
        Employee employee,
        List<EmployeeNode> subordinates
) {

    /**
     * Constructs an EmployeeNode object.
     *
     * @param employee     Employee (required)
     * @param subordinates Subordinates (required)
     * @throws NullPointerException if any params is null.
     */
    public EmployeeNode {
        Objects.requireNonNull(employee, "Employee must not be null");
        Objects.requireNonNull(subordinates, "Subordinates list must not be null");
    }

    /**
     * Constructs an EmployeeNode object. Subordinates list will be an empty list.
     *
     * @param employee Employee (required)
     * @throws EmployeeNodeException if the employee in the builder is null.
     */
    public EmployeeNode(Employee employee) {
        this(employee, new ArrayList<>());
    }

    /**
     * Adds an employee to the hierarchy as a subordinate of its manager.
     * If the manager of the {@code employeeToAdd} is not found in the hierarchy,
     * it returns false. In this case, it's important to note that the order in which employees
     * are added may affect this operation. The functionality for handling unordered
     * collections should be treated before.
     *
     * @param employeeToAdd The employee to add.
     * @return true if the employee was successfully added, false if the manager was not found.
     * @throws EmployeeNodeException if the employeeToAdd is null or if it doesn't have a manager.
     */
    public boolean addEmployee(Employee employeeToAdd) {
        if (employeeToAdd == null) throw new EmployeeNodeException("Employee must not be null");
        Integer managerId = employeeToAdd.getManagerId()
                .orElseThrow(() -> new EmployeeNodeException("Employee doesn't have a manager"));

        return addEmployeeRecursively(employeeToAdd, managerId);
    }

    /**
     * Adds an employee to the hierarchy as a subordinate of its manager.
     * If the manager of the {@code employeeToAdd} is not found in the hierarchy,
     * it returns false. In this case, it's important to note that the order in which employees
     * are added may affect this operation. The functionality for handling unordered
     * collections should be treated before.
     *
     * @param employeeToAdd The employee to add. Must not be null.
     * @param managerId     Manager id of the employee to be added. Must not be null.
     * @return true if the employee was successfully added, false if the manager was not found.
     */
    private boolean addEmployeeRecursively(Employee employeeToAdd, Integer managerId) {
        if (managerId.equals(employee.id())) {
            EmployeeNode employeeNode = new EmployeeNode(employeeToAdd);
            subordinates.add(employeeNode);
            return true;
        } else {
            for (EmployeeNode subordinate : subordinates) {
                if (subordinate.addEmployeeRecursively(employeeToAdd, managerId)) return true;
            }
        }
        return false;
    }

    /**
     * Gets the size of the hierarchy rooted at this node.
     *
     * @return The size of the hierarchy.
     */
    public int size() {
        int size = 1;
        for (EmployeeNode subordinate : subordinates) {
            size += subordinate.size();
        }
        return size;
    }
}
