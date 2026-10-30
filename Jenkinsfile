pipeline {
    agent any

    environment {
        IMAGE_NAME = 'vodnalasanthosh/calculator-api'
    }

    stages {
        stage('Build') {
            steps {
                echo 'Building...'
                sh 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                echo 'Testing...'
                sh 'mvn test'
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building docker image'
                sh "docker build -t vodnalasanthosh/calculator-api:${BUILD_NUMBER} -t vodnalasanthosh/calculator-api:latest ."
            }
        }

        stage('Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                    usernameVariable: 'DOCKERHUB_USERNAME',
                                                    passwordVariable: 'DOCKERHUB_TOKEN')])
                {
                    sh '''
                        echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin
                        docker push "$IMAGE_NAME:$BUILD_NUMBER"
                        docker push "$IMAGE_NAME:latest"
                    '''
                }
            }
            post {
                always {
                    sh 'docker logout'
                }
            }
        }

        stage('Deploy') {
            environment {
                IMAGE_TAG="$BUILD_NUMBER"
            }
            steps {
                sh 'docker compose up -d --remove-orphans --wait --wait-timeout 120'
                sh 'docker compose ps'

                sh 'curl -fsS http://127.0.0.1:9090/actuator/health'
            }
            post {
                failure {
                    sh 'docker compose ps; docker compose logs --tail=50 || true'
                }
            }
        }
    }

    post {
        success {
            echo 'Build passed'
            mail to: 'vodnalasanthosh47@gmail.com',
                subject: "Build PASSED: ${currentBuild.fullDisplayName}",
                body: "Result: ${currentBuild.currentResult}\nDetails: ${env.BUILD_URL}"
        }
        failure {
            echo 'Build failed'
            mail to: 'vodnalasanthosh47@gmail.com',
                subject: "Build FAILED: ${currentBuild.fullDisplayName}",
                body: "Result: ${currentBuild.currentResult}\nDetails: ${env.BUILD_URL}"
        }
    }

}