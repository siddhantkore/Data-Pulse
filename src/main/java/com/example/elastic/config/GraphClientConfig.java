//package com.example.elastic.config;
//
//import com.azure.identity.ClientSecretCredential;
//import com.azure.identity.ClientSecretCredentialBuilder;
//import com.microsoft.graph.serviceclient.GraphServiceClient;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
//@Configuration
//public class GraphClientConfig {
//
//    @Bean
//    public GraphServiceClient graphClient() {
//        ClientSecretCredential credential = new ClientSecretCredentialBuilder()
//                .clientId("YOUR_CLIENT_ID")
//                .clientSecret("YOUR_CLIENT_SECRET")
//                .tenantId("YOUR_TENANT_ID")
//                .build();
//
//        return new GraphServiceClient(credential);
//    }
//}
