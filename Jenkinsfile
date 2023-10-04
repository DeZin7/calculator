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
            sh "docker login"
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

        stage("Deploy to staging") { steps {
          withKubeConfig([credentialsId: 'kubectl',
                          caCertificate: 'MIIDBjCCAe6gAwIBAgIBATANBgkqhkiG9w0BAQsFADAVMRMwEQYDVQQDEwptaW5pa3ViZUNBMB4XDTIzMDUyMzIxMDU1MloXDTMzMDUyMTIxMDU1MlowFTETMBEGA1UEAxMKbWluaWt1YmVDQTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAM91hCLoLWoFHH4abesrWM71RGGItpa/jRp+GDAOsd+IXIdOJux/a1xL5pTm5XTzhptxbIW84HyiPYDYlSQf8dwjTojjxdygK87OTwXOr+0qMmMJxgVOkLBFzgaE3tcyoyQVGLTAIMIdwJtbItRCEVzZpFA4DTlF9rUEceDpOJDz2Hw/IYXZH82RoJ2oRd5xocPRr95zwkHR+LC/eHFy8oO/nTK7Usqjrd/btvfd/wxzJIWg/axndFQB6ZC0vlQOozMyJCjhRfym4ZAPg5Ae1IyDVvEwOxU/U3KetKuRJaKSjr//Lxwq2CVXxc6/e4MtKnY3jwrrvidW5BDKYwskkFsCAwEAAaNhMF8wDgYDVR0PAQH/BAQDAgKkMB0GA1UdJQQWMBQGCCsGAQUFBwMCBggrBgEFBQcDATAPBgNVHRMBAf8EBTADAQH/MB0GA1UdDgQWBBTYNQtnB8+RIj0ikKeT/R5kB65kzjANBgkqhkiG9w0BAQsFAAOCAQEAqvRZO0kzP7Yxq7+BZbrwWj7iVaA8ZgCYnHSfC5U684AqDFzJREO0wAdZWsZ95VIR8013L3RUyAMSfgOnjEu+J14wF2CqYFsDYTb+JKUOOdRYFQbtdAYx8bEJR8qdflJbE8psinh2sregANZ+zFKSXHYk4B6LWhrMt1OTxZynLRu/mg4MnsGcuMexEhRhfILyYoPwOULJ0sqwSqv4QHIC7iGz/VlLX6IiX3+UbuYEdrOmnj7uaM5tcIfXVd0A7CwylM755lNuzBwM1V9mMtI6ZMRe5zl0G5PzfrM7fqIHGHA0kN/r2uNcR6EhrKAElwMQuHvSkr8SBr/6bT5dDmsLAg==',
                          contextName: 'minikube',
                          serverUrl: 'https://192.168.49.2:51176',
                          clusterName: 'minikube'
                          ]) {
            sh 'curl -LO "https://storage.googleapis.com/kubernetes-release/release/v1.25.4/bin/linux/amd64/kubectl"'
            sh 'chmod u+x ./kubectl'                
            sh './kubectl config use-context minikube'
            sh './kubectl apply -f hazelcast.yaml'
            sh './kubectl apply -f deployment.yaml'
            sh './kubectl apply -f service.yaml'
          
        }
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
            sh "kubectl config use-context arn:aws:eks:us-west-2:846825716254:cluster/production"
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
  post {
    always {
         sh "docker stop calculator"
    }
  }
}
