## 📚 Generating and Viewing API Documentation (Javadoc)

The API documentation is generated directly from the Javadoc comments (`/** ... */`) within the source code. It covers the entire `com.example.elastic` package structure.

### 1. <img src="src/main/resources/img.png" alt="Terminal Icon" width="40px" height="40px"> Command Line (CLI)


This method uses the standard JDK `javadoc` tool.

1.  **Navigate:** Open your terminal and change the directory to the **root** of the project (the folder containing the `src` directory).
2.  **Generate Documentation:** Execute the following command. The output will be saved into a new folder named `docs`.

    ```bash
    # Ensure you are running this from the project root.
    javadoc -d docs -sourcepath src -subpackages com.example.elastic
    ```
3.  **Open Documentation:** Open the main entry file in your web browser.

    ```bash
    # Example (Linux/macOS)
    open docs/index.html
    ```

---

### 2. <img src="src/main/resources/img.png" alt="Terminal Icon" width="40px" height="40px"> IntelliJ IDEA

IntelliJ has a built-in feature to simplify Javadoc generation.

1.  **Select Target:** In the **Project** view, right-click on the **`src`** directory or the top-level **`com.example.elastic`** package.
2.  **Generate:** Go to **Tools** → **Generate Javadoc...**
3.  **Configure Settings:**
    * **Output directory:** Set this to `docs`.
    * **Visibility:** Select **Public** or **Protected**.
    * **Other command line arguments:** You may add options like `-html5 -Xdoclint:none` here.
4.  **Execute:** Click **OK** or **Generate**.
5.  **Open:** Navigate to the generated `docs/index.html` file and open it in your browser.

---

### 3. <img src="src/main/resources/img.png" alt="Terminal Icon" width="40px" height="40px"> Eclipse

Use the built-in Javadoc wizard in Eclipse.

1.  **Access Wizard:** Go to **Project** → **Generate Javadoc...**
2.  **Configuration:**
    * **Javadoc command:** Verify this points to the `javadoc.exe` file in your installed JDK.
    * **Select Packages:** Ensure the **`com.example.elastic`** package is selected.
    * **Destination:** Set the output folder to `docs`.
    * **Visibility:** Choose the desired visibility level (e.g., `protected`).
3.  **Finish:** Complete the wizard and click **Finish**.
4.  **Open:** Navigate to the generated `docs/index.html` file and open it in your browser.