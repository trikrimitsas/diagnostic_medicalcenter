import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Text file persistence: one line per record, comma-separated fields. No domain rules.
 */
public class DataStore {
    public static final String DOCTORS_FILE = "doctors.txt";
    public static final String PATIENTS_FILE = "patients.txt";
    public static final String EXAMS_FILE = "exams.txt";
    public static final String APPOINTMENTS_FILE = "appointments.txt";

    private final Path baseDir;

    public DataStore(Path baseDir) {
        this.baseDir = baseDir;
    }

    public DataStore() {
        this(Paths.get("."));
    }

    public boolean exists(String fileName) {
        return Files.isRegularFile(baseDir.resolve(fileName));
    }

    public List<String> readAllLines(String fileName) throws IOException {
        Path p = baseDir.resolve(fileName);
        if (!Files.isRegularFile(p)) {
            return new ArrayList<>();
        }
        return Files.readAllLines(p, StandardCharsets.UTF_8);
    }

    public void writeAllLines(String fileName, List<String> lines) throws IOException {
        Path p = baseDir.resolve(fileName);
        Files.write(p, lines, StandardCharsets.UTF_8);
    }

    public boolean allDataFilesMissing() {
        return !exists(DOCTORS_FILE) && !exists(PATIENTS_FILE) && !exists(EXAMS_FILE) && !exists(APPOINTMENTS_FILE);
    }

    public boolean allDataFilesExist() {
        return exists(DOCTORS_FILE) && exists(PATIENTS_FILE) && exists(EXAMS_FILE) && exists(APPOINTMENTS_FILE);
    }

    public static List<String> nonEmptyLines(List<String> raw) {
        List<String> out = new ArrayList<>();
        for (String line : raw) {
            if (line != null && !line.trim().isEmpty()) {
                out.add(line.trim());
            }
        }
        return out;
    }
}
