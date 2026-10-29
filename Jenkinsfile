pipeline {
    agent any

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
    }

    post {
        success {
            echo 'Build passed'
        }
        failure {
            echo 'Build failed'
        }
    }

}