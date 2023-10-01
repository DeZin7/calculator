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
          withKubeConfig([credentialsId: 'kubectl',
                          caCertificate: 'LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURCVENDQWUyZ0F3SUJBZ0lJRC9kZFp3aXkyNk13RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB5TXpFd01ERXhPVFEyTXpkYUZ3MHpNekE1TWpneE9UVXhNemRhTUJVeApFekFSQmdOVkJBTVRDbXQxWW1WeWJtVjBaWE13Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLCkFvSUJBUURBWTBtSnRTbGJzb3FNTk12SWplYTN4Yk0zMENERzRNQXJRdGhmVll2dDVTdXFRY01HMGNmTitTVHYKZzhXaFNaZFY5N2lVZDVwZ2lkZkxPR2pxTUdXcmdZRTR0cFJTakFONTZRL3J3ZE9PenYvVkhHVFZSMmljbHB1ego0VklIZzIwc3d2V0xUcXBud2hGZDFuRTl4TWZMVnFGd28zVmZ2K21vdUxtN3llaU9ueDltQmxTZmtUdkhES1pUCkNKL3c5SnZkSy93dXUxcjYxclluQnVKQ3hKSWRiRzQ0dUQ2TnVMSU5TbmI0ZkkxVStVRHNWanRXNG1WSTZzQ2QKaVJ6Tjg0ZFRNSG5WVmhUdmNZOWlndHJoSUVUTFlTeXpBZnJxWXJ5ZGx3T2ZpU1gzak04YTI2eUt4MzlHTGNaQwptYUxIQXB5N2dVSWZYS1c3SHVINTFDNnFXSWt6QWdNQkFBR2pXVEJYTUE0R0ExVWREd0VCL3dRRUF3SUNwREFQCkJnTlZIUk1CQWY4RUJUQURBUUgvTUIwR0ExVWREZ1FXQkJTS2kyalJLQWFvM3FIeTVaVHFNZlUxNUxsNWVUQVYKQmdOVkhSRUVEakFNZ2dwcmRXSmxjbTVsZEdWek1BMEdDU3FHU0liM0RRRUJDd1VBQTRJQkFRQjU0RHRBalhTZgpVVjEwaE1Bc3VqTGZ6bU9tUCtJYU9TbFpyM0N4eVNyUk9tK2Q4TlF2WExLamlqYzR5c0xMRGsxTVVzc21yQkJtCk5lN1FnRTBFNmkvbUcxMld3MWVUMG51LzNMQ090WjZMTmp2TEJCRzFnYmNUcXYwdTFPTm9Hbm90TCtCSUZtN1AKVWRqUTZtVjZyU1N1R2QwVnVnNVlLVU5nQUZOejNFYWtFTHdoZWxaNTRSWm5aeCtsdXliZXlxcm9DNHYzRFgxbgo4NTFZUHpYWXIzUTR5eHBnbmpwV0xyRURXa0pFaEhGV2tJOEUyUzcvQnRGSG9ROW5LOHRWZTZ6dG0xY3p5Vk9BCm5qRkMwRURTQUhndTVPQWJoSXVpM09OVTIveTJRUXh4U2xOR2l6RDZrYm80YjdyMUdLM1ZVM1RpYnR0WFpvRHcKVjhGSGJHRkc3cE1NCi0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K',
                          contextName: 'arn:aws:eks:us-west-2:846825716254:cluster/staging',
                          serverUrl: 'https://5CFE154153C036FD5FAFDCB91C628CAC.gr7.us-west-2.eks.amazonaws.com',
                          clusterName: 'arn:aws:eks:us-west-2:846825716254:cluster/staging'
                          ]) {
            sh "kubectl use-context arn:aws:eks:us-west-2:846825716254:cluster/staging"
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
