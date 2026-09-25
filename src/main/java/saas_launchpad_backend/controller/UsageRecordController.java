package saas_launchpad_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import saas_launchpad_backend.entity.UsageRecord;
import saas_launchpad_backend.service.UsageRecordService;
import saas_launchpad_backend.dto.UsageDashboardDTO;
import saas_launchpad_backend.dto.UsageSummaryDTO;

@RestController
@RequestMapping("/api/usage")
public class UsageRecordController {

    private final UsageRecordService usageRecordService;

    public UsageRecordController(
            UsageRecordService usageRecordService) {

        this.usageRecordService = usageRecordService;
    }

    // ==========================================
    // RECORD USAGE
    // ==========================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsageRecord recordUsage(
            @RequestParam String metricName,
            @RequestParam Long usageCount,
            Authentication authentication) {

        String email = authentication.getName();

        return usageRecordService.recordUsage(
                metricName,
                usageCount,
                email
        );
    }

    // ==========================================
    // GET ALL USAGE RECORDS
    // ==========================================

    @GetMapping
    public List<UsageRecord> getUsageRecords(
            Authentication authentication) {

        String email = authentication.getName();

        return usageRecordService.getUsageRecords(email);
    }

    // ==========================================
    // GET USAGE BY METRIC
    // ==========================================

    @GetMapping("/metric")
    public List<UsageRecord> getUsageByMetric(
            @RequestParam String metricName,
            Authentication authentication) {

        String email = authentication.getName();

        return usageRecordService.getUsageByMetric(
                metricName,
                email
        );
    }

    // ==========================================
    // GET TOTAL USAGE
    // ==========================================

    @GetMapping("/total")
    public Long getTotalUsage(
            @RequestParam String metricName,
            Authentication authentication) {

        String email = authentication.getName();

        return usageRecordService.getTotalUsage(
                metricName,
                email
        );
    }

    // ==========================================
    // GET USAGE DASHBOARD
    // ==========================================

    @GetMapping("/dashboard")
    public UsageDashboardDTO getDashboard(
            @RequestParam String metricName,
            Authentication authentication) {

        String email = authentication.getName();

        return usageRecordService.getDashboard(
                metricName,
                email
        );
    }

    // ==========================================
    // GET USAGE SUMMARY
    // ==========================================

    @GetMapping("/summary")
    public List<UsageSummaryDTO> getUsageSummary(
            Authentication authentication) {

        String email = authentication.getName();

        return usageRecordService.getUsageSummary(email);
    }
}