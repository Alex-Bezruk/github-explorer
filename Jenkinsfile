pipeline {
    agent {
        label 'github-explorer'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    try {
                        deleteDir()

                        checkout([$class: 'GitSCM',
                                  branches: [[name: 'master']],
                                  doGenerateSubmoduleConfigurations: false,
                                  extensions: [],
                                  submoduleCfg: [],
                                  userRemoteConfigs: [[url: 'https://github.com/Alex-Bezruk/github-explorer']]])
                    } catch (Exception e) {
                        echo "Failed to checkout the repository: ${e.message}"
                        error("Checkout failed")
                    }
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    try {
                        sh './gradlew clean test'
                        echo "Tests executed successfully"
                    } catch (Exception e) {
                        echo "Failed to run tests: ${e.message}"
                        error("Test execution failed")
                    } finally {
                        sh 'kill $(pgrep -f "quarkusDev") || true'
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    try {
                        def dockerImage = docker.build("github-explorer:latest", "-f .")
                        if (dockerImage) {
                            echo "Docker image built successfully"
                        } else {
                            error("Failed to build Docker image")
                        }
                    } catch (Exception e) {
                        echo "Failed to build Docker image: ${e.message}"
                        error("Docker image build failed")
                    }
                }
            }
        }

        stage('Push to ECR') {
            steps {
                script {
                    withCredentials([[
                        $class: 'AmazonWebServicesCredentialsBinding',
                        accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                        secretKeyVariable: 'AWS_SECRET_ACCESS_KEY',
                        credentialsId: 'AWS_ACCOUNT'
                    ]]) {
                        try {
                            sh "aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}"
                        } catch (Exception e) {
                            echo "Failed to login to ECR: ${e.message}"
                            error("ECR login failed")
                        }

                        docker.withRegistry("${ECR_REGISTRY}", 'ecr') {
                            def dockerImage = docker.image("${ECR_REGISTRY}/${ECR_REPOSITORY}:latest")
                            try {
                                dockerImage.push()
                                echo "Docker image pushed successfully to ECR"
                            } catch (Exception e) {
                                echo "Failed to push Docker image to ECR: ${e.message}"
                                error("ECR push failed")
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
