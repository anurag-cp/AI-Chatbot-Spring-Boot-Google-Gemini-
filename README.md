# Gemini Chatbot

A small web chatbot built with Spring Boot and the Google GenAI Java SDK. The browser UI sends a user's message to a Spring REST endpoint, which asks Gemini to generate a reply.

## Features

- Responsive browser-based chat interface
- Spring Boot REST API at `POST /api/chat`
- Gemini text generation using the `gemini-3.8-flash` model
- Input validation with an 8,000-character limit
- Retries for temporary Gemini rate-limit and service-overload responses
- Clear error messages when the API is unavailable or not configured

## Requirements

- Java 25
- Maven 3.9.16 or newer (or the included Maven Wrapper)
- A Gemini API key from [Google AI Studio](https://aistudio.google.com/app/apikey)

## Configure the API key

Keep your key out of source control. The tracked `application.properties` intentionally contains a blank key. For local development:

1. Copy `src/main/resources/application-local.properties.example` to `src/main/resources/application-local.properties`.
2. Replace `PASTE_YOUR_GEMINI_API_KEY_HERE` with your Gemini API key.
3. Do not commit `application-local.properties`; it is ignored by Git.

The key is read from Spring's `google.apikey` property. The app can start without a key, but chat requests will not succeed until a valid key is configured.

## Run locally

From the project directory, activate the `local` Spring profile:

```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Or use the Maven Wrapper:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

Then open [http://localhost:8085/](http://localhost:8085/). Stop the server with `Ctrl+C`.

The server uses the `PORT` setting when supplied by a hosting platform, and defaults to port `8085` for local use.

## API

### `POST /api/chat`

Request JSON:

```json
{
  "message": "What is Java?"
}
```

Successful response:

```json
{
  "reply": "Java is a programming language...",
  "error": null
}
```

Errors use a non-2xx HTTP status and include an `error` message. Empty messages and messages longer than 8,000 characters are rejected.

## Build and test

Compile the application:

```powershell
mvn -DskipTests compile
```

Run the test suite:

```powershell
mvn test
```

## Project layout

```text
src/main/java/com/jsp/chatbot/
  ChatbotApplication.java        Spring Boot entry point
  controller/ChatController.java Chat REST endpoint and request validation
  service/ChatService.java       Gemini API integration
src/main/resources/
  application.properties        Shared, non-secret configuration
  application-local.properties.example
  static/index.html              Browser chat interface
src/test/java/com/jsp/chatbot/
  ChatbotApplicationTests.java   Spring context test
```

## Publish to GitHub

Create an empty repository on GitHub, then run these commands from the project directory. Replace the remote URL with your repository URL:

```powershell
git init -b main
git add .
git status --short
git commit -m "Initial chatbot project"
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git
git push -u origin main
```

Before committing, confirm that `src/main/resources/application-local.properties` is not listed by `git status` and that no API key appears in any file being committed.

## Security notes

- Never commit API keys or paste them into screenshots, issue reports, or public chat messages.
- If a key has been exposed, revoke it in Google AI Studio and create a replacement.
- Prompts are sent to Google's Gemini API. Do not enter confidential or sensitive information.
- Before making the service public, add access controls and request rate limiting to protect your API quota.

## License

No license has been added yet. Add a license file before allowing others to reuse or redistribute this project.
