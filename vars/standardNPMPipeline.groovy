def call(Map config = [:]) {
    // Define default parameters for UI
    def defaultParameters = [
        // Tool Configuration
        choice(
            name: 'NODE_VERSION',
            choices: ['NodeJS', 'NodeJS-16', 'NodeJS-18', 'NodeJS-20', 'NodeJS-Latest'],
            description: 'Node.js version to use (must be configured in Jenkins Global Tool Configuration)'
        ),
        
        // Feature Toggles
        booleanParam(
            name: 'ENABLE_LINTING',
            defaultValue: true,
            description: 'Enable code linting stage'
        ),
        booleanParam(
            name: 'ENABLE_TESTING',
            defaultValue: true,
            description: 'Enable testing stage'
        ),
        booleanParam(
            name: 'ENABLE_SECURITY_AUDIT',
            defaultValue: true,
            description: 'Enable npm security audit stage'
        ),
        booleanParam(
            name: 'ENABLE_QUALITY_GATE',
            defaultValue: false,
            description: 'Enable quality gate checks (requires SonarQube configuration)'
        ),
        booleanParam(
            name: 'ENABLE_COVERAGE',
            defaultValue: true,
            description: 'Enable code coverage reporting'
        ),
        booleanParam(
            name: 'ENABLE_NPM_PUBLISH',
            defaultValue: false,
            description: 'Enable NPM package publishing'
        ),
        booleanParam(
            name: 'ENABLE_STAGING',
            defaultValue: true,
            description: 'Enable staging deployment'
        ),
        booleanParam(
            name: 'ENABLE_PRODUCTION',
            defaultValue: true,
            description: 'Enable production deployment'
        ),
        
        // Branch Configuration
        string(
            name: 'STAGING_BRANCH',
            defaultValue: 'develop',
            description: 'Branch name for staging deployments'
        ),
        string(
            name: 'PRODUCTION_BRANCH',
            defaultValue: 'main',
            description: 'Branch name for production deployments'
        ),
        
        // Build Commands
        string(
            name: 'BUILD_COMMAND',
            defaultValue: 'npm run build',
            description: 'Command to build the application'
        ),
        string(
            name: 'TEST_COMMAND',
            defaultValue: 'npm test',
            description: 'Command to run tests'
        ),
        string(
            name: 'LINT_COMMAND',
            defaultValue: 'npm run lint',
            description: 'Command to run linting'
        ),
        
        // Deployment Commands
        text(
            name: 'STAGING_DEPLOY_COMMAND',
            defaultValue: 'npm run deploy:staging',
            description: 'Command to deploy to staging environment'
        ),
        text(
            name: 'PRODUCTION_DEPLOY_COMMAND',
            defaultValue: 'npm run deploy:production',
            description: 'Command to deploy to production environment'
        ),
        
        // Artifact Configuration
        string(
            name: 'BUILD_ARTIFACTS',
            defaultValue: 'dist/**/*',
            description: 'Pattern for build artifacts to archive'
        ),
        string(
            name: 'TEST_RESULTS_PATTERN',
            defaultValue: 'test-results.xml',
            description: 'Pattern for test result files'
        ),
        string(
            name: 'COVERAGE_PATTERN',
            defaultValue: 'coverage/cobertura-coverage.xml',
            description: 'Pattern for coverage report files'
        ),
        
        // Notification Configuration
        booleanParam(
            name: 'ENABLE_NOTIFICATIONS',
            defaultValue: false,
            description: 'Enable email notifications'
        ),
        string(
            name: 'NOTIFICATION_EMAIL',
            defaultValue: '',
            description: 'Email address for notifications (leave empty to disable)'
        ),
        
        // NPM Publishing Configuration
        credentials(
            name: 'NPM_CREDENTIALS_ID',
            credentialType: 'org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl',
            defaultValue: 'npm-token',
            description: 'Jenkins credentials ID for NPM authentication token'
        ),
        booleanParam(
            name: 'PUBLISH_ON_TAG',
            defaultValue: true,
            description: 'Publish NPM package only on tagged releases'
        ),
        string(
            name: 'TAG_PATTERN',
            defaultValue: 'v\\d+\\.\\d+\\.\\d+',
            description: 'Regex pattern for release tags (for NPM publishing)'
        ),
        
        // Quality Gate Configuration
        string(
            name: 'SONARQUBE_ENV',
            defaultValue: 'SonarQube',
            description: 'SonarQube environment name (configured in Jenkins)'
        ),
        string(
            name: 'SONAR_COMMAND',
            defaultValue: 'npm run sonar',
            description: 'Command to run SonarQube analysis'
        )
    ]
    
    // Merge additional parameters from config
    def allParameters = defaultParameters
    if (config.additionalParameters) {
        allParameters = defaultParameters + config.additionalParameters
    }
    
    // Configure pipeline properties with parameters
    properties([
        parameters(allParameters),
        // Add other properties if needed
        buildDiscarder(logRotator(numToKeepStr: '10')),
        disableConcurrentBuilds()
    ])
    
    // Default configuration merged with user config and parameters
    def defaultConfig = [
        // Tool Configuration
        nodeVersion: params.NODE_VERSION ?: 'NodeJS',
        
        // Feature Toggles
        enableLinting: params.ENABLE_LINTING != null ? params.ENABLE_LINTING : true,
        enableTesting: params.ENABLE_TESTING != null ? params.ENABLE_TESTING : true,
        enableSecurityAudit: params.ENABLE_SECURITY_AUDIT != null ? params.ENABLE_SECURITY_AUDIT : true,
        enableQualityGate: params.ENABLE_QUALITY_GATE != null ? params.ENABLE_QUALITY_GATE : false,
        enableCoverage: params.ENABLE_COVERAGE != null ? params.ENABLE_COVERAGE : true,
        enableNPMPublish: params.ENABLE_NPM_PUBLISH != null ? params.ENABLE_NPM_PUBLISH : false,
        enableStaging: params.ENABLE_STAGING != null ? params.ENABLE_STAGING : true,
        enableProduction: params.ENABLE_PRODUCTION != null ? params.ENABLE_PRODUCTION : true,
        
        // Branch Configuration
        stagingBranch: params.STAGING_BRANCH ?: 'develop',
        productionBranch: params.PRODUCTION_BRANCH ?: 'main',
        
        // Build Commands
        buildCommand: params.BUILD_COMMAND ?: 'npm run build',
        testCommand: params.TEST_COMMAND ?: 'npm test',
        lintCommand: params.LINT_COMMAND ?: 'npm run lint',
        
        // Deployment Commands
        stagingDeployCommand: params.STAGING_DEPLOY_COMMAND ?: 'npm run deploy:staging',
        productionDeployCommand: params.PRODUCTION_DEPLOY_COMMAND ?: 'npm run deploy:production',
        
        // Artifact Configuration
        buildArtifacts: params.BUILD_ARTIFACTS ?: 'dist/**/*',
        testResultsPattern: params.TEST_RESULTS_PATTERN ?: 'test-results.xml',
        coveragePattern: params.COVERAGE_PATTERN ?: 'coverage/cobertura-coverage.xml',
        
        // Notification Configuration
        enableNotifications: params.ENABLE_NOTIFICATIONS != null ? params.ENABLE_NOTIFICATIONS : false,
        notificationEmail: params.NOTIFICATION_EMAIL ?: '',
        
        // NPM Publishing Configuration
        npmCredentialsId: params.NPM_CREDENTIALS_ID ?: 'npm-token',
        publishOnTag: params.PUBLISH_ON_TAG != null ? params.PUBLISH_ON_TAG : true,
        tagPattern: params.TAG_PATTERN ?: 'v\\d+\\.\\d+\\.\\d+',
        
        // Quality Gate Configuration
        sonarQubeEnv: params.SONARQUBE_ENV ?: 'SonarQube',
        sonarCommand: params.SONAR_COMMAND ?: 'npm run sonar',
        
        // Custom stages
        customStages: [:]
    ]
    
    // Merge user config with defaults (user config takes precedence)
    def finalConfig = defaultConfig + config
    
    pipeline {
        agent any
        
        tools {
            nodejs finalConfig.nodeVersion
        }
        
        environment {
            NODE_ENV = 'production'
            NPM_CONFIG_CACHE = "${WORKSPACE}/.npm"
            NPM_CONFIG_USERCONFIG = "${WORKSPACE}/.npmrc"
        }
        
        stages {
            stage('Checkout') {
                steps {
                    echo 'Checking out code...'
                    checkout scm
                }
            }
            
            stage('Setup') {
                steps {
                    echo 'Setting up Node.js environment...'
                    sh 'node --version'
                    sh 'npm --version'
                    sh 'npm cache clean --force'
                }
            }
            
            stage('Install Dependencies') {
                steps {
                    echo 'Installing dependencies...'
                    sh 'npm ci'
                }
            }
            
            stage('Lint') {
                when {
                    expression { finalConfig.enableLinting }
                }
                steps {
                    echo 'Running linting...'
                    sh "${finalConfig.lintCommand} || echo 'Linting completed with warnings'"
                }
                post {
                    always {
                        archiveArtifacts artifacts: 'lint-results.xml', allowEmptyArchive: true
                    }
                }
            }
            
            stage('Test') {
                when {
                    expression { finalConfig.enableTesting }
                }
                steps {
                    echo 'Running tests...'
                    sh finalConfig.testCommand
                }
                post {
                    always {
                        script {
                            if (finalConfig.enableTesting) {
                                publishTestResults testResultsPattern: finalConfig.testResultsPattern
                            }
                            
                            if (finalConfig.enableCoverage) {
                                publishCoverage adapters: [
                                    istanbulCoberturaAdapter(finalConfig.coveragePattern)
                                ], sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
                            }
                        }
                    }
                }
            }
            
            stage('Security Audit') {
                when {
                    expression { finalConfig.enableSecurityAudit }
                }
                steps {
                    echo 'Running security audit...'
                    sh 'npm audit --audit-level=high'
                }
            }
            
            stage('Build') {
                steps {
                    echo 'Building application...'
                    sh finalConfig.buildCommand
                }
                post {
                    success {
                        archiveArtifacts artifacts: finalConfig.buildArtifacts, allowEmptyArchive: false
                    }
                }
            }
            
            stage('Quality Gate') {
                when {
                    allOf {
                        expression { finalConfig.enableQualityGate }
                        anyOf {
                            branch finalConfig.productionBranch
                            branch finalConfig.stagingBranch
                        }
                    }
                }
                steps {
                    echo 'Running quality gate checks...'
                    script {
                        if (finalConfig.enableQualityGate) {
                            withSonarQubeEnv(finalConfig.sonarQubeEnv) {
                                sh finalConfig.sonarCommand
                            }
                        }
                    }
                }
            }
            
            stage('Custom Pre-Deploy') {
                when {
                    expression { finalConfig.customStages.preDeploy != null }
                }
                steps {
                    echo 'Running custom pre-deploy stage...'
                    script {
                        finalConfig.customStages.preDeploy()
                    }
                }
            }
            
            stage('Deploy to Staging') {
                when {
                    allOf {
                        expression { finalConfig.enableStaging }
                        branch finalConfig.stagingBranch
                    }
                }
                steps {
                    echo 'Deploying to staging environment...'
                    sh finalConfig.stagingDeployCommand
                }
            }
            
            stage('Deploy to Production') {
                when {
                    allOf {
                        expression { finalConfig.enableProduction }
                        branch finalConfig.productionBranch
                    }
                }
                steps {
                    echo 'Deploying to production environment...'
                    sh finalConfig.productionDeployCommand
                }
            }
            
            stage('Publish NPM Package') {
                when {
                    allOf {
                        expression { finalConfig.enableNPMPublish }
                        anyOf {
                            allOf {
                                expression { finalConfig.publishOnTag }
                                tag pattern: finalConfig.tagPattern, comparator: 'REGEXP'
                            }
                            allOf {
                                expression { !finalConfig.publishOnTag }
                                branch finalConfig.productionBranch
                            }
                        }
                    }
                }
                steps {
                    echo 'Publishing NPM package...'
                    withCredentials([string(credentialsId: finalConfig.npmCredentialsId, variable: 'NPM_TOKEN')]) {
                        sh '''
                            echo "//registry.npmjs.org/:_authToken=${NPM_TOKEN}" > ~/.npmrc
                            npm publish
                        '''
                    }
                }
            }
            
            stage('Custom Post-Deploy') {
                when {
                    expression { finalConfig.customStages.postDeploy != null }
                }
                steps {
                    echo 'Running custom post-deploy stage...'
                    script {
                        finalConfig.customStages.postDeploy()
                    }
                }
            }
        }
        
        post {
            always {
                echo 'Pipeline completed'
                cleanWs()
            }
            
            success {
                echo 'Pipeline succeeded!'
                script {
                    if (finalConfig.enableNotifications && finalConfig.notificationEmail) {
                        emailext (
                            subject: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                            body: "Build succeeded: ${env.BUILD_URL}",
                            to: finalConfig.notificationEmail
                        )
                    }
                }
            }
            
            failure {
                echo 'Pipeline failed!'
                script {
                    if (finalConfig.enableNotifications && finalConfig.notificationEmail) {
                        emailext (
                            subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                            body: "Build failed: ${env.BUILD_URL}",
                            to: finalConfig.notificationEmail
                        )
                    }
                }
            }
            
            unstable {
                echo 'Pipeline is unstable'
            }
            
            aborted {
                echo 'Pipeline was aborted'
            }
        }
    }
}
