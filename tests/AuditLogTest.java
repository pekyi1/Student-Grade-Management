
import services.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AuditLogTest {
    private AuditLogService auditService;
    private final String LOG_DIR = "logs/audit";

    @BeforeEach
    public void setUp() {
        // Clean up logs before test
        cleanupLogs();
        auditService = new AuditLogService();
    }

    @AfterEach
    public void tearDown() {
        if (auditService != null) {
            auditService.shutdown();
        }
        // cleanupLogs(); // keep logs for inspection if needed
    }

    private void cleanupLogs() {
        try {
            Path dir = Paths.get(LOG_DIR);
            if (Files.exists(dir)) {
                Files.walk(dir)
                        .filter(Files::isRegularFile)
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                            }
                        });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBasicLogging() throws InterruptedException, IOException {
        auditService.log("TEST_OP", "Details 1", "USER1", true);
        auditService.log("TEST_OP", "Details 2", "USER1", false);

        // Force flush
        auditService.flushBuffer();

        // Check recent logs
        List<String> logs = auditService.getRecentLogs(10);
        assertTrue(logs.size() >= 2, "Should have logs");
        assertTrue(logs.get(0).contains("TEST_OP"));
        assertTrue(logs.get(0).contains("USER1"));
    }

    @Test
    public void testConcurrentLogging() throws InterruptedException, IOException {
        int threadCount = 10;
        int logsPerThread = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < logsPerThread; j++) {
                        auditService.log("CONCURRENT", "Thread " + threadId + " msg " + j, "TEST", true);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executor.shutdown();

        // Wait for asynchronous writer to catch up
        Thread.sleep(1000);
        auditService.flushBuffer();

        // Verify total lines
        int expectedLogs = threadCount * logsPerThread;
        List<String> logs = auditService.searchLogs("CONCURRENT");
        assertEquals(expectedLogs, logs.size(), "Should have recorded all concurrent logs");
    }
}
