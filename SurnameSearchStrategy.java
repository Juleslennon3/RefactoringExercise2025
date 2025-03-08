import java.util.List;

public class SurnameSearchStrategy implements SearchStrategy {
    @Override
    public Employee search(String query, List<Employee> employees) {
        if (query == null || query.trim().isEmpty()) {
            return null;
        }
        String targetSurname = query.trim().toLowerCase();
        for (Employee emp : employees) {
            if (emp != null) {
                String surname = emp.getSurname();
                if (surname != null && surname.toLowerCase().equals(targetSurname)) {
                    return emp;
                }
            }
        }
        return null;
    }
}
