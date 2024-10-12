# File Sharing App

## Overview

The File Sharing App is a simple RESTful API built using Java, designed for securely uploading and downloading files. This application encrypts files using a passcode provided by the user during upload, ensuring that files are securely stored and inaccessible without the correct passcode. The application automatically deletes files older than 48 hours to maintain storage efficiency.

## Features

1. **File Upload**:
   - Accepts a single file and a passcode from the user.
   - Encrypts the file using the provided passcode before storage.
   - Returns a unique URL for downloading the uploaded file.

2. **File Download**:
   - Provides a unique URL that accepts a passcode for decryption.
   - Users can download the file only if they provide the correct passcode.

3. **Automatic File Deletion**:
   - Files uploaded more than 48 hours ago are automatically deleted.
   - The unique URL will return a 404 error if accessed after the file is deleted.

4. **No Authentication Required**:
   - The application does not require user login or authentication to access the endpoints.

5. **Validation and Error Handling**:
   - Validates file size and existence during upload.
   - Returns appropriate error messages for invalid operations.

## Technologies Used

- **Java**: Core programming language.
- **Spring Boot**: Framework for building the REST API.
- **Maven**: Dependency management.
- **JUnit & Mockito**: For unit testing.
- **Postman**: For testing API endpoints.

## API Endpoints

1. **Upload File**: `POST files/upload`
   - Request Body: Multipart file and passcode.
   - Response: Unique URL for downloading the file.

2. **Download File**: `GET files/download/{fileId}`
   - Request Parameters: `fileId`, `passcode`.
   - Response: Decrypted file download.

3. **File Cleanup**: Automatically handled by the application (scheduled task for files older than 48 hours).


## Getting Started

This section will guide you through the process of setting up and testing the File Sharing App.

### Prerequisites

Before you begin, ensure you have met the following requirements:

- **Java**: Version 11 or higher must be installed on your machine.
- **Maven**: Used for managing the project dependencies.
- **Postman**: A popular tool for testing APIs. You can download it from [Postman Download](https://www.postman.com/downloads/).

### Clone the Repository
1. Run the following command:

   ```bash
   git clone https://github.com/devrukhkarmansi/filesharingapp.git

Start the application.

### Test the APIs Using Postman

#### 1. Upload File API

To upload a file and provide a passcode:

1. Open Postman.
2. Create a new `POST` request to the following URL:

   ```bash
   http://localhost:8080/files/upload
3. In the Body tab, select **form-data**.
4. Add the following key-value pairs:
   - **Key:** `file` (select File as the type) - Choose the file you want to upload.
   - **Key:** `passcode` (select Text as the type) - Enter a secure passcode for encryption.
5. Click **Send**. You should receive a response containing a unique URL for downloading the file.

### Download File API

To download the file using the unique URL and passcode:

1. Create a new **GET** request in Postman.
2. Use the unique URL obtained from the upload response and append the passcode as a query parameter:
   

Click Send. You should receive the decrypted file as a response.


## Additional Notes

### Encryption Details

1. **File Encryption**:
   - Files are encrypted using the Advanced Encryption Standard (AES) algorithm. The encryption is performed in `Cipher Block Chaining (CBC)` mode, ensuring that identical plaintext blocks are encrypted into different ciphertext blocks, enhancing security.
   - The encryption key is derived from the user-provided passcode using the SHA-256 hashing algorithm. This creates a strong, fixed-length key that is suitable for AES.

2. **Key Management**:
   - The passcode is not stored on the server; only the encrypted files and their metadata are saved. This means that without the correct passcode, even server administrators cannot access the original file content.
   - The key is truncated to 16 bytes for AES-128 encryption, ensuring compatibility with the encryption standard.

3. **File Metadata**:
   - Metadata associated with each file (including the original file name and the path of the encrypted file) is saved in a database. This allows for efficient file management and retrieval while keeping sensitive information secure.

4. **File Expiry and Deletion**:
   - Files uploaded are automatically deleted after 48 hours. After this period, attempting to access the file via the unique URL will return a `404 Not Found` status, ensuring that stale data does not linger on the server.

### Testing and Validation
- Comprehensive unit and integration tests should be conducted to validate the functionality and security of the application.
- Testing should cover various scenarios, including valid and invalid file uploads, file retrieval with correct and incorrect passcodes, and attempts to access expired files.

### Future Enhancements
- Consider adding user authentication to restrict access to uploaded files.
- Explore the use of more advanced encryption techniques, such as public-key cryptography, for even greater security.
- Can upload files on S3 intead of same directory


