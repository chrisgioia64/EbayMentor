pipeline {
  agent any
  stages {
  stage('Smoke') {
      steps {
          bat "mvn test -P smoke -D locale=us -D browser=firefox -D product_numbers=1"
      }
    }
  stage('Product 1 -- Single Images') {
    parallel {
        stage('US Locale') {
            steps {
                bat "mvn test -P jenkins -D locale=us -D browser=firefox -D product_numbers=1"
            }
        }
        stage('UK Locale') {
            steps {
                bat "mvn test -P jenkins -D locale=uk -D browser=firefox -D product_numbers=1"
            }
        }
        stage('IT Locale') {
            steps {
                bat "mvn test -P jenkins -D locale=it -D browser=firefox -D product_numbers=1"
            }
        }
        stage('ES Locale') {
            steps {
                bat "mvn test -P jenkins -D locale=es -D browser=firefox -D product_numbers=1"
            }
        }
    }
  }
}
}