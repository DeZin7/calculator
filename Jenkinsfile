pipeline {
  agent any
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

        stage('Static code analysis') {
          steps {
            sh './gradlew checkstyleMain'
          }
        }
        
        stage("Package") {
          steps {
            sh "./gradlew build"
          }
        }

        stage("Docker build") {
          steps {
            sh "docker build -t dezin7/calculator ."
          }
        }

        stage("Docker push") {
          steps {
            sh "docker login"
            sh "docker push dezin7/calculator"
          }
        }

        stage("Deploy to staging") {
          steps {
            sh "docker run -d --rm -p 8765:8080 --name calculator1 dezin7/calculator"
          }
        }

        stage("Acceptance test") {
          steps {
            sleep 60
            sh "chmod +x acceptance_test.sh && ./acceptance_test.sh"
          }
        }
      }
    }

  }
}
  post {
    always {
         sh "docker stop calculator"
    }
  }