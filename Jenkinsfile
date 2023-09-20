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
            sh "docker login -u dockerhub"
            sh "docker push dezin7/calculator"
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
      body: "Your build is completed, please check: ${env.BUILD_URL}"
    }
  }