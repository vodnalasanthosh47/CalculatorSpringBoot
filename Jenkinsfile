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
            mail to: 'vodnalasanthosh47@gmail.com',
                subject: 'Build passed: ${currentBuild.fullDisplayName}'
                body: 'Result: ${currentBuild.currentResult}\nDetails: ${env.BUILD_URL}'
        }
        failure {
            echo 'Build failed'
            mail to: 'vodnalasanthosh47@gmail.com',
                subject: 'Build failed: ${currentBuild.fullDisplayName}'
                body: 'Result: ${currentBuild.currentResult}\nDetails: ${env.BUILD_URL}'
        }
    }

}