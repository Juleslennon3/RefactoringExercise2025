import java.util.List;

public interface SearchStrategy {
    /**
     * Searches for an Employee in the given list according to some criterion.
     * @param query the search query (could be ID or surname)
     * @param employees the list of all employees (null entries represent empty records)
     * @return the matching Employee, or null if not found
     */
    Employee search(String query, List<Employee> employees);
}
