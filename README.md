# Java Exercise Template
This template repository is used to create autograded Java exercise repositories for CBF Academy bootcamps. It includes a GitHub Classroom autograding workflow that scores student submissions on functionality and code quality by executing unit tests and submitting the code changes for review by an automated agent.

## Usage

1. Create a new repository for the exercise, with the following settings:
    - Template: Select this repository
    - Name: Use the `java-exercises-[exercise name]` naming convention, e.g. `java-exercises-spring-boot`
    - Visibility: Public (needed for Classroom)
2. After initialising, navigate to the repo settings and set as a template, so it can be used for assignments
3. Commit README, Maven assets, starter code and unit tests to the main branch
4. Create a solutions branch and commit any reference solutions code to it
5. Modify the `MAX_TESTS` environment variable to reflect the total number of tests available
6. Push all changes

## Testing

1. Create a new Classroom assignment using the exercise repo as the starter template, with the following settings
   - Repository visibility: Private
   - Grant students admin access to their repository: Disabled
   - Copy the default branch only: Enabled
   - Supported editor: Don't use an online IDE
   - Protected file paths: `.github/**/*`, `**/test/**/*`
   - Enable feedback pull requests: Enabled
2. Accept the assignment from a test account.
3. Commit and push
4. Review the Actions output and Feedback PR comment to ensure everything operates as expected
