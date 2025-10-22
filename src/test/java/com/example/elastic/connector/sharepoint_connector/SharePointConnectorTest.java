//package com.example.elastic.connector.sharepoint_connector;
//
//import com.example.elastic.connectors.sharepoint_connector.SharePointConnector;
//import com.microsoft.graph.models.DriveItem;
//import com.microsoft.graph.models.DriveItemCollectionResponse;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.TestPropertySource;
//
//import java.util.List;
//
//@SpringBootTest(properties = {
//        "sharepoint.site.id=fake-site-id",
//        "sharepoint.drive.id=fake-drive-id"
//})
//@TestPropertySource(properties = {
//        "logging.level.root=WARN",
//        "logging.level.org.springframework.boot.autoconfigure=WARN",
//        "sharepoint.site.id=fake-site-id",
//        "sharepoint.drive.id=fake-drive-id"
//})
//@Import(MockSharePointConfig.class)
//class SharePointConnectorTest {
//
//    private final SharePointConnector connector;
//
//    @Autowired
//    SharePointConnectorTest(SharePointConnector connector) {
//        this.connector = connector;
//    }
//
//    @Test
//    void testFetchFilesWithMockData() {
//        List<DriveItem> files = connector.fetchFiles();
//        if (files != null) {
//            for (DriveItem file : files) {
//                if (file.getFile() != null) {
//                    System.out.println("Found file: " + file.getName());
//                }
//            }
//        }
//    }
//}
