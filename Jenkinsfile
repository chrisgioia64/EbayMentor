pipeline {
  agent any
  stages {
  stage('Smoke') {
      steps {
          bat "mvn test -P smoke -D locale=us -D browser=firefox -D product_numbers=1"
      }
      post {
          always {
            step([$class: 'Publisher', reportFilenamePattern: '**/target/smoke/testng-results.xml'])
          }
      }
    }
  stage('Product 1 -- Single Images') {
    parallel {
        stage('US Locale') {
            steps {
                bat "mvn test -P jenkins -D locale=us -D browser=firefox -D product_numbers=1"
            }
            post {
                always {
                step([$class: 'Publisher', reportFilenamePattern: '**/testng-results.xml'])
                }
            }
        }

        stage('UK Locale') {
            steps {
                bat "mvn test -P jenkins -D locale=uk -D browser=firefox -D product_numbers=1"
            }
            post {
                always {
                step([$class: 'Publisher', reportFilenamePattern: '**/target/locale/uk/1/testng-results.xml'])
                }
            }
        }
        stage('IT Locale') {
            steps {
                bat "mvn test -P jenkins -D locale=it -D browser=firefox -D product_numbers=1"
            }
            post {
                always {
                step([$class: 'Publisher', reportFilenamePattern: '**/target/locale/it/1/testng-results.xml'])
                }
            }
        }
        stage('ES Locale') {
            steps {
                bat "mvn test -P jenkins -D locale=es -D browser=firefox -D product_numbers=1"
            }
            post {
                always {
                step([$class: 'Publisher', reportFilenamePattern: '**/target/locale/es/1/testng-results.xml'])
                }
            }
        }
    }
  }
}
}