package com.example.elastic.services.llm;

import com.example.elastic.exceptions.LLMServiceException;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service
public class LLMService {

    private final String OpenApiKey;
    private final String model;
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    private final String GeminiApiKey;
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    private OkHttpClient client = new OkHttpClient();

    public LLMService (@Value("${openai.api.key}") String openApiKey,
                       @Value("${openai.model}") String model,
                       @Value("${gemini.api.key}") String geminiApiKey) {

        this.OpenApiKey = openApiKey;
        this.model = model;
        this.GeminiApiKey = geminiApiKey;
    }

    String prompt = "You are an intelligent document classification and tagging assistant.  \n" +
            "You will be given raw text extracted from a document using OCR or text parsing.  \n" +
            "Your task is to analyze this text and return structured metadata that makes the document searchable.  \n" +
            "\n" +
            "### Instructions\n" +
            "1. Identify the main categories or type of document (e.g., Invoice, Resume, Medical Report, HR Document, Legal Document, Research Paper, Email, etc.).  \n" +
            "2. Extract 5–15 keywords that best describe the content of the document.  \n" +
            "3. Provide core metadata fields, which should always include:  \n" +
            "   - Title or Subject (if identifiable)  \n" +
            "   - Author / Sender / Organization (if identifiable)  \n" +
            "   - Date (if present in text)  \n" +
            "   - Entities (names, places, companies, IDs, etc.)  \n" +
            "   - Summary (2–3 sentences only)  \n" +
            "4. Detect and extract 5-7 document-specific important fields that vary by type of document. Examples:  \n" +
            "   - For HR docs: \"Approved By\", \"Employee Name\", \"Manager Name\", \"Joining Date\", etc.  \n" +
            "   - For Invoices: \"Invoice Number\", \"Total Amount\", \"Due Date\", \"Tax ID\".  \n" +
            "   - For Legal docs: \"Case Number\", \"Court\", \"Judge\", \"Parties\".  \n" +
            "   - For Medical docs: \"Patient Name\", \"Doctor Name\", \"Diagnosis\", \"Prescription\".  \n" +
            "   - If the document mentions presence of logos, stamps, signatures, seals, or other marks → include `\"Logo Present\": true/false` or similar.  \n" +
            "   - Add any other unique field that makes this document more searchable.  \n" +
            "\n" +
            "### Output Format\n" +
            "Return ONLY valid JSON.\n"+
            "Do not include explanations, markdown formatting, or code fences.\n"+
            "Do not add ```json or ``` anywhere.\n" +
            "Output must begin with { and end with }.\n"+
            "\"Return the answer strictly in JSON with the following structure:\n" +
            "\n" +
            "```json\n" +
            "{\n" +
            "  \"categories\": [\"...\"],\n" +
            "  \"keywords\": [\"...\"],\n" +
            "  \"metadata\": {\n" +
            "    \"title\": \"...\",\n" +
            "    \"author_or_sender\": \"...\",\n" +
            "    \"date\": \"...\",\n" +
            "    \"entities\": [\"...\"],\n" +
            "    \"summary\": \"...\"\n" +
            "  },\n" +
            "  \"document_specific_fields\": {\n" +
            "    \"field_name_1\": \"...\",\n" +
            "    \"field_name_2\": \"...\",\n" +
            "    \"field_name_3\": \"...\",\n" +
            "    \"...\": \"...\"\n" +
            "  }\n" +
            "}\n";

    /**
     * Calls the LLM with OCR-extracted text.
     *
     * @param extractedText OCR text from your pipeline
     * @return LLM response as string
     * @throws IOException, LLMServiceException if network/API call fails
     */
    public String processWithOpenAPI(String extractedText) throws IOException {
        prompt = prompt + extractedText;

        JSONObject requestBody = new JSONObject()
                .put("models", model) // choose your models
                .put("messages", new org.json.JSONArray()
                        .put(new JSONObject()
                                .put("role", "system")
                                .put("content", "You are a helpful assistant."))
                        .put(new JSONObject()
                                .put("role", "user")
                                .put("content", prompt))
                );

        RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .header("Authorization", "Bearer " + OpenApiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseStr = response.body().string();
            JSONObject json = new JSONObject(responseStr);
            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();

        } catch (LLMServiceException llmServiceException) {
            llmServiceException.getLocalizedMessage();
            throw llmServiceException;

        } catch (IOException ioException) {
            ioException.getLocalizedMessage();
            throw new IOException();

        } catch (NullPointerException nullPointerException) {
            nullPointerException.getLocalizedMessage();
            throw new NullPointerException();
        }
    }

    /**
     * Calls Gemini with OCR-extracted text.
     *
     * @param extractedText OCR text from your pipeline
     * @return Gemini response as string
     * @throws IOException if network/API call fails
     */
    public String processWithGemini(String extractedText) throws Exception {
        prompt = prompt + extractedText;

        JSONObject requestBody = new JSONObject()
                .put("contents", new JSONArray()
                        .put(new JSONObject()
                                .put("parts", new JSONArray()
                                        .put(new JSONObject()
                                                .put("text", prompt)
                                        )
                                )
                        )
                );

        RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
        );

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(GEMINI_API_URL)).newBuilder()
                .addQueryParameter("key", GeminiApiKey)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new LLMServiceException("Unexpected code " + response);
            }
            String responseStr = response.body().string();
            JSONObject json = new JSONObject(responseStr);

            // Navigate Gemini's response format
            return json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
                    .trim();

        } catch (LLMServiceException llmServiceException) {
            llmServiceException.getLocalizedMessage();
            throw llmServiceException;

        } catch (IOException ioException) {
            ioException.getLocalizedMessage();
            throw new IOException();

        } catch (NullPointerException nullPointerException) {
            nullPointerException.getLocalizedMessage();
            throw new NullPointerException();
        }

    }

}
