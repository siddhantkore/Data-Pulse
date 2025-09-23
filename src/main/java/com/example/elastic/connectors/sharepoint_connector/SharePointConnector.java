//package com.example.elastic.connectors.sharepoint_connector;
//
//import com.example.elastic.service.DocumentService;
//import com.microsoft.graph.models.DriveItemCollectionResponse;
//import com.microsoft.graph.serviceclient.GraphServiceClient;
//import com.microsoft.graph.models.DriveItem;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class SharePointConnector {
//
//    @Value("${sharepoint.site.id}")
//    private String siteId;
//
//    @Value("${sharepoint.drive.id}")
//    private String driveId;
//
//    private final GraphServiceClient graphClient;
//    private final DocumentService documentService;
//
//    public SharePointConnector(GraphServiceClient graphClient, DocumentService documentService) {
//        this.graphClient = graphClient;
//        this.documentService = documentService;
//    }
//
//    // Runs every 5 minutes
//    @Scheduled(fixedRate = 300000)
//    public void fetchFiles() {
//        try {
//
//            DriveItemCollectionResponse driveItems = graphClient.sites()
//                    .bySiteId(siteId)
//                    .drives()
//                    .byDriveId(driveId)
//                    .root()
//                    .children()
//                    .get();
//
//            List<DriveItem> files = driveItems.getValue();
//
//            if (files != null) {
//                for (DriveItem file : files) {
//                    if (file.getFile() != null) {
//                        System.out.println("Found file: " + file.getName());
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Error fetching files from SharePoint: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}
