package utils;

public final class DemoPause {
    private static final long DEFAULT_DELAY_MS = 300;
    private static final long STEP_DELAY_MS = Long.getLong("demo.delay.ms", DEFAULT_DELAY_MS);
    private static final long FINAL_DELAY_MS = Long.getLong("demo.final.delay.ms", 1000);

    private DemoPause() {
    }

    public static void afterStep() {
        sleep(STEP_DELAY_MS);
    }

    public static void beforeBrowserClose() {
        sleep(FINAL_DELAY_MS);
    }

    private static void sleep(long delayMs) {
        if (delayMs <= 0) {
            return;
        }

        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
