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

        stage("Deploy to staging") { steps {
          withKubeConfig([credentialsId: 'kubectl',
                          caCertificate: 'LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUMvakNDQWVhZ0F3SUJBZ0lCQURBTkJna3Foa2lHOXcwQkFRc0ZBREFWTVJNd0VRWURWUVFERXdwcmRXSmwKY201bGRHVnpNQjRYRFRJek1Ea3lOREEyTlRreE1Gb1hEVE16TURreU1UQTJOVGt4TUZvd0ZURVRNQkVHQTFVRQpBeE1LYTNWaVpYSnVaWFJsY3pDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUJBTnZiClFudDRzcFFWVkszakJIemg4aVpLeGhoWWRCOHBvNjZSYU5DSFhCZU5nc2RRK25iWmNHS1ZSN3ZnSUhrMnZvK1MKOGdMazhLRWRIZXo1N3MwdFhoTmdKRGx1NUQ2aHhVQzhIWjM2bnNDQkk2TFhCa29pRjNraGYrVmNNQ0R3RmFLUgpzZkhZSEZvSHlLRkRVcDZvMTdmMWNYSVNxWkVhcGNJVU9kellDNGt3SFA5SnpYUnp2QkUwbXBWQkVkYjl0VHJ3Cm04NUZFTkUwUFhNTDNnSEFuRllXYXR0SWV6MmpEcmdsdmJ3NWR4ZllUZXlVSytPa21EbU9GTmVMZ3VPL3h2VFIKOFYwcVIyZ3pXQ3FoaXltMHRwN3h6TExoR0NQMGhsZjI0MGFKc29CQ3B2QkM4dHZ5UEQrWmJLZ1RFczFCdlZPYQpSQXczTFJiOThhK3JzUk9HMUJzQ0F3RUFBYU5aTUZjd0RnWURWUjBQQVFIL0JBUURBZ0trTUE4R0ExVWRFd0VCCi93UUZNQU1CQWY4d0hRWURWUjBPQkJZRUZPcWpzZjQydHZOVVlqRmRDaTQzT0VObENqM1FNQlVHQTFVZEVRUU8KTUF5Q0NtdDFZbVZ5Ym1WMFpYTXdEUVlKS29aSWh2Y05BUUVMQlFBRGdnRUJBRnFXTFl3empWMGx6Njl1SHh0KwpYNFFZRDNNOWNKd1g4aXlVZ3ZiWm9FUU14Zk1aYlM2K1ZXTVBNMUlIUEFmS3lUcHRhVE1iQk1aN1lKOEtFVnowCmhzZjdxOGxCMG5kZlc2eEZuR1hTRlkyT2pFQnZmWURINHZhYlZ4VWthaEpBRm83NHVCb0lBUWFwS0RSNFdtb04KRVBtdGVneVJwYkdaT3I4SlJpaVMwWkpMdVBORjNndTd6S3c2aTYzZ1RNR0tjb0ZXT21YM1dXUWpKUk1MNTFrRworc1FXM3FYSmxydUtrdXkwZ1RuVVVZVDRKYWJIU3lJRTQzS0NXdjhpNklQZnVpdzl2emNLZTYyYkMwbDJTbnE2CnVvOTZCWGlybmRUMnZEdHhZckQ2U0JVRmZOUmhhb3RzTWQvbTlibmF5dkZuTW8rc3VRN0lQTXB1bkFqTTJxUWUKbjRRPQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==',
                          contextName: 'docker-desktop',
                          serverUrl: 'https://127.0.0.1:6443',
                          clusterName: 'docker-desktop'
                          ]) {
            sh 'curl -LO "https://storage.googleapis.com/kubernetes-release/release/v1.25.4/bin/linux/amd64/kubectl"'
            sh 'chmod u+x ./kubectl'                
            sh './kubectl config use-context docker-desktop'
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
