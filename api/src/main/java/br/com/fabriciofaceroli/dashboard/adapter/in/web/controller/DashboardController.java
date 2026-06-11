package br.com.fabriciofaceroli.dashboard.adapter.in.web.controller;

import br.com.fabriciofaceroli.dashboard.adapter.in.web.dto.DashboardSummaryResponse;
import br.com.fabriciofaceroli.dashboard.adapter.in.web.mapper.DashboardWebMapper;
import br.com.fabriciofaceroli.dashboard.application.port.in.GetDashboardSummaryPort;
import br.com.fabriciofaceroli.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController implements DashboardApiDocs {

    private final GetDashboardSummaryPort getDashboardSummaryPort;
    private final DashboardWebMapper dashboardWebMapper;

    @Override
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary() {
        var summary = getDashboardSummaryPort.getSummary();
        return ResponseEntity.ok(ApiResponse.success("Resumo do dashboard obtido com sucesso.", dashboardWebMapper.toResponse(summary)));
    }
}
