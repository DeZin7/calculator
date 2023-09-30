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
        
        stage("Build") {
          steps {
            sh "./gradlew build"
          }
        }

        stage("Docker build") {
          steps {
            sh "docker build -t dezin7/calculator:${BUILD_TIMESTAMP} ."
          }
        }

        stage("Docker push") {
          steps {
            sh "docker login"
            sh "docker push dezin7/calculator:${BUILD_TIMESTAMP}"
          }
        }

        stage("Update Version") {
          steps {
            sh "sed -i 's/{{VERSION}}/${BUILD_TIMESTAMP}/g' deployment.yaml"
          }
        }

        stage("Deploy to staging") {
          steps {
            sh "kubectl use-context arn:aws:eks:us-west-1:846825716254:cluster/staging"
            sh "kubectl apply -f hazelcast.yaml"
            sh "kubectl apply -f deployment.yaml"
            sh "kubectl apply -f service.yaml"
          }
        }

        stage("Acceptance test") {
          steps {
            sleep 60
            sh "chmod +x acceptance_test.sh && ./acceptance_test.sh"
          }
        }

        // Performance test stages

        stage("Release") {
          steps {
            sh "kubectl config use-context arn:aws:eks:us-west-1:846825716254:cluster/production"
            sh "kubectl apply -f hazelcast.yaml"
            sh "kubectl apply -f deployment.yaml"
            sh "kubectl apply -f service.yaml"
          }
        }

        stage("Smoke test") {
          steps {
            sleep 60
            sh "chmod +x smoke-test.sh && ./smoke-test.sh"
          }
        }
      }
    }

  }
  node {
  stage('Apply Kubernetes files') {
    withKubeConfig([credentialsId: 'Marcus Andre', serverUrl: 'https://api.k8s.my-company.com']) {
      sh 'kubectl apply -f my-kubernetes-directory'
    }
  }
}
  post {
    always {
         sh "docker stop calculator"
    }
  }
}
