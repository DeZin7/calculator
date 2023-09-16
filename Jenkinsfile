pipeline {
  agent any
  triggers {
    pollSCM ('* * * * *')
  }
  stages {
    stage('Checkout') {
      parallel {
        stage('Checkout') {
          steps {
            git(url: 'https://github.com/DeZin7/calculator.git', branch: 'main')
          }
        }

        stage('Compile') {
          steps {
            sh './gradlew compileJava'
          }
        }

        stage('Unit test') {
          steps {
            sh './gradlew test'
          }
        }

        stage('Code coverage') {
          steps {
            sh './gradlew jacocoTestReport'
            publishHTML (target: [
              reportDir: 'build/reports/jacoco/test/html',
              reportFiles: 'index.html',
              reportName: "JaCoCo Report"
            ])
            sh './gradlew jacocoTestCoverageVerification'
          }
        }

        stage('Static code analysis') {
          steps {
            sh './gradlew checkstyleMain'
          }
        }
        
      }
    }

  }
}
  post {
    always {
      mail to: 'marcusandre77@icloud.com',
      subject: "Completed Pipeline: ${currentBuild.fullDisplayName}",
      body: "Your build completed, please check: ${env.BUILD_URL}"
    }
  }