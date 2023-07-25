# OpenAI Chat Vaadin App

This is a small sample how to use OpenAI chat completion API in Vaadin and Spring Boot to
create a own version of ChatGPT app using the Vaadin components like [MessageList](https://vaadin.com/docs/latest/components/message-list) 
and [MessageInput](https://vaadin.com/docs/latest/components/message-input). 

Read more about how the application was built in this blog post: [Building a Chatbot in Vaadin with OpenAI](https://dev.to/samiekblad/happy-path-building-a-chatbot-in-vaadin-with-openai-4b8a)

[Vaadin](https://vaadin.com/flow) is a web application development framework that allows developers to 
create rich, interactive web interfaces with Java. It provides pre-built 
UI components, simplified data binding, and server-side processing for a 
seamless development experience.

<img width="494" alt="open-ai-vaadin-chat" src="https://user-images.githubusercontent.com/991105/231206982-35d9053e-9bed-4c16-b4a1-e462e79a9288.png">

## Running the application

To run this application locally you need to get [OpenAI API key](https://platform.openai.com/account/api-keys) and
update the `open.apikey` in you `src/main/resources/application.properties`.

The project is a standard Maven project. To run it from the command line,
type `OPENAI_APIKEY=your_key_here mvnw` (Windows), or `OPENAI_APIKEY=your_key_here ./mvnw` (Mac & Linux), then open
http://localhost:8080 in your browser.

You can also import the project to your IDE of choice as you would with any
Maven project. Read more on [how to import Vaadin projects to different IDEs](https://vaadin.com/docs/latest/guide/step-by-step/importing) (Eclipse, IntelliJ IDEA, NetBeans, and VS Code).

## Deploying to Production

To create a production build, call `mvnw clean package -Pproduction` (Windows),
or `./mvnw clean package -Pproduction` (Mac & Linux).
This will build a JAR file with all the dependencies and front-end resources,
ready to be deployed. The file can be found in the `target` folder after the build completes.

Once the JAR file is built, you can run it using
`java -jar target/vaadin-openai-chat-1.0-SNAPSHOT.jar`

## Project structure

- `MainLayout.java` in `src/main/java` contains the navigation setup (i.e., the
  side/top bar and the main menu). This setup uses
  [App Layout](https://vaadin.com/docs/components/app-layout).
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `views` folder in `frontend/` contains the client-side JavaScript views of your application.
- `themes` folder in `frontend/` contains the custom CSS styles.

## Useful links

- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).
- Follow the tutorial at [vaadin.com/docs/latest/tutorial/overview](https://vaadin.com/docs/latest/tutorial/overview).
- Create new projects at [start.vaadin.com](https://start.vaadin.com/).
- Search UI components and their usage examples at [vaadin.com/docs/latest/components](https://vaadin.com/docs/latest/components).
- View use case applications that demonstrate Vaadin capabilities at [vaadin.com/examples-and-demos](https://vaadin.com/examples-and-demos).
- Build any UI without custom CSS by discovering Vaadin's set of [CSS utility classes](https://vaadin.com/docs/styling/lumo/utility-classes). 
- Find a collection of solutions to common use cases at [cookbook.vaadin.com](https://cookbook.vaadin.com/).
- Find add-ons at [vaadin.com/directory](https://vaadin.com/directory).
- Ask questions on [Stack Overflow](https://stackoverflow.com/questions/tagged/vaadin) or join our [Discord channel](https://discord.gg/MYFq5RTbBn).
- Report issues, create pull requests in [GitHub](https://github.com/vaadin).


## Deploying using Docker

To build the Dockerized version of the project, run

```
mvn clean package -Pproduction
docker build . -t vaadin-openai-chat:latest
```

Once the Docker image is correctly built, you can test it locally using

```
docker run -p 8080:8080 vaadin-openai-chat:latest
```
