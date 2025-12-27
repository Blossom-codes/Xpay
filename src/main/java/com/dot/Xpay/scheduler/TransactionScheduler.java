package com.dot.Xpay.scheduler;

import com.dot.Xpay.dto.response.TransactionSummaryResponse;
import com.dot.Xpay.enums.GenerationMode;
import com.dot.Xpay.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionScheduler {

    private final TransactionService transactionService;

    // Runs daily at 01:00 AM
    @Scheduled(cron = "0 0 1 * * ?", zone = "Africa/Lagos")
    @SchedulerLock(
            name = "generatePreviousDaySummary",
            lockAtMostFor = "10m",
            lockAtLeastFor = "5m"
    )
    public void generatePreviousDaySummary() {

        LocalDate yesterday = LocalDate.now().minusDays(1);

        TransactionSummaryResponse summary =
                transactionService.generateAndSaveSummary(yesterday, GenerationMode.GENERATE_AND_PERSIST);

        log.info("Daily summary generated for {} -> {}", yesterday, summary);
    }

    /**
     * Runs twice daily at 12:00 AM and 12:00 PM
     */
    @Scheduled(cron = "0 0 0/12 * * ?", zone = "Africa/Lagos")
    @SchedulerLock(
            name = "processDailyCommissions",
            lockAtLeastFor = "5m",
            lockAtMostFor = "10m"
    )
    public void processDailyCommissions() {

        log.info("Starting daily commission processing");

        try {
            transactionService.processCommissions();
        } catch (Exception ex) {
            log.error("Commission scheduler failed", ex);
        }

        log.info("Daily commission processing completed");
    }
}
