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
                          caCertificate: 'MIIC/jCCAeagAwIBAgIBADANBgkqhkiG9w0BAQsFADAVMRMwEQYDVQQDEwprdWJlcm5ldGVzMB4XDTIzMTAyMDA1NDYxN1oXDTMzMTAxNzA1NDYxN1owFTETMBEGA1UEAxMKa3ViZXJuZXRlczCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKpK3Q6jccyDUeN8aAsrZz/ToE02nvcrpJPiNYDeVo5c28kkzVjwUfkhufkHKqW1TpOBO7ArpHZF4NbPhJgIDih2GQU43QUVfgwhPSOclTNTDlwm12AXa9ZDe/PKkcF3TeBfjX3OVQICn68D4/nOKcsJn5LdX91b/G2qyJEQAkFhcpgBve9rI0PgKaX/4Tgl5SbPj8wariLa934Mkq5dnuup7yDArj7ZSRvEYV6tkdmw6C+CslFZhDL6NR2u8iCsfNdIL1Xn7pFLR5K5XCptAhNOp3UDmN33EVZGP6pUtmTlQAYDUPiDv9dt+WSx99XBEXYjAU7deKht/MByZZHhhXMCAwEAAaNZMFcwDgYDVR0PAQH/BAQDAgKkMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFMdEHs63xRzDra3eF4/utbm8RolaMBUGA1UdEQQOMAyCCmt1YmVybmV0ZXMwDQYJKoZIhvcNAQELBQADggEBAA9E674hFl5XoOK30KOGz4HqtiRZiJ24zf+HyoYVwLr8j03weK5GHNV8aCV3b8d0alutf3cDAfcMXSxzLWJX5Y8ocXElK+YSKe6IcgT98QjtxqgWE2aD88jNp6Ir5QKlm3m3Mj2ImWvHDjU40PPdzlhHkac+3uxLQxGzusNWPINCPcGKgkzFH6P8aW3GdRhjmh8DsCpy37Vqg1dTgy9CJElS43nf6NSYEGqBI7CNdxt62+x1sCaqEDmESQ8LUPT6TLYNoft+ykPMWoK90n0ReD9iu/wKwyJvlYMhw7lu/F/EbauT+HWOvzHniGqRTdsGUJ0MteiFiip62o51eFB2E+I=',
                          contextName: 'minikube',
                          serverUrl: 'https://127.0.0.1:6443',
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
