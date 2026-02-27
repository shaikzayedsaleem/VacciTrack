# Model-View-Controller (MVC) for Swing-based AI Applications

## 1. The Model (Data & API Logic)
The Model handles the formulation of the prompt, the network request to the AI service, and parsing the response.
* **Entities:** POJOs like `PromptRequest.java` and `AIResponse.java`.
* **The AI Client:** A dedicated class (e.g., `GenAIClient.java`) that uses `java.net.http.HttpClient` or `HttpURLConnection` to send the JSON payload to your AI backend or a third-party API (like OpenAI/Google).
* **Responsibility:** Strictly handles formatting the text, adding API keys to the headers, executing the HTTP POST request, and extracting the generated text from the JSON response.

## 2. The View (Java Swing Interface)
The View is built entirely using `javax.swing.*` and `java.awt.*` components. 
* **Input Components:** `JTextArea` wrapped in a `JScrollPane` for the user to paste large blocks of text.
* **Action Components:** `JButton` (e.g., "Summarize Text") and a `JProgressBar` (set to indeterminate) to show the user that the AI is "thinking".
* **Output Components:** A read-only `JTextArea` to display the AI's generated response.
* **Responsibility:** Displaying the GUI and capturing user input. It knows nothing about APIs or HTTP requests.

## 3. The Controller (Multithreaded Action Logic)
The Controller bridges the View and the Model, but it **must** use multithreading to keep the UI responsive.
* **Implementation:** An `ActionListener` attached to the Generate button that launches a `javax.swing.SwingWorker`.
* **Action Flow:**
    1. **View:** User clicks "Generate".
    2. **Controller (EDT):** The ActionListener disables the "Generate" button and makes the `JProgressBar` visible so the user knows it's loading.
    3. **Controller (Background Thread):** The `SwingWorker.doInBackground()` method takes the text, passes it to the Model (`GenAIClient`), and waits for the network response.
    4. **Controller (EDT via `done()`):** Once the AI responds, `SwingWorker.done()` updates the output `JTextArea`, hides the progress bar, and re-enables the button.
