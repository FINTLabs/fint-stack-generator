pipeline {
    agent { label 'docker' }
    stages {
        stage('Build') {
            steps {
                withDockerRegistry([credentialsId: 'dtr-fintlabs-no', url: 'https://dtr.fintlabs.no']) {
                    sh "docker build --tag ${GIT_COMMIT} ."
                }
            }
        }
        stage('Publish') {
            when { branch 'master' }
            steps {
                withDockerRegistry([credentialsId: 'dtr-fintlabs-no', url: 'https://dtr.fintlabs.no']) {
                    sh "docker tag ${GIT_COMMIT} dtr.fintlabs.no/beta/stack-generator:latest"
                    sh "docker push 'dtr.fintlabs.no/beta/stack-generator:latest'"
                }
                withDockerServer([credentialsId: "ucp-fintlabs-jenkins-bundle", uri: "tcp://ucp.fintlabs.no:443"]) {
                    sh "docker service update stack-generator_stacks --image dtr.fintlabs.no/beta/stack-generator:latest --detach=false"
                }
            }
        }
        stage('Publish PR') {
            when { changeRequest() }
            steps {
                withDockerRegistry([credentialsId: 'dtr-fintlabs-no', url: 'https://dtr.fintlabs.no']) {
                    sh "docker tag ${GIT_COMMIT} dtr.fintlabs.no/beta/stack-generator:${BRANCH_NAME}"
                    sh "docker push 'dtr.fintlabs.no/beta/stack-generator:${BRANCH_NAME}'"
                }
            }
        }
        stage('Publish Version') {
            when {
                tag pattern: "v\\d+\\.\\d+\\.\\d+(-\\w+-\\d+)?", comparator: "REGEXP"
            }
            steps {
                script {
                    VERSION = TAG_NAME[1..-1]
                }
                sh "docker tag ${GIT_COMMIT} dtr.fintlabs.no/beta/stack-generator:${VERSION}"
                withDockerRegistry([credentialsId: 'dtr-fintlabs-no', url: 'https://dtr.fintlabs.no']) {
                    sh "docker push dtr.fintlabs.no/beta/stack-generator:${VERSION}"
                }
            }
        }
    }
}
