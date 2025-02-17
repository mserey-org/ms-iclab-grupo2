import groovy.json.JsonSlurperClassic

def jsonParse(def json) {
    new groovy.json.JsonSlurperClassic().parseText(json)
}

pipeline {
    agent any
    environment {
        channel='C04B17VE0JH'
        NEXUS_PASSWORD = credentials('nexus-password')
        BRANCH = "${env.BRANCH_NAME.split("/")[1]}"
        VERSION = "${env.BRANCH_NAME.split("/")[1]}"        
    }
    stages {    
        stage("CI 1: Compilar"){
        environment { STAGE='CI 1: Compilar' }
            steps {
                script{
                    sh "echo 'Compile Code!!'"
                    sh "./mvnw clean compile -e"    
                }
            }
            post{
				failure{
					slackSend color: 'danger',channel: "${env.channel}", message: "[grupo2] [Build ${BUILD_NUMBER}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'slack-angelo-channel'
				}
        }            
        }
        stage("CI 2: Testear"){
            environment { STAGE='CI 2: Testear'}
            steps {
                script{
                    sh "echo 'Test Code!'"
                    sh "./mvnw clean test -e"    
                }
            }
            post{
				failure{
					slackSend color: 'danger',channel: "${env.channel}", message: "[grupo2] [Build ${BUILD_NUMBER}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'slack-angelo-channel'
				}
        }    
        }
        stage("CI 3: Build release .Jar"){
            when { branch 'release/*'}
            environment { STAGE="CI 3: Build .Jar" }
            steps {
                script{
                    sh "echo 'Building jar and adding version'"
                    sh "./mvnw  clean package -e versions:set -DnewVersion=${VERSION}"
                }
            }
            post{
				failure{
					slackSend color: 'danger',channel: "${env.channel}", message: "[grupo2] [Build ${BUILD_NUMBER}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'slack-angelo-channel'
				}
        }                
        }   
        stage("CI 3: Build .Jar"){
            when { not { anyOf {branch 'release/*';branch 'main'}} }
            environment { STAGE="CI 3: Build .Jar" }
            steps {
                script{
                    sh "echo 'Building jar'"
                    sh "./mvnw clean package -e"
                }
            }
            post{
				failure{
					slackSend color: 'danger',channel: "${env.channel}", message: "[grupo2] [Build ${BUILD_NUMBER}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'slack-angelo-channel'
				}
        }                
        }        

        stage("CI 4: Análisis SonarQube"){
            environment { STAGE="CI 4: Análisis SonarQube" }
            steps {
                script{
                     withSonarQubeEnv('sonarqube') {
                            sh "echo 'Calling sonar Service in another docker container!'"
                            sh './mvnw clean verify sonar:sonar -Dsonar.projectKey=ms-iclab-grupo2  -Dsonar.projectName=ms-iclab-grupo2 -Dsonar.java.binaries=build'
                        }
                        
                }
            }
            post{
				failure{
					slackSend color: 'danger',channel: "${env.channel}", message: "[grupo2] [Build ${BUILD_NUMBER}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'slack-angelo-channel'
				}
        }                
        }
        stage("CD 1: Subir Artefacto a Nexus"){
            when { branch 'release/*'}
            environment { STAGE="CD 1: Subir Artefacto a Nexus" }
            steps {
                script{
                    nexusPublisher nexusInstanceId: 'nexus',
                        nexusRepositoryId: 'repository_grupo2',
                        packages: [
                            [$class: 'MavenPackage',
                                mavenAssetList: [
                                    [classifier: '',
                                    extension: 'jar',
                                    filePath: "build/DevOpsUsach2020-${VERSION}.jar"
                                ]
                            ],
                                mavenCoordinate: [
                                    artifactId: 'DevOpsUsach2020',
                                    groupId: 'com.devopsusach2020',
                                    packaging: 'jar',
                                    version: "${VERSION}"
                                ]
                            ]
                        ]
                }
            }
            post{
				failure{
					slackSend color: 'danger',channel: "${env.channel}", message: "[grupo2] [Build ${BUILD_NUMBER}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'slack-angelo-channel'
				}
        }                
        }
        stage("CD 2: Descargar Nexus"){
            when { branch 'release/*'}
            environment { STAGE="CD 2: Descargar Nexus" }            
            steps {
                script{
                    sh 'curl -X GET -u admin:$NEXUS_PASSWORD "http://nexus:8081/repository/repository_grupo2/com/devopsusach2020/DevOpsUsach2020/${VERSION}/DevOpsUsach2020-${VERSION}.jar" -O'
                }
            }
            post{
				failure{
					slackSend color: 'danger',channel: "${env.channel}", message: "[grupo2] [Build ${BUILD_NUMBER}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'slack-angelo-channel'
				}
        }                
        }
        stage("CD 3: Levantar Artefacto Jar en server Jenkins"){
            when { branch 'release/*'}
            environment { STAGE="CD 3: Levantar Artefacto Jar en server Jenkins" }            
            steps {
                script{
                sh "nohup java -jar DevOpsUsach2020-${VERSION}.jar & >/dev/null" 
                }
            }
            post{
				failure{
					slackSend color: 'danger',channel: "${env.channel}", message: "[grupo2] [Build ${BUILD_NUMBER}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'slack-angelo-channel'
				}
        }                
        }
        stage("CD 4: Testear Artefacto - Dormir(Esperar 20sg)"){
            when { branch 'release/*'}
            environment { STAGE="CD 4: Testear Artefacto - Dormir(Esperar 20sg)" }            
            steps {
                script{
                    sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
                }
            }
            post{
				failure{
					slackSend color: 'danger',channel: "${env.channel}", message: "[grupo2] [Build ${BUILD_NUMBER}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'slack-angelo-channel'
				}
        }                
        }

        stage("CD 4.1: Testear estadoPais - Chile"){
            when { branch 'release/*'}
            environment { STAGE="CD 4.1: Testear estadoPais - Chile" }            
            steps {
                script{
                    sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/estadoPais?pais=Chile'"
                }
            }
            post{
				failure{
					slackSend color: 'danger',channel: "${env.channel}", message: "[grupo2] [Build ${BUILD_NUMBER}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'slack-angelo-channel'
				}
        }                
        }
        stage("CD 4.2: Testear estadoMundial"){
            when { branch 'release/*'}
            environment { STAGE="CD 4.2: Testear estadoMundial" }            
            steps {
                script{
                    sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/estadoMundial'"
                }
            }
            post{
				failure{
					slackSend color: 'danger',channel: "${env.channel}", message: "[grupo2] [Build ${BUILD_NUMBER}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'slack-angelo-channel'
				}
        }                
        }   

        stage("CD 4.3: Testear Artefacto con newman"){
            when { branch 'release/*'}
            environment { STAGE="CD 4.3: Testear Artefacto con newman" }  
            steps {
                script{
                    sh 'newman run  ejemplo-maven-4pipe.postman_collection.json'
                }                        
                }
            post{
				failure{
					slackSend color: 'danger',channel: "${env.channel}", message: "[grupo2] [Build ${BUILD_NUMBER}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'slack-angelo-channel'
				}                
            }
        }
        stage("CD 5: Detener Atefacto jar en Jenkins server"){
            when { branch 'release/*'}
            environment { STAGE="CD 5: Detener Atefacto jar en Jenkins server" }            
            steps {
                script{
                    sh '''
                        echo 'Process Java .jar: ' $(pidof java | awk '{print $1}')  
                        sleep 20
                        kill -9 $(pidof java | awk '{print $1}')
                    '''
                }
                }
            post{
				failure{
					slackSend color: 'danger',channel: "${env.channel}", message: "[grupo2] [Build ${BUILD_NUMBER}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'slack-angelo-channel'
				}
				success{
					slackSend color: 'good', channel: "${env.channel}", message: "[grupo2] [Build ${BUILD_NUMBER}] Ejecucion Exitosa", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'slack-angelo-channel'
				}
                }
        } 
    }
}