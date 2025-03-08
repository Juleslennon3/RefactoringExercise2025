public class ValidationUtil {

    /** Validates that the given string is a valid employee ID (numeric between 1 and maxRecords). */
    public static boolean isValidId(String idStr, int maxRecords) {
        if (idStr == null || idStr.trim().isEmpty()) return false;
        try {
            int id = Integer.parseInt(idStr.trim());
            return id >= 1 && id <= maxRecords;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Validates that the given PPS number is exactly 7 characters (alphanumeric). */
    public static boolean isValidPps(String pps) {
        if (pps == null) return false;
        String trimmed = pps.trim();
        return trimmed.length() == 7;
    }

    /** Validates that a name (first name or surname) is not null/empty. */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    /** Validates that the given salary string is a non-negative integer. */
    public static boolean isValidSalary(String salaryStr) {
        if (salaryStr == null || salaryStr.trim().isEmpty()) return false;
        try {
            int salary = Integer.parseInt(salaryStr.trim());
            return salary >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
