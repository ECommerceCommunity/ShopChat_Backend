@Component
public class BackupRunner implements CommandLineRunner {

    private final DataBackupService backupService;

    public BackupRunner(DataBackupService backupService) {
        this.backupService = backupService;
    }

    @Override
    public void run(String... args) {
        backupService.backupAllToJson();
    }
}
