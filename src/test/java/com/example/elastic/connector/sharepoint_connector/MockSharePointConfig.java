package com.example.elastic.connector.sharepoint_connector;

import com.microsoft.graph.models.*;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.*;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@TestConfiguration
public class MockSharePointConfig {

    @Bean
    public GraphServiceClient mockGraphClient() {
        GraphServiceClient mockClient = Mockito.mock(GraphServiceClient.class, Mockito.RETURNS_DEEP_STUBS);

        // Create the complete fake structure
        Map<String, List<DriveItem>> structure = new HashMap<>();

        // --- Shared Documents (root) ---
        structure.put("root", List.of(
                folder("Engineering"),
                folder("HR"),
                folder("Projects")
        ));

        // --- Engineering ---
        structure.put("Engineering", List.of(
                folder("Designs"),
                folder("Reports"),
                folder("Archive")
        ));

        // --- Designs ---
        structure.put("Designs", List.of(
                folder("CAD"),
                folder("BOM")
        ));

        // --- CAD ---
        structure.put("CAD", List.of(
                folder("v1"),
                folder("v2")
        ));

        structure.put("v1", List.of(
                file("wheel_design_v1.dwg"),
                file("frame_blueprint_v1.dxf")
        ));

        structure.put("v2", List.of(
                file("frame_blueprint_v2.dxf")
        ));

        // --- BOM ---
        structure.put("BOM", List.of(
                file("materials_list.xlsx"),
                file("parts_costing_v3.csv")
        ));

        // --- Reports ---
        structure.put("Reports", List.of(
                file("weekly_report_01.docx"),
                file("weekly_report_02.docx")
        ));

        // --- Archive ---
        structure.put("Archive", List.of(
                folder("old_project_docs"),
                folder("temp")
        ));

        structure.put("old_project_docs", List.of(
                file("2019_specifications.pdf")
        ));

        structure.put("temp", List.of(
                file("dummy.txt"),
                file("temp_design.png")
        ));

        // --- HR ---
        structure.put("HR", List.of(
                folder("Policies"),
                folder("Employee Records"),
                folder("Payroll")
        ));

        structure.put("Policies", List.of(
                file("leave_policy.pdf"),
                file("code_of_conduct.docx")
        ));

        structure.put("Employee Records", List.of(
                folder("2024"),
                folder("2025")
        ));

        structure.put("2024", List.of(
                file("emp_001.json"),
                file("emp_002.json")
        ));

        structure.put("2025", List.of(
                file("emp_001_review.docx"),
                file("emp_002_review.docx")
        ));

        structure.put("Payroll", List.of(
                folder("payslips")
        ));

        structure.put("payslips", List.of(
                file("emp_001_sep2025.pdf"),
                file("emp_002_sep2025.pdf")
        ));

        // --- Projects ---
        structure.put("Projects", List.of(
                folder("Project Alpha"),
                folder("Project Beta")
        ));

        // --- Project Alpha ---
        structure.put("Project Alpha", List.of(
                folder("Documentation"),
                folder("Data"),
                folder("Presentation")
        ));

        structure.put("Documentation", List.of(
                file("design_specs_v1.pdf"),
                file("design_specs_v2.pdf")
        ));

        structure.put("Data", List.of(
                file("results.csv"),
                folder("logs")
        ));

        structure.put("logs", List.of(
                file("build_log_01.txt"),
                file("error_log.txt")
        ));

        structure.put("Presentation", List.of(
                file("overview.pptx"),
                file("financials.xlsx")
        ));

        // --- Project Beta ---
        structure.put("Project Beta", List.of(
                folder("Datasets")
        ));

        structure.put("Datasets", List.of(
                folder("input"),
                folder("output")
        ));

        structure.put("input", List.of(
                file("data_input_v1.json"),
                file("data_input_v2.json")
        ));

        structure.put("output", List.of(
                file("processed_output_01.csv"),
                file("summary.docx")
        ));

        // --- Mock the Graph API calls ---
        when(mockClient
                .drives()
                .byDriveId(anyString())
                .items()
                .byDriveItemId(anyString())
                .children()
                .get())
//                .thenReturn(response);
                .thenAnswer(invocation -> {
                    DriveItemCollectionResponse response = new DriveItemCollectionResponse();
                    response.setValue(structure.getOrDefault("root", Collections.emptyList())); // simple for root
                    return response;
                });

        return mockClient;
    }

    private static DriveItem folder(String name) {
        DriveItem item = new DriveItem();
        item.setId(name);
        item.setName(name);
        item.setFolder(new Folder());
        return item;
    }

    private static DriveItem file(String name) {
        DriveItem item = new DriveItem();
        item.setId(name);
        item.setName(name);
        item.setFile(new File());
        return item;
    }
}

