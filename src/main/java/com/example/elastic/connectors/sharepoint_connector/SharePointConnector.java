//package com.example.elastic.connectors.sharepoint_connector;
//
//import com.example.elastic.service.DocumentProcessorService;
//import com.microsoft.graph.models.DriveItemCollectionResponse;
//import com.microsoft.graph.serviceclient.GraphServiceClient;
//import com.microsoft.graph.models.DriveItem;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class SharePointConnector {
//
////    @Value("${sharepoint.site.id}")
//    private String siteId;
//
////    @Value("${sharepoint.drive.id}")
//    private String driveId;
//
//    private final GraphServiceClient graphClient;
//    private final DocumentProcessorService documentProcessorService;
//
//    public SharePointConnector(GraphServiceClient graphClient, DocumentProcessorService documentProcessorService) {
//        this.graphClient = graphClient;
//        this.documentProcessorService = documentProcessorService;
//    }
//
//    // Runs every 5 minutes
//    @Scheduled(fixedRate = 300000)
//    public List<DriveItem> fetchFiles() {
//        List<DriveItem> files = new ArrayList<>();
//        try {
//
//            DriveItemCollectionResponse driveItems = graphClient
//                    .drives()
//                    .byDriveId(driveId)
//                    .items()
//                    .byDriveItemId("root")
//                    .children()
//                    .get();
//
//            if (driveItems.getValue() != null) {
//                for (DriveItem file : driveItems.getValue()) {
//                    if (file.getFile() != null) {
//                        System.out.println("Found file: " + file.getName());
//                    }
//                    files.add(file);
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Error fetching files from SharePoint: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return files;
//    }
//}
