pipeline {
  agent {
    docker {
      image 'maven:3-jdk-8'
      args '-v /data/jenkins/m2:/.m2 -v /data/jenkins/gpg:/.gnupg -v /data/jenkins/docker:/.docker'
    }
    
  }
  stages {
    stage('Clean') {
      steps {
        sh '''cd reactions-root
mvn "-Duser.home=/" "-Djenkins=true" clean'''
      }
    }
    stage('Build') {
      steps {
        sh '''cd reactions-root
mvn "-Duser.home=/" "-Djenkins=true" install'''
      }
    }
    stage('Deploy') {
      steps {
        sh '''cd reactions-root
mvn "-Duser.home=/" -DskipTests=true "-Djenkins=true" deploy'''
      }
    }
  }
}