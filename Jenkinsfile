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
                          caCertificate: 'MIIDBjCCAe6gAwIBAgIBATANBgkqhkiG9w0BAQsFADAVMRMwEQYDVQQDEwptaW5pa3ViZUNBMB4XDTIzMTAxOTA3MjExMVoXDTMzMTAxNzA3MjExMVowFTETMBEGA1UEAxMKbWluaWt1YmVDQTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMqaWBGnkLCpZXmFyxORKoOsAGwuD7rsmALXU7bj5GVw0c5j2L2AZMwt+SXvJezEuRJjL6xUgwJRQxQ7Zqh6XzJtaN/FB8yGnX3rCFH3PC09jdZhZQjZ7cFyaa7WBDvyU8l2dkph0R79UtsK26iLTKgKswAOpWf7d/b5SBxvY25djJicYEuZdOv5b0cukEKKNTWXt8XiF+VgBfiYS4IpNF5fLYYhwE9Rb4eoN4hbIpHr01gyyiH6fCmsooviyl4wkqENSCcIsxbQY/p7O4VBzPf4SZkLewKdwKPGVCB6kQcp16S3t73Tlppavvwt9fLtzFocJIjBb4OXN3kFnFz1r3ECAwEAAaNhMF8wDgYDVR0PAQH/BAQDAgKkMB0GA1UdJQQWMBQGCCsGAQUFBwMCBggrBgEFBQcDATAPBgNVHRMBAf8EBTADAQH/MB0GA1UdDgQWBBQ//iDW2cZYYUxgcaYfcS361r3vPzANBgkqhkiG9w0BAQsFAAOCAQEAt3VubutGFMe2ttZcewmfXH9C/07DnicuvR+x3BQoXpSdmLqKmKXX/M9V521QIBc/V4MROUGH6aFY+MlHWedvGz9EueTf1TSwC4RCo2yLXS7xPGQXVhTb9Dw1Vzn3Qb8UIJ2KalhSA+FjygOtm5HHRwE/bykhf0pgXE5RQLK2yrcgToVfPuEJHIiyq+WpSAHewOZCt3HMp+LEwy+UAplLI37xQM/GL6fz1tvWJOVz7oC0KdoP9px66UxPI8uO7LxsbOqZUMfPzFeQk5JmvJOXE2Kv7aw8cwtAMnIb8HW+Cj5JWWS9Qws/44iKXtjoVFjKeHtw6nDBjuvvvkailyn87w==',
                          contextName: 'minikube',
                          serverUrl: ' https://127.0.0.1:52741',
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
