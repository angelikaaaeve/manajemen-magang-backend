# Media Module - API Documentation for AI Agent

This document provides a concise overview of the endpoints, types, and behaviors for the `media` module. The base path for these endpoints is `/api/media`.

## Code Files
- Controller: `controller/MediaController.java`
- Service: `service/MediaService.java` (impl: `service/impl/MediaServiceImpl.java`)
- Repository: `repository/MediaRepository.java` (impl: `repository/impl/CloudflareMediaRepository.java`)

## Endpoints

### 1. Upload File
- **Endpoint:** `POST /upload`
- **Content-Type:** `multipart/form-data`
- **Behavior:** Uploads a file to the storage system and returns a generated unique key to access it.
- **Request Parameters:** 
  - `file` (MultipartFile / File, required): The file to be uploaded.
- **Responses:**
  - **200 OK (Success)**
    ```json
    {
      "message": "File uploaded successfully",
      "key": "filename-with-uuid-or-original-name"
    }
    ```
  - **500 Internal Server Error (Failure)**
    ```json
    {
      "error": "Failed to process file upload: [reason]"
    }
    ```

### 2. Get / View File
- **Endpoint:** `GET /{key}`
- **Behavior:** Retrieves the file content based on its key. The response is sent inline with the appropriate `Content-Type` guessed from the file extension (e.g., `image/png`, `application/pdf`, `image/jpeg`).
- **Path Variables:**
  - `key` (String, required): The unique key of the file.
- **Responses:**
  - **200 OK:** Raw file bytes (byte[]).
  - **404 Not Found:** If the file does not exist or cannot be retrieved.

### 3. Delete File
- **Endpoint:** `DELETE /{key}`
- **Behavior:** Deletes the specified file from the storage system.
- **Path Variables:**
  - `key` (String, required): The unique key of the file to delete.
- **Responses:**
  - **200 OK (Success)**
    ```json
    {
      "message": "File deleted successfully",
      "key": "filename-with-uuid-or-original-name"
    }
    ```
  - **500 Internal Server Error (Failure)**
    ```json
    {
      "error": "Failed to delete file: [reason]"
    }
    ```
