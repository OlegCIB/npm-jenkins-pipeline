# standardNPMPipeline.groovy

**standardNPMPipeline.groovy** is a reusable Jenkins pipeline for Node.js/NPM projects.  
It provides a robust CI/CD workflow, configurable via UI parameters or overrides, and can be extended from your Jenkinsfile.

## Key Features

- **Standardized NPM CI/CD** for install, lint, test, coverage, audit, build, deployment, and NPM publish.
- **UI-Driven Configuration:** All major pipeline options are exposed as Jenkins parameters.
- **Extensible:** Projects can inject additional UI parameters and custom pipeline stages.
- **Environment/Branch Aware:** Supports staging/production deployments, conditional NPM publishing, and custom quality gates.

## How To Use

1. **Reference the library** in your `Jenkinsfile`:
    ```groovy
    @Library('your-shared-library') _
    ```

2. **Invoke** `standardNPMPipeline` in your Jenkinsfile.  
   You can use default configuration or override/extend with a map.

---

## Usage Examples

### 1. [Jenkinsfile.example1](./Jenkinsfile.example1)
Basic usage with all default parameters.

**Link:** [Jenkinsfile.example1](./Jenkinsfile.example1)

---

### 2. [Jenkinsfile.example2](./Jenkinsfile.example2)
Override configuration and add custom UI parameters and stages.

**Link:** [Jenkinsfile.example2](./Jenkinsfile.example2)

---

### 3. [Jenkinsfile.example3](./Jenkinsfile.example3)
React/Frontend project with extra parameters and pre/post deploy custom logic.

**Link:** [Jenkinsfile.example3](./Jenkinsfile.example3)

---

### 4. [Jenkinsfile.example4](./Jenkinsfile.example4)
Disable deploy stages for a library/package project; add library-specific parameters.

**Link:** [Jenkinsfile.example4](./Jenkinsfile.example4)

---

### 5. [Jenkinsfile.example5](./Jenkinsfile.example5)
Microservice with custom database migration and health-check parameters.

**Link:** [Jenkinsfile.example5](./Jenkinsfile.example5)

---

## Extending the Pipeline

You can add or override UI parameters by supplying an `additionalParameters` list, and inject custom logic in `customStages`.  
See the example Jenkinsfiles above for real-world usage.

---

## Parameters Reference

All standard parameters are defined in the top of `standardNPMPipeline.groovy` and exposed as Jenkins UI parameters (choice, string, booleanParam, credentials, etc).  
They cover Node.js version, build/test/lint commands, deployment toggles, artifact patterns, notification settings, NPM publish, SonarQube, and more.

---

## File Location

- Shared pipeline: [`vars/standardNPMPipeline.groovy`](./vars/standardNPMPipeline.groovy)

---

## License

See [LICENSE](./LICENSE) for details.
