pipeline {
  agent {
    docker {
      image 'evoila/concourse-mvn-resource'
    }

  }
  stages {
    stage('Build') {
      steps {
        git(credentialsId: 'patsys', url: 'https://github.com/evoila/osb-redis', branch: 'develop', poll: true)
      }
    }
  }
}