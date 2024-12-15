# SynergySpace backend

## Current Features

### Authentication (`/auth`)

- [X] User Registration (`/register`)
  - Creates a new user based on the provided credentials.
  - Returns a JWT token upon successful registration.
- [X] User Login (`/login`)
  - Authenticates a user based on the provided credentials.
  - Returns a JWT token upon successful authentication.
- [X] Test Protected Endpoint (`/test_auth`)
  - Accessible only to authenticated users.
  - Returns user information extracted from the JWT token.

### Ideas (`/idea`)

- [x] Create Idea (`/`)
    - Creates a new idea associated with the authenticated user.
    - Requires a JWT token.
    - Supported statuses: `DRAFT`, `OPEN`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`.
- [x] Get All Ideas (`/`)
    - Retrieves a list of all ideas.
    - Requires a JWT token.
- [x] Get Idea by ID (`/{id}`)
    - Retrieves an idea with the specified ID.
    - Requires a JWT token.
- [x] Update Idea (`/{id}`)
    - Updates an idea with the specified ID.
    - Only accessible to the idea's owner.
    - Requires a JWT token.
- [X] Delete Idea (`/{id}`)
    - Deletes an idea with the specified ID.
    - Only accessible to the idea's owner.
    - Requires a JWT token.

## API Documentation

The API documentation is available at `http://localhost:8080/swagger-ui` (when running locally)

- TODO: Add swagger URL after deploying.

## Building & Running

To build or run the project, use one of the following tasks:

| Task                          | Description                                                          |
|-------------------------------|----------------------------------------------------------------------|
| `./gradlew run`               | Run the server                                                       |
| `./gradlew test`              | Run the tests                                                        |
| `./gradlew build`             | Build everything                                                     |
| `buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `buildImage`                  | Build the docker image to use with the fat JAR                       |
| `publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `run`                         | Run the server                                                       |
| `runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

## Future Plans (TODO)

### High Priority

- [ ] **Teams (`/team`)**:
  - [ ] Create a team.
  - [ ] Join a team.
  - [ ] Leave a team.
  - [ ] Manage team member roles.
  - [ ] Associate a team with an idea.
- [ ] **Tasks (`/task`)**:
  - [ ] Create a task within an idea.
  - [ ] Assign a task to a team member.
  - [ ] Change task status.
  - [ ] Comment on a task.
  - [ ] Associate a task with a timeline.
- [ ] **Timeline (`/timeline`)**:
  - [ ] Create a timeline for an idea.
  - [ ] Add milestones to the timeline.
  - [ ] Visualize task progress on the timeline.
- [ ] **Followers (`/follower`)**:
  - [ ] Follow an idea.
  - [ ] Unfollow an idea.
  - [ ] Receive notifications about updates to followed ideas.
- [ ] **Git Integration (`/git`)**:
  - [ ] Link a Git repository (GitHub, GitLab) to an idea.
  - [ ] Display repository information (commits, branches).

### Medium Priority

- [ ] **Tools**:
  - [ ] Kanban Boards (`/tool/kanban`).
  - [ ] Chat (`/tool/chat`).
  - [ ] File Storage (`/tool/storage`).
- [ ] **Search and Filtering**:
  - [ ] Full-text search across ideas, tasks, and teams.
  - [ ] Filtering by tags, categories, status, and creation date.
- [ ] **User Rating**:
  - [ ] Implement a rating system based on user activity and contributions.

### Low Priority

- [ ] **Notifications**:
  - [ ] Notifications for new comments, tasks, and status changes.
  - [ ] Customizable notification settings.
- [ ] **Profile Customization**:
  - [ ] Enhanced user profile settings.
  - [ ] Avatar upload.
- [ ] **Multilingual Support**:
  - [ ] Support for multiple interface languages.
