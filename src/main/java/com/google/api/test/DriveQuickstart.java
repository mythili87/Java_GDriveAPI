package com.google.api.test;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DriveQuickstart {
    private static final String APPLICATION_NAME = "Google Drive Service";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "C:\\Users\\conflowence\\IntelliJ\\GDriveAPI_Testing\\src\\main\\resources\\ServiceAccountKey.json";


    private static Credential getCredentials() throws IOException {
       return MockGoogleCredential.fromStream(new FileInputStream(CREDENTIALS_FILE_PATH ))
                .createScoped(SCOPES);
    }

    public static Drive getDriveService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {

        FileList result = getDriveService().files().list()
                .setPageSize(20)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }

        List<String> parents = new ArrayList<>();
        parents.add("1c2M-jalBgxq9MA9v8Eh498kswiVJGmZt"); //info --> Invoices --> Netrovert
        File fileMetadata = new File();
        fileMetadata.setName("Netrovert_ts_we_06042022.pdf").setParents(parents).setMimeType("application/pdf");
        java.io.File filePath = new java.io.File("C:\\Mythili\\Timesheets&Invoices\\Netrovert\\Timesheets\\13\\Netrovert_ts_we_06042022.pdf");
        FileContent mediaContent = new FileContent("application/pdf", filePath);

        try {
            // upload updated agreement as a PDF file to the Team Drive folder
            File file = getDriveService().files().create(fileMetadata, mediaContent)
                    .setFields("id")  // remember to set this property to true!
                    .execute();
            System.out.println("File ID: " + file.getId());
        } catch (IOException ex) {
            System.out.println("Exception: {} " + ex.getMessage());
            throw new IOException("Exception: " + ex.getMessage());
        } catch (GeneralSecurityException ex) {
            System.out.println("Exception: {} " + ex.getMessage());
            throw new GeneralSecurityException("Exception: " + ex.getMessage());
        }

    }
}
