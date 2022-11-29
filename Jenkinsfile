import groovy.json.JsonSlurperClassic
def jsonParse(def json) {
    new groovy.json.JsonSlurperClassic().parseText(json)

    
}
pipeline {
    agent any
    stages {
        //if(env.BRANCH_NAME == 'master'){    
            stage("Paso 1: Compliar"){                
                steps {
                    script {
                    sh "echo $env.BRANCH_NAME"
                    sh "echo 'Compile Code!'"
                    // Run Maven on a Unix agent.
                    sh "./mvnw clean compile -e"
                    }
                }
            }
            stage("Paso 2: Testear"){
                steps {
                    script {
                    sh "echo 'Test Code!'"
                    // Run Maven on a Unix agent.
                    sh "./mvnw clean test -e"
                    }
                }
            }
            stage("Paso 3: Build .Jar"){
                steps {
                    script {
                    sh "echo 'Build .Jar!'"
                    // Run Maven on a Unix agent.
                    sh "./mvnw clean package -e"
                    }
                }
            }
            stage("Paso 4: Análisis SonarQube"){
                steps {
                    withSonarQubeEnv('sonarqube') {
                        sh "echo 'Calling sonar Service in another docker container!'"
                        // Run Maven on a Unix agent to execute Sonar.
                        sh './mvnw clean verify sonar:sonar -Dsonar.projectKey=ms-iclab-grupo2 -Dsonar.projectName=ms-iclab-grupo2 -Dsonar.java.binaries=build'
                    }
                }
                post {
                    //record the test results and archive the jar file.
                    success {
                        sh "echo 'Analisis exitoso'"
                        //archiveArtifacts artifacts:'build/*.jar'
                    }
                }
            }
            stage("Paso 5: Merge"){
                steps {
                    sh "echo 'git flow $branch_type finish'"
                }
                post {
                    //record the test results and archive the jar file.
                    success {
                        sh "echo 'Merge exitoso'"
                        //archiveArtifacts artifacts:'build/*.jar'
                    }
                }
            }
        //}      
    }
    post {
        always {
            sh "echo 'fase always executed post'"
        }
        success {
            sh "echo 'fase success'"
        }
        failure {
            sh "echo 'fase failure'"
        }
    }
}