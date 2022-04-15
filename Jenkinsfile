pipeline {
  agent any
  stages {
  stage('Smoke') {
      steps {
          mvn test -P smoke -D locale=us -D browser=firefox -D product_numbers=1
      }
    }
  stage('Product 1 -- Single Images') {
    parallel {
        stage('US Locale') {
            steps {
                mvn test -P jenkins -D locale=us -D browser=firefox product_numbers=1
            }
        }
        stage('UK Locale') {
            steps {
                mvn test -P jenkins -D locale=uk -D browser=firefox product_numbers=1
            }
        }
        stage('IT Locale') {
            steps {
                mvn test -P jenkins -D locale=it -D browser=firefox product_numbers=1
            }
        }
        stage('ES Locale') {
            steps {
                mvn test -P jenkins -D locale=es -D browser=firefox product_numbers=1
            }
        }
    }
  }

}