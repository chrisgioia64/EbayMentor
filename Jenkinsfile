pipeline {
  agent any
  stages {
  stage('Smoke') {
      steps {
          bat "mvn test -P smoke -D locale=us -D browser=firefox -D product_numbers=1"
      }
      post {
          always {
//             step([$class: 'Publisher', reportFilenamePattern: '**/target/smoke/testng-results.xml'])
            publishHTML (target: [
                  allowMissing: false,
                  alwaysLinkToLastBuild: false,
                  keepAll: true,
                  reportDir: 'target/smoke',
                  reportFiles: 'emailable-report.html',
                  reportName: "Smoke Test"
                ])
          }

      }
    }
  stage('Product 1 -- Single Images') {
    parallel {
//         stage('US Locale') {
//             steps {
//                 sh "mvn test -P jenkins -D locale=us -D browser=firefox"
//             }
//             post {
//                 always {
// //                 step([$class: 'Publisher', reportFilenamePattern: '**/target/smoke/testng-results.xml'])
//             publishHTML (target: [
//                   allowMissing: false,
//                   alwaysLinkToLastBuild: false,
//                   keepAll: true,
//                   reportDir: 'target/locale/us',
//                   reportFiles: 'emailable-report.html',
//                   reportName: "US Locale"
//                 ])
//
//                 }
//             }
//         }

        stage('UK Locale') {
            steps {
                bat "mvn test -P jenkins -D locale=uk -D browser=firefox"
            }
            post {
                always {
//                 step([$class: 'Publisher', reportFilenamePattern: '**/target/locale/uk/1/testng-results.xml'])
            publishHTML (target: [
                  allowMissing: false,
                  alwaysLinkToLastBuild: false,
                  keepAll: true,
                  reportDir: 'target/locale/uk',
                  reportFiles: 'emailable-report.html',
                  reportName: "UK Locale"
                ])
                }
            }
        }
//         stage('IT Locale') {
//             steps {
//                 sh "mvn test -P jenkins -D locale=it -D browser=firefox"
//             }
//             post {
//                 always {
// //                 step([$class: 'Publisher', reportFilenamePattern: '**/target/locale/it/1/testng-results.xml'])
//             publishHTML (target: [
//                   allowMissing: false,
//                   alwaysLinkToLastBuild: false,
//                   keepAll: true,
//                   reportDir: 'target/locale/it',
//                   reportFiles: 'emailable-report.html',
//                   reportName: "IT Locale"
//                 ])
//                 }
//             }
//         }
//         stage('ES Locale') {
//             steps {
//                 sh "mvn test -P jenkins -D locale=es -D browser=firefox"
//             }
//             post {
//                 always {
// //                 step([$class: 'Publisher', reportFilenamePattern: '**/target/locale/es/1/testng-results.xml'])
//             publishHTML (target: [
//                   allowMissing: false,
//                   alwaysLinkToLastBuild: false,
//                   keepAll: true,
//                   reportDir: 'target/locale/es',
//                   reportFiles: 'emailable-report.html',
//                   reportName: "ES Locale"
//                 ])
//                 }
//             }
//         }
    }
            post {
                  always {
                  step([$class: 'Publisher', reportFilenamePattern: '**/target/locale/**/testng-results.xml'])
                  }
              }
  }
}
}