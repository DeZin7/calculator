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
                          caCertificate: 'MIIC/jCCAeagAwIBAgIBADANBgkqhkiG9w0BAQsFADAVMRMwEQYDVQQDEwprdWJlcm5ldGVzMB4XDTIzMDkyNDA2NTkxMFoXDTMzMDkyMTA2NTkxMFowFTETMBEGA1UEAxMKa3ViZXJuZXRlczCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANvbQnt4spQVVK3jBHzh8iZKxhhYdB8po66RaNCHXBeNgsdQ+nbZcGKVR7vgIHk2vo+S8gLk8KEdHez57s0tXhNgJDlu5D6hxUC8HZ36nsCBI6LXBkoiF3khf+VcMCDwFaKRsfHYHFoHyKFDUp6o17f1cXISqZEapcIUOdzYC4kwHP9JzXRzvBE0mpVBEdb9tTrwm85FENE0PXML3gHAnFYWattIez2jDrglvbw5dxfYTeyUK+OkmDmOFNeLguO/xvTR8V0qR2gzWCqhiym0tp7xzLLhGCP0hlf240aJsoBCpvBC8tvyPD+ZbKgTEs1BvVOaRAw3LRb98a+rsROG1BsCAwEAAaNZMFcwDgYDVR0PAQH/BAQDAgKkMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFOqjsf42tvNUYjFdCi43OENlCj3QMBUGA1UdEQQOMAyCCmt1YmVybmV0ZXMwDQYJKoZIhvcNAQELBQADggEBAFqWLYwzjV0lz69uHxt+X4QYD3M9cJwX8iyUgvbZoEQMxfMZbS6+VWMPM1IHPAfKyTptaTMbBMZ7YJ8KEVz0hsf7q8lB0ndfW6xFnGXSFY2OjEBvfYDH4vabVxUkahJAFo74uBoIAQapKDR4WmoNEPmtegyRpbGZOr8JRiiS0ZJLuPNF3gu7zKw6i63gTMGKcoFWOmX3WWQjJRML51kG+sQW3qXJlruKkuy0gTnUUYT4JabHSyIE43KCWv8i6IPfuiw9vzcKe62bC0l2Snq6uo96BXirndT2vDtxYrD6SBUFfNRhaotsMd/m9bnayvFnMo+suQ7IPMpunAjM2qQen4Q=',
                          contextName: 'docker-desktop',
                          serverUrl: 'https://localhost:6443',
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
