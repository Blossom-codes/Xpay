package com.dot.Xpay.scheduler;

import com.dot.Xpay.dto.response.TransactionSummaryResponse;
import com.dot.Xpay.enums.GenerationMode;
import com.dot.Xpay.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionScheduler {

    private final TransactionService transactionService;

    // Runs daily at 00:01 AM
    @Scheduled(cron = "0 1 0 * * ?")
    public void generatePreviousDaySummary() {

        LocalDate yesterday = LocalDate.now().minusDays(1);

        TransactionSummaryResponse summary =
                transactionService.generateAndSaveSummary(yesterday, GenerationMode.GENERATE_AND_PERSIST);

        log.info("Daily summary generated for {} -> {}", yesterday, summary);
    }

}
