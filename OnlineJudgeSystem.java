import java.io.*;
import java.util.*;
import java.sql.*;
public class OnlineJudgeSystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/online_judge";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "29112004Leo!";
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Online Judge System");
        while (true) {System.out.println("1. Submit Code\n2. View Submissions\n3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1: submitCode(scanner); break;
                case 2: viewSubmissions(); break;
                case 3: System.out.println("Exiting..."); return;
                default: System.out.println("Invalid choice, please try again.");}}
    }
    private static void submitCode(Scanner scanner) {
        System.out.print("Enter your user ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter problem ID: ");
        int problemId = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter your code (end with an empty line): ");
        StringBuilder code = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).isEmpty()) {code.append(line).append("\n");}
        boolean success = executeAndStoreSubmission(userId, problemId, code.toString());
        if (success) {System.out.println("Submission successful!");}
        else {System.out.println("Submission failed.");}
    }
    private static boolean executeAndStoreSubmission(int userId, int problemId, String code) {
        boolean isCorrect = executeUserCodeInDocker(code, problemId);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO submissions (user_id, problem_id, code, status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, problemId);
                stmt.setString(3, code);
                stmt.setString(4, isCorrect ? "Accepted" : "Wrong Answer");
                stmt.executeUpdate();
                updateLeaderboard(userId, isCorrect);
                return true;}}
        catch (SQLException e) {e.printStackTrace();return false;}
    }
    private static boolean executeUserCodeInDocker(String code, int problemId) {
        System.out.println("Executing user code...");return true;}
    private static void updateLeaderboard(int userId, boolean isCorrect) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM leaderboard WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {int totalScore = rs.getInt("total_score");
                        int totalSolved = rs.getInt("total_solved");
                        if (isCorrect) {totalScore += 10;totalSolved++;}
                        String updateQuery = "UPDATE leaderboard SET total_score = ?, total_solved = ? WHERE user_id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, totalScore);
                            updateStmt.setInt(2, totalSolved);
                            updateStmt.setInt(3, userId);
                            updateStmt.executeUpdate();}}
                    else {
                        String insertQuery = "INSERT INTO leaderboard (user_id, total_score, total_solved) VALUES (?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                            insertStmt.setInt(1, userId);
                            insertStmt.setInt(2, isCorrect ? 10 : 0);
                            insertStmt.setInt(3, isCorrect ? 1 : 0);
                            insertStmt.executeUpdate();}}}}}
        catch (SQLException e) {e.printStackTrace();}
    }
    private static String executeCode(String code) {
        try {File tempFile = File.createTempFile("submission", ".java");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {writer.write(code);}
            String command = "javac " + tempFile.getAbsolutePath();
            Process compileProcess = Runtime.getRuntime().exec(command);
            compileProcess.waitFor();
            if (compileProcess.exitValue() != 0) {return "Compilation Error";}
            String className = tempFile.getName().replace(".java", "");
            command = "java -cp " + tempFile.getParent() + " " + className;
            Process runProcess = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {output.append(line).append("\n");}
            tempFile.delete();
            return output.toString();}
        catch (IOException | InterruptedException e) {e.printStackTrace();return "Execution Error";}}
    private static void viewSubmissions() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM submissions";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    System.out.println("Submission ID: " + rs.getInt("id") + ", User ID: " + rs.getInt("user_id") +
                            ", Problem ID: " + rs.getInt("problem_id") + ", Status: " + rs.getString("status"));
                }
            }}
        catch (SQLException e) {e.printStackTrace();}}
    private static void viewLeaderboard() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM leaderboard ORDER BY total_score DESC LIMIT 10";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    System.out.println("User ID: " + rs.getInt("user_id") + ", Total Score: " + rs.getInt("total_score") +
                            ", Problems Solved: " + rs.getInt("total_solved"));}}}
        catch (SQLException e) {e.printStackTrace();}}
}