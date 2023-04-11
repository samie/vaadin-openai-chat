/*
 * Copyright 2023 Sami Ekblad, Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.example.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/** Java class to interact with Open AI API.
 *
 */
@Component
public class OpenAI {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private static final String MODEL = "gpt-3.5-turbo";

    private final String apiKey;
    private String latestUserInput;
    private List<Message> chatMessages;
    private List<Message> messages = new ArrayList();

    public OpenAI(@Value("${openai.apikey}")String apiKey) {
        this.apiKey = apiKey;
        this.latestUserInput = "";
        this.chatMessages = new ArrayList<>();
    }

    public CompletableFuture<List<Message>> sendAsync(String userInput) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return send(userInput);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<Message> send(String userInput) {
        latestUserInput = userInput;
        this.messages.add(new Message("user", userInput, Instant.now()));

        try {
            ChatRequest request = new ChatRequest(MODEL, this.messages);
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(request);

            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + this.apiKey);
            conn.setDoOutput(true);


            conn.getOutputStream().write(requestBody.getBytes());

            if (conn.getResponseCode() >= 400) {
                Scanner scanner = new Scanner(conn.getErrorStream());
                StringBuilder responseBuilder = new StringBuilder();
                while (scanner.hasNextLine()) {
                    responseBuilder.append(scanner.nextLine());
                }
                String errorBody = responseBuilder.toString();
                APIError error = objectMapper.readValue(errorBody, APIError.class);
                messages.add(new Message("system", error.getError().message, Instant.now()));
            } else {
                Scanner scanner = new Scanner(conn.getInputStream());
                StringBuilder responseBuilder = new StringBuilder();
                while (scanner.hasNextLine()) {
                    responseBuilder.append(scanner.nextLine());
                }
                String responseBody = responseBuilder.toString();
                ChatResponse response = objectMapper.readValue(responseBody, ChatResponse.class);
                this.messages.addAll(response.getChoices().stream().map(Choice::getMessage).collect(Collectors.toList()));
            }
        }catch (Exception e) {
            messages.add(new Message("system", e.getMessage(), Instant.now()));
        }
        return messages;
    }
    
    public static class ChatRequest {
        private String model;
        private List<Message> messages;

        public ChatRequest(String model, List<Message> messages) {
            this.model = model;
            this.messages = messages;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public void setMessages(List<Message> messages) {
            this.messages = messages;
        }

        public String toJsonString() throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        }
    }

    public static class ChatResponse {
        private String id;

        private String object;

        private long created;

        private String model;

        private Usage usage;

        private List<Choice> choices;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public long getCreated() {
            return created;
        }

        public void setCreated(long created) {
            this.created = created;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public Usage getUsage() {
            return usage;
        }

        public void setUsage(Usage usage) {
            this.usage = usage;
        }

        public List<Choice> getChoices() {
            return choices;
        }

        public void setChoices(List<Choice> choices) {
            this.choices = choices;
        }
    }

    public static class Choice {
        private int index;
        private Message message;
        private String finish_reason;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public String getFinish_reason() {
            return finish_reason;
        }

        public void setFinish_reason(String finish_reason) {
            this.finish_reason = finish_reason;
        }
    }

    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;

        // Getters and Setters

        public int getPrompt_tokens() {
            return prompt_tokens;
        }

        public void setPrompt_tokens(int prompt_tokens) {
            this.prompt_tokens = prompt_tokens;
        }

        public int getCompletion_tokens() {
            return completion_tokens;
        }

        public void setCompletion_tokens(int completion_tokens) {
            this.completion_tokens = completion_tokens;
        }

        public int getTotal_tokens() {
            return total_tokens;
        }

        public void setTotal_tokens(int total_tokens) {
            this.total_tokens = total_tokens;
        }
    }

    public static class Message {

        @JsonIgnore
        private Instant time;
        private String role;
        private String content;

        public Message() {
            this.time = Instant.now();
        }

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
            this.time = Instant.now();
        }

        public Message(String role, String content, Instant time) {
            this.role = role;
            this.content = content;
            this.time = time;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Instant getTime() {
            return time;
        }
    }

    public static class APIError {
        private ErrorDetail error;

        public ErrorDetail getError() {
            return error;
        }

        public void setError(ErrorDetail error) {
            this.error = error;
        }
    }

    public static class ErrorDetail {
        private String message;
        private String type;
        private String param;
        private String code;

        public String getMessage() {

            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getParam() {
            return param;
        }

        public void setParam(String param) {
            this.param = param;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

}