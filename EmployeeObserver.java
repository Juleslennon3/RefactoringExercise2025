public interface EmployeeObserver {
    /**
     * Called when the employee data has been modified (added, edited, or deleted).
     * Implementing classes should update their view accordingly.
     */
    void employeeDataChanged();
}
