import java.util.List;

public class IdSearchStrategy implements SearchStrategy {
    @Override
    public Employee search(String query, List<Employee> employees) {
        if (query == null || query.trim().isEmpty()) {
            return null;
        }
        try {
            int id = Integer.parseInt(query.trim());
            if (id < 1 || id > employees.size()) {
                return null;
            }
            // Convert to zero-based index for list access
            return employees.get(id - 1);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
