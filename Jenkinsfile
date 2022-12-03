#!groovy​
withEnv(['channel=C04B17VE0JH']) {

    def BRANCH = "${env.BRANCH_NAME.split("/")[1]}"
    def BRANCH_TYPE = "${env.BRANCH_NAME.split("/")[0]}"
    def VERSION = "${env.BRANCH_NAME.split("/")[1]}"
    def NEXUS_PASSWORD = credentials('nexus-password')

    try{

            stage("Inicio"){
                    env.STAGE='Inicio'
                    node {
                        sh "echo 'Se inicia Pipeline'"
                        checkout(
                                    [$class: 'GitSCM',
                                    //Acá reemplazar por el nonbre de branch
                                    branches: [[name: "$env.BRANCH_NAME" ]],
                                    //Acá reemplazar por su propio repositorio
                                    userRemoteConfigs: [[url: 'https://github.com/mserey-org/ms-iclab-grupo2.git']]])
                    }
            }
            stage("CI 1: Compliar"){
                env.STAGE='CI 1: Compliar'
                node {
                    sh "echo 'Compile Code!'"
                    sh "./mvnw clean compile -e"
                }
            }
            stage("CI 2: Testear"){
                env.STAGE='CI 2: Testear'
                node {
                    script {
                        sh "echo 'Test Code!'"
                        // Run Maven on a Unix agent.
                        sh "./mvnw clean test -e"
                    }
                }
            }
            stage("CI 3: Build .Jar"){
                env.STAGE='CI 3: Build .Jar'
                node {
                    script {
                        sh "echo 'Build .Jar!'"
                        // Run Maven on a Unix agent.
                        sh "./mvnw  clean package -e"
                    }            
                }
            }
            stage("CI 4: Análisis SonarQube"){
                env.STAGE='CI 4: Análisis SonarQube'
                node {
                    withSonarQubeEnv('sonarqube') {
                        sh "echo 'Calling sonar Service in another docker container!'"
                        // Run Maven on a Unix agent to execute Sonar.
                        sh './mvnw clean verify sonar:sonar -Dsonar.projectKey=ms-iclab-grupo2 -Dsonar.projectName=ms-iclab-grupo2 -Dsonar.java.binaries=build'
                    }
                }
            } 


        if (env.BRANCH_NAME =~ ".*release/.*") {
             

            stage("CD"){
                env.STAGE='CD'
                node {
                    sh "echo 'Se inicia release $VERSION'"
                }
            }  
            stage("CD 1: Subir Artefacto a Nexus"){
                    env.STAGE='CD 1: Subir Artefacto a Nexus'
                    node {
                        nexusPublisher nexusInstanceId: 'nexus',
                            nexusRepositoryId: 'repository_grupo2',
                            packages: [
                                [$class: 'MavenPackage',
                                    mavenAssetList: [
                                        [classifier: '',
                                        extension: 'jar',
                                        filePath: "build/DevOpsUsach2020-'$VERSION'.jar"
                                    ]
                                ],
                                    mavenCoordinate: [
                                        artifactId: 'DevOpsUsach2020',
                                        groupId: 'com.devopsusach2020',
                                        packaging: 'jar',
                                        version: "'$VERSION'"
                                    ]
                                ]
                            ]                
                    }
                }
                stage("CD 2: Descargar Nexus"){
                    env.STAGE='CD 2: Descargar Nexus'
                    node {
                            sh " curl -X GET -u admin:$NEXUS_PASSWORD 'http://nexus:8081/repository/repository_grupo2/com/devopsusach2020/DevOpsUsach2020/$VERSION/DevOpsUsach2020-'$VERSION'.jar' -O"
                    }
                }
                stage("CD 3: Levantar Artefacto Jar en server Jenkins"){
                    env.STAGE='CD 3: Levantar Artefacto Jar en server Jenkins'
                    node {
                            sh "nohup java -jar DevOpsUsach2020-'$VERSION'.jar & >/dev/null"                
                    }
                }
                stage("CD 4: Testear Artefacto - Dormir(Esperar 20sg) "){
                    env.STAGE='CD 4: Testear Artefacto - Dormir(Esperar 20sg) '
                    node {
                            sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"                
                    }
                }
                stage("CD 5: Detener Atefacto jar en Jenkins server"){
                    env.STAGE='CD 5: Detener Atefacto jar en Jenkins server'
                    node {
                        sh '''
                            echo 'Process Java .jar: ' $(pidof java | awk '{print $1}')  
                            sleep 20
                            kill -9 $(pidof java | awk '{print $1}')
                        '''
                    }
                }
                
        }


        stage("Mensaje slack"){
            node {
                slackSend (color: 'good', channel: "${env.channel}", message: "[grupo2] [${env.JOB_NAME}] [${BUILD_NUMBER}] Ejecucion Exitosa [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'token-slack')
            }
        }        
            
    }

    catch (e) {
            echo 'This will run only if failed'
            slackSend (color: 'danger', channel: "${env.channel}", message: "[grupo2] [${env.JOB_NAME}] [${BUILD_NUMBER}] Ejecucion fallida en stage [${env.STAGE}]", teamDomain: 'devopsusach20-lzc3526', tokenCredentialId: 'token-slack')
            throw e
    }
}

 
