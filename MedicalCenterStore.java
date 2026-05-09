import java.io.IOException;

public interface MedicalCenterStore {
    DataSetStatus status();

    MedicalCenterSnapshot loadSnapshot() throws IOException;

    void saveSnapshot(MedicalCenterSnapshot snapshot) throws IOException;
}
